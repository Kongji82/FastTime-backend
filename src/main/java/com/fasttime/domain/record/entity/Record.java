package com.fasttime.domain.record.entity;

import com.fasttime.domain.article.entity.Article;
import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.record.dto.RecordDTO;
import com.fasttime.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Record extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne
    private Member member;

    @JoinColumn(name = "article_id")
    @ManyToOne
    private Article article;

    @Comment("좋아요 = 1, 싫어요 = 0")
    @Column(name = "type")
    private boolean isLike;

    @Builder
    public Record(Long id, Member member, Article article, boolean isLike) {
        this.id = id;
        this.member = member;
        this.article = article;
        this.isLike = isLike;
    }

    public RecordDTO toDTO() {
        return RecordDTO.builder().id(this.id).articleId(this.article.getId())
            .memberId(this.member.getId()).isLike(this.isLike).build();
    }
}
