package com.fasttime.global.batch.tasklet;

import com.fasttime.domain.reference.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateDoneCompetitionTasklet implements Tasklet {

    private final CrawlingService crawlingService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws InterruptedException {
        log.info("모집이 끝난 공모전 크롤링 Tasklet 시작");
        crawlingService.updateDoneCompetition();

        return RepeatStatus.FINISHED;
    }
}