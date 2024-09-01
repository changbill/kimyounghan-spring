package study.data_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing		// Auditing(공통 속성 만들기) 위한 어노테이션
@SpringBootApplication
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	// TODO : Spring Security에서 현재 사용자 session에서 뽑아와서 그 아이디를 넣어줄 예정
	@Bean
	public AuditorAware<String> auditorProvider() {
		// Security에서 아이디 받아올것
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
