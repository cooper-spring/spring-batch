package com.cooper.springbatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleJobConfig {

    /**
     *
     * 1. Job
     * (1) 하나의 배치 작업 단위를 말한다.
     * (2) 공식 문서 정의 : 전체 배치 프로세스를 캡슐화한 엔티티
     *
     *
     * 2. Step
     * (1) Job 의 독립된 세부 단계를 의미한다.
     * (2) 공식 문서 정의 : job의 독립적이고 순차적인 단계를 캡슐화한 도메인 객체
     * (3) 하나의 Job은 여러 개의 step으로 구성된다.
     *
     * 3. tasklet
     * (1) step 안에서 수행할 기능을 명시한다.
     * (2) step 은 tasklet 1개 + reader/processor/writer 한 묶음으로 구성되어 있다.
     * (3) Reader & Processor 가 끝나고 Tasklet 으로 마무리하는 것은 금지한다.
     *
     */

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    /**
     * (1) jobBuilderFactory.get("simpleJob") : simpleJob 이름의 Job 을 생성한다.
     * (2) job 의 이름은 별도로 지정하지 않고, 이렇게 builder 를 통해 지정한다.
     */
    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleStep1())
                .build();
    }

    /**
     * (1) stepBuilderFactory.get("simpleStep1") : simpleStep 이름의 step 을 생성한다.
     * (2) step 의 이름은 별도로 지정하지 않고, 이렇게 builder 를 통해 지정한다.
     */
    @Bean
    public Step simpleStep1() {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>> This is Step1");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
