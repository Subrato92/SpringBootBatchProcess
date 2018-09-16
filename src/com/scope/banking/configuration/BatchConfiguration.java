package com.scope.banking.configuration;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.scope.banking.models.usrLogin;
import com.scope.banking.processors.UserItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jbBuildFac;
	
	@Autowired
	public StepBuilderFactory StpBuildFac;
	
	@Autowired
	public DataSource datSrc;
	
	@Bean
	public DataSource datSource(){
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost:5432/SCBdB");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres");
		
		return dataSource;
	}
	
	@Bean
	public JdbcCursorItemReader<usrLogin> reader(){
		JdbcCursorItemReader<usrLogin> reader = new JdbcCursorItemReader<usrLogin>();
		reader.setDataSource(datSrc);
		reader.setSql("SELECT * FROM scope_user");
		reader.setRowMapper( new UserRowMapping()); 
		
		return reader;
	}
	
	
	public class UserRowMapping implements RowMapper<usrLogin>{
		
		@Override
		public usrLogin mapRow(ResultSet rs, int rowNum) throws SQLException {
			usrLogin usr = new usrLogin();
			usr.setName(rs.getString("u_name"));
			usr.setPass(rs.getString("password"));
			System.out.println(usr.getName());
			return usr;
		}
	}
	
	@Bean
	public UserItemProcessor processor(){
		return new UserItemProcessor();
	}
	
	@Bean
	public FlatFileItemWriter<usrLogin> writer(){
		System.out.println("file Searching....");
		FlatFileItemWriter<usrLogin> writer = new FlatFileItemWriter<usrLogin>();
		System.out.println("file Searching...");
		writer.setResource(new FileSystemResource("usrLogin.txt"));
		System.out.println("File Found");
		writer.setLineAggregator(new DelimitedLineAggregator<usrLogin>() {{
			setDelimiter(",");
			setFieldExtractor(new BeanWrapperFieldExtractor<usrLogin>(){{
				setNames(new String[] { "name", "pass"});
			}});
		}});
		
		return writer;
	}
	
	@Bean
	 public Step step1() {
		return StpBuildFac.get("step1").<usrLogin, usrLogin> chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	 }
	 
	 @Bean
	 public Job exportUserJob() {
	  return jbBuildFac.get("exportUserJob")
	    .incrementer(new RunIdIncrementer())
	    .flow(step1())
	    .end()
	    .build();
	 }
	
	
}
