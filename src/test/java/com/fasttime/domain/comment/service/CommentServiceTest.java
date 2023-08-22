package com.fasttime.domain.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasttime.domain.comment.dto.CommentDto;
import com.fasttime.domain.comment.dto.request.CreateCommentRequest;
import com.fasttime.domain.comment.dto.request.DeleteCommentRequest;
import com.fasttime.domain.comment.dto.request.UpdateCommentRequest;
import com.fasttime.domain.comment.entity.Comment;
import com.fasttime.domain.comment.exception.NotFoundException;
import com.fasttime.domain.comment.repository.CommentRepository;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.member.repository.MemberRepository;
import com.fasttime.domain.post.entity.Post;
import com.fasttime.domain.post.repository.PostRepository;
import java.util.Optional;
import javax.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("createComment()는 ")
    class Context_createComment {

        @Test
        @DisplayName("비익명으로 댓글을 등록할 수 있다.")
        void nonAnonymous_willSuccess() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            Comment comment = Comment.builder().id(0L).post(post.get()).member(member.get())
                .content("test").anonymity(false).parentComment(null).build();

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            CommentDto CommentDto = commentService.createComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "test", false, null);
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("익명으로 댓글을 등록할 수 있다.")
        void anonymousComment_willSuccess() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(true).parentCommentId(null).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            Comment comment = Comment.builder().id(0L).post(post.get()).member(member.get())
                .content("test").anonymity(true).parentComment(null).build();

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            CommentDto CommentDto = commentService.createComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "test", true, null);
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("비익명으로 대댓글을 등록할 수 있다.")
        void nonAnonymousReply_willSuccess() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(0L).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            Optional<Comment> parentComment = Optional.of(
                Comment.builder().id(0L).post(post.get()).member(member.get()).content("test")
                    .anonymity(false).parentComment(null).build());
            Comment comment = Comment.builder().id(1L).post(post.get()).member(member.get())
                .content("test").anonymity(false).parentComment(parentComment.get()).build();

            given(commentRepository.findById(any(Long.class))).willReturn(parentComment);
            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);
            // when
            CommentDto CommentDto = commentService.createComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(1L, 0L, 0L, "test", false, 0L);
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("익명으로 대댓글을 등록할 수 있다.")
        void anonymousReply_willSuccess() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(true).parentCommentId(0L).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            Optional<Comment> parentComment = Optional.of(
                Comment.builder().id(0L).post(post.get()).member(member.get()).content("test")
                    .anonymity(false).parentComment(null).build());
            Comment comment = Comment.builder().id(1L).post(post.get()).member(member.get())
                .content("test").anonymity(true).parentComment(parentComment.get()).build();

            given(commentRepository.findById(any(Long.class))).willReturn(parentComment);
            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(commentRepository.save(any(Comment.class))).willReturn(comment);

            // when
            CommentDto CommentDto = commentService.createComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(1L, 0L, 0L, "test", true, 0L);
            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).save(any(Comment.class));
        }

        @Test
        @DisplayName("게시물을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void postNotFound_willFail() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            Optional<Post> post = Optional.empty();

            given(postRepository.findById(any(Long.class))).willReturn(post);

            // when, then
            Throwable exception = assertThrows(NotFoundException.class, () -> {
                commentService.createComment(request);
            });
            assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("회원을 찾을 수 없으면 댓글을 등록할 수 없다.")
        void memberNotFound_willFail() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(null).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            member = Optional.empty();

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);

            // when, then
            Throwable exception = assertThrows(NotFoundException.class, () -> {
                commentService.createComment(request);
            });
            assertEquals("존재하지 않는 회원입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, never()).findById(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 대댓글을 등록할 수 없다.")
        void parentCommentNotFound_willFail() {
            // given
            CreateCommentRequest request = CreateCommentRequest.builder().postId(0L).memberId(0L)
                .content("test").anonymity(false).parentCommentId(0L).build();
            Optional<Post> post = Optional.of(Post.builder().id(0L).build());
            Optional<Member> member = Optional.of(Member.builder().id(0L).build());
            Optional<Comment> parentComment = Optional.empty();

            given(postRepository.findById(any(Long.class))).willReturn(post);
            given(memberRepository.findById(any(Long.class))).willReturn(member);
            given(commentRepository.findById(any(Long.class))).willReturn(parentComment);

            // when, then
            Throwable exception = assertThrows(NotFoundException.class, () -> {
                commentService.createComment(request);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

            verify(postRepository, times(1)).findById(any(Long.class));
            verify(memberRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, times(1)).findById(any(Long.class));
            verify(commentRepository, never()).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("getComment()는 ")
    class Context_getComment {

        @Test
        @DisplayName("댓글을 가져올 수 있다.")
        void _willSuccess() {
            // given
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(0L).post(post).member(member).content("test").anonymity(false)
                    .parentComment(null).build());

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            CommentDto CommentDto = commentService.getComment(0L);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "test", false, null);
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 가져올 수 없다.")
        void CommentNotFound_willFail() {
            // given
            Optional<Comment> comment = Optional.empty();

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when, then
            Throwable exception = assertThrows(NotFoundException.class, () -> {
                commentService.getComment(0L);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

            verify(commentRepository, times(1)).findById(any(Long.class));
        }
    }

    @Nested
    @DisplayName("updateComment()는 ")
    class Context_updateComment {

        @Test
        @DisplayName("댓글을 수정할 수 있다.")
        void _willSuccess() {
            // given
            UpdateCommentRequest request = UpdateCommentRequest.builder().id(0L).content("modified")
                .build();
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(0L).post(post).member(member).content("test").anonymity(false)
                    .parentComment(null).build());

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            CommentDto CommentDto = commentService.updateComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "modified", false, null);
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 가져올 수 없다.")
        void CommentNotFound_willFail() {
            // given
            UpdateCommentRequest request = UpdateCommentRequest.builder().id(0L).content("modified")
                .build();
            Optional<Comment> comment = Optional.empty();

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when, then
            Throwable exception = assertThrows(NotFoundException.class, () -> {
                commentService.updateComment(request);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

            verify(commentRepository, times(1)).findById(any(Long.class));
        }
    }

    @Nested
    @DisplayName("deleteComment()는 ")
    class Context_deleteComment {

        @Test
        @DisplayName("댓글을 삭제할 수 있다.")
        void _willSuccess() {
            // given
            DeleteCommentRequest request = DeleteCommentRequest.builder().id(0L).build();
            Post post = Post.builder().id(0L).build();
            Member member = Member.builder().id(0L).build();
            Optional<Comment> comment = Optional.of(
                Comment.builder().id(0L).post(post).member(member).content("test").anonymity(false)
                    .parentComment(null).build());

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when
            CommentDto CommentDto = commentService.deleteComment(request);

            // then
            assertThat(CommentDto).extracting("id", "postId", "memberId", "content", "anonymity",
                "parentCommentId").containsExactly(0L, 0L, 0L, "test", false, null);
            verify(commentRepository, times(1)).findById(any(Long.class));
        }

        @Test
        @DisplayName("댓글을 찾을 수 없으면 댓글을 삭제할 수 없다.")
        void CommentNotFound_willFail() {
            // given
            DeleteCommentRequest request = DeleteCommentRequest.builder().id(0L).build();
            Optional<Comment> comment = Optional.empty();

            given(commentRepository.findById(any(Long.class))).willReturn(comment);

            // when, then
            Throwable exception = assertThrows(NotFoundException.class, () -> {
                commentService.deleteComment(request);
            });
            assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());

            verify(commentRepository, times(1)).findById(any(Long.class));
        }
    }
}
