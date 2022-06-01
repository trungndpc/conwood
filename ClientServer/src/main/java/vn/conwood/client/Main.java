package vn.conwood.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaRepositories(basePackages = {"vn.conwood.jpa"})
@EntityScan(basePackages = {"vn.conwood.jpa"})
@ComponentScan(basePackages = "vn.conwood")
@SpringBootApplication
@EnableScheduling
public class Main  {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
