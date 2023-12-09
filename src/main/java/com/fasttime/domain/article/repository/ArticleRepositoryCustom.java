package com.fasttime.domain.article.repository;

import com.fasttime.domain.article.service.usecase.ArticleQueryUseCase.ArticlesSearchRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

public interface ArticleRepositoryCustom {

    List<ArticleQueryResponse> search(ArticlesSearchRequest searchCondition);

    @Getter
    class ArticleQueryResponse {

        private Long id;
        private Long memberId;
        private String nickname;
        private String title;
        private boolean anonymity;
        private int commentCount;
        private int likeCount;
        private int hateCount;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private LocalDateTime deletedAt;
    }
}
