package com.fasttime.domain.review.service;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.exception.MemberNotFoundException;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.review.dto.request.ReviewRequestDTO;
import com.fasttime.domain.review.dto.response.ReviewResponseDTO;
import com.fasttime.domain.review.entity.Review;
import com.fasttime.domain.review.entity.ReviewTag;
import com.fasttime.domain.review.entity.Tag;
import com.fasttime.domain.review.exception.ReviewAlreadyDeletedException;
import com.fasttime.domain.review.exception.ReviewAlreadyExistsException;
import com.fasttime.domain.review.exception.ReviewNotFoundException;
import com.fasttime.domain.review.exception.TagNotFoundException;
import com.fasttime.domain.review.exception.UnauthorizedAccessException;
import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.domain.review.repository.ReviewTagRepository;
import com.fasttime.domain.review.repository.TagRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;

    public Review createReview(ReviewRequestDTO requestDTO, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(MemberNotFoundException::new);
        if (!member.isCampCrtfc()) {
            throw new UnauthorizedAccessException();
        }

        Review existingReview = reviewRepository.findByMemberId(memberId);
        if (existingReview != null) {
            if (existingReview.isDeleted()) {
                reviewTagRepository.deleteByReview(existingReview);
                updateReviewDetails(existingReview, requestDTO);
                updateReviewTags(existingReview, requestDTO);
                existingReview.restore();
                return reviewRepository.save(existingReview);
            } else {
                throw new ReviewAlreadyExistsException();
            }
        }

        Review newReview = requestDTO.createReview(member);
        updateReviewTags(newReview, requestDTO);
        return reviewRepository.save(newReview);
    }

    public ReviewResponseDTO createAndReturnReviewResponse(ReviewRequestDTO requestDTO,
        Long memberId) {
        Review review = createReview(requestDTO, memberId);

        Set<String> goodTagContents = getTagContents(requestDTO.goodtags());
        Set<String> badTagContents = getTagContents(requestDTO.badtags());

        return ReviewResponseDTO.of(review, goodTagContents, badTagContents);
    }

    private Set<String> getTagContents(Set<Long> tagIds) {
        return tagIds.stream()
            .map(tagId -> tagRepository.findById(tagId)
                .map(Tag::getContent)
                .orElseThrow(TagNotFoundException::new))
            .collect(Collectors.toSet());
    }

    public void deleteReview(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(ReviewNotFoundException::new);
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
        if (review.isDeleted()) {
            throw new ReviewAlreadyDeletedException();
        }

        review.softDelete();
        reviewRepository.save(review);
    }

    public Review updateReview(Long reviewId, ReviewRequestDTO requestDTO, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(ReviewNotFoundException::new);
        if (!review.getMember().getId().equals(memberId)) {
            throw new UnauthorizedAccessException();
        }
        reviewTagRepository.deleteByReview(review);

        updateReviewDetails(review, requestDTO);
        updateReviewTags(review, requestDTO);
        return reviewRepository.save(review);
    }

    private void updateReviewDetails(Review review, ReviewRequestDTO requestDTO) {
        review.updateReviewDetails(requestDTO.title(), requestDTO.rating(), requestDTO.content());
    }

    private void updateReviewTags(Review review, ReviewRequestDTO requestDTO) {
        Set<ReviewTag> newReviewTags = new HashSet<>();
        newReviewTags.addAll(createReviewTags(requestDTO.goodtags(), review, true));
        newReviewTags.addAll(createReviewTags(requestDTO.badtags(), review, false));
        review.setReviewTags(newReviewTags);
    }

    private Set<ReviewTag> createReviewTags(Set<Long> tagIds, Review review, boolean isGoodTag) {
        return tagIds.stream()
            .map(tagId -> tagRepository.findById(tagId)
                .orElseThrow(TagNotFoundException::new))
            .map(tag -> new ReviewTag(null, review, tag, isGoodTag))
            .collect(Collectors.toSet());
    }

    public ReviewResponseDTO updateAndReturnReviewResponse(Long reviewId,
        ReviewRequestDTO requestDTO, Long memberId) {
        Review updatedReview = updateReview(reviewId, requestDTO, memberId);
        Set<String> goodTagContents = getTagContents(requestDTO.goodtags());
        Set<String> badTagContents = getTagContents(requestDTO.badtags());
        return ReviewResponseDTO.of(updatedReview, goodTagContents, badTagContents);
    }
}
