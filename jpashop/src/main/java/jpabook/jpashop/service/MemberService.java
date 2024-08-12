package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// spring 프레임워크에서 트랜잭션 어노테이션을 사용하라. 활용할 수 있는 기능이 더 많다.
// 같은 어노테이션이 클래스, 메소드 단에 적용되어 있을 경우 세부적인 쪽이 적용된다. 즉, 메소드 어노테이션이 적용된다.
// 조회 기능만 있는 메소드의 경우 readOnly = true 옵션을 써주면 성능을 향상시킬 수 있다.
@Transactional(readOnly = true)
@RequiredArgsConstructor    // final이 적용되어있는 필드만 생성자로 만들어줌, 전부 다 생성자로 만드는 어노테이션은 @AllArgsConstructor
public class MemberService {

    // Repository는 변경할 일이 없고, 생성자에서 주입을 시켰는지 이중으로 확인할 수 있어 final을 권장
    private final MemberRepository memberRepository;

    /**
     * 생성자 injection의 장점
     * setter injection같이 중간에 바뀔 위험이 없음.
     * 필드 injection와 달리 중간에 인스턴스를 바꿔야 할 때 바꿀 수 있음
     * 최근엔 생성자 injection이 기본이어서 @Autowired를 작성해주지 않아도 생성자가 있다면
     * 기본적으로 생성자 injection 해줌.
     */
//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        ValidateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void ValidateDuplicateMember(Member member) {
        // EXCEPTION
        // 멀티쓰레드 상황에는 이 중복 회원 검증 메서드가 적용 안될 경우도 있다.
        // 따라서 데이터베이스에도 member name에 unique 제약을 이중으로 걸어둬 안전하게 하는 것이 좋다.
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
