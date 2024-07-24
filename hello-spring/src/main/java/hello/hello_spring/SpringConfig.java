package hello.hello_spring;

import hello.hello_spring.aop.TimeTraceAop;
import hello.hello_spring.repository.*;
import hello.hello_spring.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    // Jdbc
//    private DataSource dataSource;
//
//    @Autowired  // spring container에서 자동으로 DI 해준다.
//    public SpringConfig(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    // Jpa
//    private EntityManager em;
//
//    @Autowired
//    public SpringConfig(EntityManager em) {
//        this.em = em;
//    }

    // Spring Data Jpa
    private MemberRepository memberRepository;

    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }

//    @Bean
//    public MemberRepository memberRepository() {
    // memory 사용(RDB 사용 X)
//        return new MemoryMemberRepository();
    // jdbc 사용
//        return new JdbcMemberRepository(dataSource);
    // jdbc template 사용
//        return new JdbcTemplateMemberRepository(dataSource);
    // jpa 사용
//        return new JpaMemberRepository(em);
//    }

    // Spring data jpa 사용
    // 스프링 데이터 JPA가 SpringDataJpaMemberRepository 를 스프링 빈으로 자동 등록해준다.
}
