package com.kodilla.csvdateconverter.configuration;

import com.kodilla.csvdateconverter.domain.PersonWithAge;
import com.kodilla.csvdateconverter.domain.PersonWithBirthdate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    FlatFileItemReader<PersonWithBirthdate> reader() {
        FlatFileItemReader<PersonWithBirthdate> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("persons.csv"));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "name", "familyName", "birthdate");

        BeanWrapperFieldSetMapper<PersonWithBirthdate> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(PersonWithBirthdate.class);

        DefaultLineMapper<PersonWithBirthdate> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    PersonProcessor processor() {
        return new PersonProcessor();
    }

    @Bean
    FlatFileItemWriter<PersonWithAge> writer() {
        BeanWrapperFieldExtractor<PersonWithAge> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"id", "name", "familyName", "age"});

        DelimitedLineAggregator<PersonWithAge> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        FlatFileItemWriter<PersonWithAge> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output.csv"));
        writer.setShouldDeleteIfExists(true);
        writer.setLineAggregator(aggregator);

        return writer;
    }

    @Bean
    Step personChange(ItemReader<PersonWithBirthdate> reader,
                      ItemProcessor<PersonWithBirthdate, PersonWithAge> processor,
                      ItemWriter<PersonWithAge> writer) {
        return stepBuilderFactory.get("personChange")
                .<PersonWithBirthdate, PersonWithAge>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    Job changePersonJob(Step personChange) {
        return jobBuilderFactory.get("changePersonJob")
                .incrementer(new RunIdIncrementer())
                .flow(personChange)
                .end()
                .build();
    }

}
