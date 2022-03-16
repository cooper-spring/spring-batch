package com.cooper.springbatch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
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
public class StepConditionalJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;


    /**
     * step1 성공 시나리오 : step1 -> step3
     * step1 실패 시나리오 : step1 -> step2 -> step3
     */


    /**
     * [on & to & from & end]
     * 1. on()
     * - 캐치할 ExitStatus 지정
     * - * : 모든 ExistStatus 가 지정
     *
     *
     * 2. to()
     * - 다음으로 이동할 Step 지정
     *
     *
     * 3. from()
     * - 상태 값을 보고 일치하는 상태라면 to() 포함된 step 메서드를 호출한다.
     * - step1의 이벤트 캐치가 FAILED 로 되어 있는 상태에서 추가로 이벤트 캐치하려면 from 을 써야만 함.
     *
     * 4. end()
     * - FlowBuilder 를 반환하는 end 와 FlowBuilder 를 종료하느 end 2개가 있음.
     * - on(*) 뒤에 있는 end : FlowBuilder 를 반환하는 end
     * - build() 앞에 있는 end : FlowBuilder 를 종료하는 end
     * - FlowBuilder 를 반환하는 end 사용시 계속해서 from 을 이어갈 수 있음.
     *
     */

    /**
     * [ Batch Status vs Exit Status ]
     * 1. BatchStatus
     * - Job 또는 Step 의 실핼 결과를 Spring 에서 기록하는 사용하는 Enum
     *
     * 2. ExitStatus
     * - Step 실행 후 상태
     *
     */
    @Bean
    public Job stepNextConditionalJob() {
        return jobBuilderFactory.get("stepNextConditionalJob")
                .start(conditionalJobStep1())
                .on("FAILED") // ExitStatus 가 FAILED 일 경우
                .to(conditionalJobStep3()) // step3 으로 이동한다.
                .on("*") // step3 의 결과 관계없이
                .end() //step3으로 이동하면 Flow 를 종료한다

                .from(conditionalJobStep1()) // step1 로 부터
                .on("*") // FAILED 외에 모든 경우 (앞에서 FAILED 를 필터링 했기 때문에)
                .to(conditionalJobStep2()) // step2로 이동한다.
                .next(conditionalJobStep3()) // step2가 정상 종료되면 step3 으로 이동한다.
                .on("*") //step3 의 결과 관계없이
                .end()// step3 으로 이동하면 Flow 가 종료한다.

                .from(conditionalJobStep2()) // step2 가
                .on("FAILED")// ExitStatus == FAILED 일 경우,
                .to(conditionalJobStep4()) // step4 번으로 이동한다.
                .on("*")// step4 의 결과에 관계없이
                .end() // step4로 이동하면 Flow를 종료한다.

                .end() // Job 종료
                .build();
    }

    @Bean
    public Step conditionalJobStep1() {
        return stepBuilderFactory.get("conditionalJobStep1")
                .tasklet((((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step1");

                    /**
                     * ExitStatus를 FAILED로 지정한다.
                     * 해당 status를 보고 flow가 진행된다.
                     */
//                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }))).build();
    }

    @Bean
    public Step conditionalJobStep2() {
        return stepBuilderFactory.get("conditionalJobStep2")
                .tasklet((contribution, chunkContext) -> {
//                    contribution.setExitStatus(ExitStatus.FAILED);
                    log.info(">>>>> This is stepNextConditionalJob Step2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalJobStep3() {
        return stepBuilderFactory.get("conditionalJobStep3")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step conditionalJobStep4() {
        return stepBuilderFactory.get("conditionalJobStep4")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step4");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
