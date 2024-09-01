package study.data_jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<Long> {

    @Id @GeneratedValue
    private Long id;

    /**
     * isNew 판정
     * isNew는 새로운 엔티티 여부를 확인하는 메소드로 Entity에 값이 있는지 없는지로 확인한다.
     * 하지만 식별자 전략이 @GeneratedValue가 아닐 경우 값을 직접 넣어주므로 isNew() 실행 시 값이 존재한다.
     * 따라서 persist 전략 대신 merge를 사용하게 되고, 비효율적인 DB 호출을 하게된다.
     * 이를 해결하기 위해 isNew()를 override하는 것이다.
     * @CreatedDate로 새 엔티티 여부를 확인하면 생성 시점만 관여하고 있으므로 안전하게 여부를 구할 수 있다.
     */
    @CreatedDate
    private LocalDateTime createdDate;

    public Item(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
