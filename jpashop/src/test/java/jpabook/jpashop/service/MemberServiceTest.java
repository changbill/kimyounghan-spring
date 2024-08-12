package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)  // 스프링과 연관된 테스트라는 어노테이션
@SpringBootTest     // 스프링 부트를 띄운 상태로 테스트하고 싶을 때 사용
@Transactional      // 테스트에서 트랜잭션을 사용하면 데이터베이스에 커밋되지 않고 롤백된다.
class MemberServiceTest {

    // test 코드에서는 다른 곳에서 상속받고 쓰는 일이 없으므로 가장 간편한 주입방법을 사용하는 것이 좋다!
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");
        // when
        Long savedId = memberService.join(member);

        // then
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");
        // when
        memberService.join(member1);
        // then
        /**
         * Junit5에서는 assertThrows로 예외를 테스트한다.
         * assertThrows에서 예외 메세지까지 테스트하고 싶다면
         * Throwable을 사용하여 assertEquals로 테스트하면 된다.
          */
        Throwable exception = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertEquals("이미 존재하는 회원입니다.", exception.getMessage());
    }
}