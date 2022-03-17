package com.cooper.springbatch.job;

import com.cooper.springbatch.job.domain.Pay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcCursorItemReaderJobConfig {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    private static final int CHUNK_SIZE = 10;

    @Bean
    public Job jdbcCursorItemReaderJob() {
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")
                .start(jdbcCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep() {
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")
                .<Pay, Pay>chunk(CHUNK_SIZE) // <Reader 에서 반환할 타입, Writer 에서 파라미터로 넘어올 타입>
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemWriter())
                .build();
    }

    /**
     * CursorItemReader
     * - Streaming 으로 데이터를 처리하는 리더
     * - 예시) Database와 어플리케이션 사이에 통로를 하나 연결하고 하나씩 데이터를 빨아들인다고 생각하기
     * - reader는 Tasklet이 아니기 때문에 reader 만으로는 수행될 수 없다.
     * - 주의!) Database 와 SocketTimeout을 충분히 큰 값으로 설정해야만 한다.
     * - 만약에 Batch 수행시간이 오래 걸리는 경우에는 PagingItemReader 를 사용하자.
     *     (Conenction을 맺고 끊기 때문에 많은 데이터에 대한 타입아웃과 부하가 없다.)
     */
    @Bean
    public JdbcCursorItemReader<Pay> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Pay>()
                .fetchSize(CHUNK_SIZE) // fetchSize : 데이터를 한번에 가져올 데이터 양 (paging과 차이 : 분할 처리가 없다.)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Pay.class))
                .sql("SELECT id, amount, tx_name, tx_date_time FROM pay") // Reader 로 사용할 쿼리문을 사용하기
                .name("jdbcCursorItemReader")
                .build();
    }

    @Bean
    public ItemWriter<Pay> jdbcCursorItemWriter() {
        return list -> {
            for (Pay pay : list) {
                log.info("current pay = {}", pay);
            }
        };
    }

}
