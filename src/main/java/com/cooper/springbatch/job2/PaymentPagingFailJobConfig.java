package com.cooper.springbatch.job2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import static com.cooper.springbatch.job2.PaymentPagingFailJobConfig.JOB_NAME;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "job.name", havingValue = JOB_NAME)
public class PaymentPagingFailJobConfig {

    public static final String JOB_NAME = "payPagingFailJob";

    private final EntityManagerFactory entityManagerFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    private final int chunkSize = 10;

    @Bean
    public Job paymentPagingJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(paymentPagingStep())
                .build();
    }

    private Step paymentPagingStep() {
        return stepBuilderFactory.get("paymentPagingStep")
                .<Payment, Payment>chunk(chunkSize)
                .reader(paymentPagingReader())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Payment> paymentPagingReader() {
        return new JpaPagingItemReaderBuilder<Payment>()
                .queryString("SELECT p FROM Payment p WHERE p.successStatus = false")
                .pageSize(chunkSize)
                .entityManagerFactory(entityManagerFactory)
                .name("paymentPagingReader")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Payment, Payment> paymentPagingProcessor() {
        return item -> {
            item.success();
            return item;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<Payment> writer() {
        JpaItemWriter<Payment> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

}
