package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 트랜잭션이 이미 진행중인데 추가로 트랜잭션을 수행할 때 어떻게 동작할지 결정하는 것을 트랜잭션 전파라 한다.
 */
@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config {

        /**
         * txManager에 들어가는 객체는 디폴트가 자동 생성인데 Bean 등록이 되어있다면 Bean이 들어옴
         * DataSourceTransactionManager를 스프링 빈으로 등록했으므로
         * PlatformTransactionManager를 주입 받으면 DataSourceTransactionManager가 주입됨
         */
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }


    @Test
    void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        txManager.commit(status);
        log.info("트랜잭션 커밋 완료");
    }


    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        txManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
    }


    /**
     * Acquired Connection [HikariProxyConnection@1064414847 wrapping conn0] for JDBC transaction
     * 트랜잭션1을 시작하고, 커넥션 풀에서 conn0 커넥션을 획득했다.
     *
     * Releasing JDBC Connection [HikariProxyConnection@1064414847 wrapping conn0] after transaction
     * 트랜잭션1을 커밋하고, 커넥션 풀에 conn0 커넥션을 반납했다.
     *
     * Acquired Connection [HikariProxyConnection@ 778350106 wrapping conn0] for JDBC transaction
     * 트랜잭션2을 시작하고, 커넥션 풀에서 conn0 커넥션을 획득했다.
     *
     * Releasing JDBC Connection [HikariProxyConnection@ 778350106 wrapping conn0] after transaction
     * 트랜잭션2을 커밋하고, 커넥션 풀에 conn0 커넥션을 반납했다.
     *
     * 로그를 보면 트랜잭션1과 트랜잭션2가 같은 conn0 커넥션을 사용중이다.
     * 트랜잭션1은 conn0 커넥션을 모두 사용하고 커넥션 풀에 반납까지 완료했다.
     * 이후에 트랜잭션2가 conn0 를 커넥션 풀에서 획득한 것이므로 둘은 완전히 다른 커넥션이다.
     *
     * 히카리 커넥션 풀에서 커넥션을 획득하면 실제 커넥션을 그대로 반환하는 것이 아니라
     * 내부 관리를 위해 실제 커넥션이 포함되어 있는 히카리 프록시 커넥션이라는 객체를 생성해서 반환한다.
     * 이 객체의 주소를 확인하면 커넥션 풀에서 획득한 커넥션을 구분할 수 있다.
     *
     * 결과적으로 conn0 을 통해 커넥션이 재사용 된 것을 확인할 수 있고, HikariProxyConnection@1064414847 ,
     * HikariProxyConnection@778350106 을 통해 각각 커넥션 풀에서 커넥션을 조회한 것을 확인할 수 있다.
     */
    @Test
    void double_commit() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋");
        txManager.commit(tx2);
    }


    /**
     * 트랜잭션이 각각 수행되면서 사용되는 DB 커넥션도 각각 다르다
     * 이 경우 트랜잭션을 각자 관리하기 때문에 전체 트랜잭션을 묶을 수 없다.
     *
     * 전체 트랜잭션을 묶지 않고 각각 관리했기 때문에, 트랜잭션1에서 저장한 데이터는 커밋되고, 트랜잭션2에서 저
     * 장한 데이터는 롤백된다.
     */
    @Test
    void double_commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        txManager.rollback(tx2);
    }


    /**
     * 내부, 외부 커밋
     *
     * 트랜잭션이 수행중인데 트랜잭션을 추가로 수행했다.
     * 이 때 수행중인 트랜잭션이 외부 트랜잭션, 추가로 수행한 트랜잭션이 내부 트랜잭션이다.
     * 내부 트랜잭션은 외부 트랜잭션에 참여한다.
     * 참여한다는 뜻은 내부 트랜잭션이 외부 트랜잭션을 그대로 이어받아서 따른다는 뜻이다.
     * 그리고 외부에서 시작된 물리적인 트랜잭션의 범위가 내부 트랜잭션까지 넓어진다는 뜻이다.
     *
     * 외부 트랜잭션은 처음 수행된 트랜잭션이다. 이 경우 신규 트랜잭션( isNewTransaction=true )이 된다.
     * 내부 트랜잭션은 이미 진행중인 외부 트랜잭션에 참여한다. 이 경우 신규 트랜잭션이 아니다( isNewTransaction=false ).
     */
    @Test
    void inner_commit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);
    }
    /**
     * 외부 트랜잭션 시작
     * Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
     * Acquired Connection [HikariProxyConnection@1943867171 wrapping conn0] for JDBC transaction
     * Switching JDBC Connection [HikariProxyConnection@1943867171 wrapping conn0] to manual commit
     * outer.isNewTransaction()=true
     *
     * 내부 트랜잭션 시작
     * Participating in existing transaction
     * inner.isNewTransaction()=false
     * 내부 트랜잭션 커밋
     *
     * 외부 트랜잭션 커밋
     * Initiating transaction commit
     * Committing JDBC transaction on Connection [HikariProxyConnection@1943867171 wrapping conn0]
     * Releasing JDBC Connection [HikariProxyConnection@1943867171 wrapping conn0] after transaction
     *
     *
     * 내부 트랜잭션을 시작할 때 Participating in existing transaction 이라는 메시지가 있는데 내부 트랜잭션이
     * 기존에 존재하는 외부 트랜잭션에 참여한다는 뜻이다.
     *
     * 스프링은 이렇게 여러 트랜잭션이 함께 사용되는 경우, 처음 트랜잭션을 시작한 외부 트랜잭션이 실제 물리 트랜
     * 잭션을 관리하도록 한다. 이를 통해 내부 트랜잭션이 커밋하지 못하도록 한다.
     * 그리고 최종적으로 트랜잭션 중복 커밋 문제를 해결한다.
     */


    // 외부 롤백
    @Test
    void outer_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outer);
    }

    // 내부 롤백
    @Test
    void inner_rollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        assertThatThrownBy(()-> txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);
    }

    @Test
    void inner_rollback_requires_new() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = txManager.getTransaction(definition);     // 새 물리 트랜잭션 사용
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());  // true

        log.info("내부 트랜잭션 롤백");

    }
}
