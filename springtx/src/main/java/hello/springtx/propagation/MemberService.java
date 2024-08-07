package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    @Transactional
    public void joinV1(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("--memberRepository 호출 시작--");
        memberRepository.save(member);
        log.info("--memberRepository 호출 종료--");

        log.info("--logRepository 호출 시작");
        logRepository.save(logMessage);
        log.info("--logRepository 호출 종료");
    }

    /**
     * try catch로 예외 받아서 정상 흐름으로 돌려놓고자 함.
     * 하지만 내부 트랜잭션에서 오류가 발생해 스프링 AOP에 RollbackOnly를 등록한다.
     * 그 결과 겉으로 봤을 때는 try catch로 예외를 해결한 것처럼 보이지만 스프링 AOP 내부에서는 Rollback하라고 남아있어
     * UnexpectedRollbackException을 던지면서 전체 롤백된다.
     */
    @Transactional
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("--memberRepository 호출 시작--");
        memberRepository.save(member);
        log.info("--memberRepository 호출 종료--");

        log.info("--logRepository 호출 시작--");
        try {
            logRepository.save(logMessage);
        } catch(RuntimeException e) {
            log.info("log 저장에 실패했습니다. logMessage={}", logMessage.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("--logRepository 호출 종료--");
    }
}
