package com.scope.banking.util;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages="com.scope.banking.*", exclude=DataSourceAutoConfiguration.class)
@EnableBatchProcessing
public class Main {
	public static void main(String[] args){		
		SpringApplication.run(Main.class, args);
	}
}
