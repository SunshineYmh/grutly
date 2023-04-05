package com.vastly.affairs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;



@SpringBootApplication(exclude= DataSourceAutoConfiguration.class)
@ComponentScan(basePackages={"com.vastly"})
@MapperScan("com.vastly.ymh.mybatis.affairs.dao")
@EnableAsync
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication sApp= new SpringApplication(StartApplication.class);
		sApp.run(args);
	}

}
