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
public class StepNextJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Tip. 원하는 JOB만 실행하는 방법
     * 1. 현재 상태로 application 을 실행할 경우, SimpleJobConfig.simpleJob 까지 실행한다.
     * 2. 이를 해결하기 위해서 application.properties 에 아래왕 같이 설저하도록 하자.
     *  >>> spring.batch.job.names= ${job.name:NONE}
     *  - run configuration 의 program arguments 로 --job.name=stepNextJob 를 할당하면
     *  - 원하는 jobName 가 있을 경우, 해당 job을 실행
     *  - 원하는 jobName 이 존재하지 않을 경우, NONE을 반환
     *
     *  @ref https://jojoldu.tistory.com/328?category=902551
     */

    /*
     * 1. next() : step을 순차적으로 연결시키는 메서드
     */

    @Bean
    public Job stepNextJob(){
        return jobBuilderFactory.get("stepNextJob")
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(((contribution, chunkContext) ->{
                    log.info(">>>> This is step1");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is step2");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">>>> This is step3");
                    return RepeatStatus.FINISHED;
                })).build();
    }

}
