package hello.springtx.exception;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RollbackTest {

    @Autowired RollbackService service;

    /**
     * runTimeException()
     * 내용
     * Getting transaction for [...RollbackService.runtimeException]
     * call runtimeException
     * Completing transaction for [...RollbackService.runtimeException]
     * 여기서
     */
    @Test
    void runtimeException() {
        Assertions.assertThatThrownBy(()-> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkedExcpetion() {
        Assertions.assertThatThrownBy(()->service.checkedException())
                .isInstanceOf(MyException.class);
    }

    @Test
    void rollbackFor() {
        Assertions.assertThatThrownBy(()->service.rollbackFor())
                .isInstanceOf(MyException.class);
    }

    @TestConfiguration
    static class RollbackTestConfig {

        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {

        // 1. 런타임 예외 발생: 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 2. 체크 예외 발생: 커밋
        @Transactional
        public void checkedException() throws Exception {
            log.info("call checkedException");
            throw new MyException();
        }

        // 3. 체크 예외 rollbackFor 지정: 롤백
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call rollbackFor");
            throw new MyException();
        }
    }

    static class MyException extends Exception{

    }
}
