package hellojpa;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
@Entity
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ", //매핑할 데이터베이스 시퀀스 이름
        initialValue = 1, allocationSize = 50)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
    @Column(name = "name")  // 필드 명은 username이지만 컬럼명은 name이라면 @Column(name = ) 을 사용해보자.
    private String username;
    private Integer age;

    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Enumerated(EnumType.STRING)    // DB에는 기본적으로 ENUM 타입이 없으므로 @Enumerated 애노테이션으로 맵핑한다.
    private RoleType roleType;
    @Temporal(TemporalType.TIMESTAMP)   // 시간은 @Temporal 애노테이션을 사용한다. 타입에는 DATE, TIME, TIMESTAMP 3가지가 있다.
    private Date createdDate;           // DATE : 날짜, TIME : 시간, TIMESTAMP : 날짜 시간 을 나타낸다.
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    private LocalDate localDate;        // java8부터는 LocalDate, LocalDateTime 타입을 제공하는데 이는 애노테이션 없이도 맵핑된다.
    private LocalDateTime localDateTime;// LocalDate : 날짜(Date) 타입, LocalDateTime 날짜 시간(TimeStamp) 타입
    @Lob                            // VARCHAR 범위를 넘어서는 큰 값을 DB에 넣고 싶다면 @Lob을 애노테이션을 사용한다.
    private String description;
    @Transient                          // 메모리에서만 계산하고 DB에 맵핑안하고 싶은 필드 속성이 있다면 @Transient 애노테이션 사용
    private int temp;
    //Getter, Setter…
}
