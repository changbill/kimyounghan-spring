package hello.hello_spring.repository;

import hello.hello_spring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {
// interface가 상속받을 경우 extends.
    // interface는 다중 상속이 가능하다.

    // findByName() , findByEmail() 처럼 메서드 이름 만으로 조회 기능 제공
    @Override
    Optional<Member> findByName(String name);
}
