package com.cooper.springbatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DeciderJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;


    /**
     * 1. flow
     * -  startStep -> oddDecider 에서 홀수 인지 짝수인지 구분 -> oddStep or evenStep 진행
     *
     * 2. JobExecutionDecider
     * - Step 들의 Flow 속에서 분기만 담당하는 타입
     *
     * 2-1. Decider 의 장점
     * - Step 이 담당하는 역할을 분리할 수 있다. (로직 + 분기처리 -> 로직 / 분기처리)
     * - 다양한 분기 로직을 구현할 수 있다. ( Listener 를 생성하고 JobFlow 에 등록하는 번거로움이 없다.)
     */

    @Bean
    public Job deciderJob() {
        return jobBuilderFactory.get("deciderJob")
                .start(startStep())
                .next(decider())
                .from(decider())
                .on("ODD")
                .to(oddStep())
                .from(decider())
                .on("EVEN")
                .to(evenStep())
                .end()
                .build();
    }

    @Bean
    public Step startStep() {
        return stepBuilderFactory.get("startStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is startStep!");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step evenStep() {
        return stepBuilderFactory.get("evenStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is evenStep!");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step oddStep() {
        return stepBuilderFactory.get("oddStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is oddStep!");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();
    }

    private static class OddDecider implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            Random random = new Random();

            int randomNumber = random.nextInt(50) + 1;
            log.info("randomNumber : {}", randomNumber);

            if (randomNumber % 2 == 0) {
                return new FlowExecutionStatus("EVEN");
            }
            return new FlowExecutionStatus("ODD");
        }
    }

}
