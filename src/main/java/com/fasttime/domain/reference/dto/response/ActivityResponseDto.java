package com.fasttime.domain.reference.dto.response;

import com.fasttime.domain.reference.entity.Activity;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ActivityResponseDto(
    long id,
    String title,
    String organization,
    String corporateType,
    String participate,
    LocalDate startDate,
    String endDate,
    String period,
    int recruitment,
    String area,
    String preferredSkill,
    String homepageUrl,
    String activityBenefit,
    String bonusBenefit,
    String description,
    String imageUrl
) {

    public static ActivityResponseDto of(Activity activity) {
        return ActivityResponseDto.builder()
            .id(activity.getId())
            .title(activity.getTitle())
            .organization(activity.getOrganization())
            .corporateType(activity.getCorporateType())
            .participate(activity.getParticipate())
            .startDate(activity.getStartDate())
            .endDate(activity.getEndDate())
            .period(activity.getPeriod())
            .recruitment(activity.getRecruitment())
            .area(activity.getArea())
            .preferredSkill(activity.getPreferredSkill())
            .homepageUrl(activity.getHomepageUrl())
            .activityBenefit(activity.getActivityBenefit())
            .bonusBenefit(activity.getBonusBenefit())
            .description(activity.getDescription())
            .imageUrl(activity.getImageUrl())
            .build();
    }
}
