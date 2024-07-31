package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class TxLevelTest {

    @Autowired LevelService service;

    @Test
    void orderTest() {
        service.write();
        service.read();
    }


    @TestConfiguration
    static class TxLevelTestConfig {
        @Bean
        LevelService levelService() {
            return new LevelService();
        }
    }

    /**
     * @Transactional
     * 트랜잭션 어노테이션이 class 레벨과 메소드 레벨에 동시에 있다면
     * 메소드 레벨의 트랜잭션 우선순위가 더 높다.
     * 따라서, 아래의 예제에서 write() 메서드의 readOnly는 false가 된다.
     *
     * 스프링에서 우선순위는 항상 더 구체적이고 자세한 것이 높은 우선순위를 가진다.
     *
     * 인터페이스에서도 @Transactional 사용가능하다.
     * 우선순위는 구현체에서 인터페이스를 상속받다보니
     * 1. class 메소드
     * 2. class 타입
     * 3. 인터페이스 메소드
     * 4. 인터페이스 타입
     * 순서가 된다.
     *
     * 하지만 인터페이스에 @Transactional 사용을 권장하지 않는다.
     * Spring AOP가 적용 안되는 경우가 있기 때문이다. 가급적 구현 클래스에 @Transactional을 사용하자.
     */
    @Slf4j
    @Transactional(readOnly = true)
    static class LevelService {

        @Transactional(readOnly = false)
        public void write() {
            log.info("call write");
            printTxInfo();
        }

        public void read() {
            log.info("call read");
            printTxInfo();
        }

        public void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly={}", readOnly);
        }
    }
}
