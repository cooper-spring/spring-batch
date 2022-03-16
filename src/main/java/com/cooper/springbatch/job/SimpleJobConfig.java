package com.cooper.springbatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
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
                .start(simpleStep1(null))
                .next(simpleStep2(null))
                .build();
    }

    /**
     * (1) stepBuilderFactory.get("simpleStep1") : simpleStep 이름의 step 을 생성한다.
     * (2) step 의 이름은 별도로 지정하지 않고, 이렇게 builder 를 통해 지정한다.
     */
    @Bean
    @JobScope
    public Step simpleStep1(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep1")
                .tasklet((contribution, chunkContext) -> {
//                    throw new IllegalArgumentException("step1에서 실패");
                    log.info(">>>> This is Step1");
                    log.info(">>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                }).build();
    }

    /**
     * 1. BATCH_JOB_INSTANCE & BATCH_JOB_EXECUTION
     * (1) BATCH_JOB_INSTANCE(부모) 와 BATCH_JOB_EXECUTION(자식)은 부모-자식 관계이다.
     * (2) BATCH_JOB_EXECUTION 은 JOB_INSTANCE 의 성공/실패 모든 내역을 기록한다.
     * (3) 동일한 Job Parameter 로 한번 성공했을 경우, 다시 재수행하지 않는다.
     *  - 실패 -> 성공 (O) / 성공 -> 성공 (X)
     *
     * 2. Job, JOB_INSTANCE, JOB_EXECUTION in sampleJobConfig
     * (1) Job : 해당 프로젝트에서는 simpleJob
     * (2) JobInstance : Job Parameter를 requestDate=20220320 로 실행한 simpleJob (Job Parameter로 생성)
     * (3) JobExecution : Job Parameter를 requestDate=20220320 로 실행한 simpleJob 의 1번째 시도 혹은 다음 시도
     */
    @Bean
    @JobScope
    public Step simpleStep2(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return stepBuilderFactory.get("simpleStep2")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is Step2");
                    log.info(">>>> requestDate = {}", requestDate);
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
