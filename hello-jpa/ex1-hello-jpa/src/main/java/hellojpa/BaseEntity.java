package hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;
/**
 * @MappedSuperclass
 * 공통 속성을 상속받을 수 있게 하는 애노테이션
 * 여러 엔티티에 공통적인 속성이 생긴다면 고려해볼 것
 * 사용방법은 공통 속성과 메소드 작성, 이후 공통 속성을 넣을 엔티티 클래스에 extends를 통해 상속해주면 된다.
 * 주의 :: Entity가 아니고, 상속관계 맵핑이 아니다. BaseEntity라는 테이블이 생성되지 않고 오직 자식 클래스에 맵핑 정보만 제공
 *
 * 엔티티에 extends(상속)을 할때는 2가지 경우가 있다.
 * @Entity 클래스 extends : 상속
 * @MappedSuperclass extends : 속성만 맵핑
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "INSERT")
    private String createdBy;
    private LocalDateTime createdDate;
    @Column(name = "UPDATE")
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
