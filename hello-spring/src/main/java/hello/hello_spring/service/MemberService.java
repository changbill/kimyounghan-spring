package hello.hello_spring.service;

import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.MemberRepository;
import hello.hello_spring.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Service는 비지니스에 가깝게 이름을 설계한다.
// 개발자가 아닌 기획자들도 알아볼 수 있게 하기 위함
// 반면 Repository는 기계적으로 이름을 설계한다.
//@Service
@Transactional
// 스프링은 해당 클래스의 메서드를 실행할 때 트랜잭션을 시작하고, 메서드가 정상 종료되면 트랜잭션을 커밋한다.
// 만약 런타임 예외가 발생하면 롤백한다.
// JPA를 통한 모든 데이터 변경은 트랜잭션 안에서 실행해야 한다.
public class MemberService {

    private final MemberRepository memberRepository;

//    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * 회원가입
     */
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId) {
        return memberRepository.findById(memberId);
    }

    private void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }
}
