package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    /**
     * 원래 JPA repository는 @PersistenceContext로 엔티티 매니저를 주입해야하지만
     * 스프링 부트의 스프링 데이터 JPA는 @Autowired도 지원하여 @RequiredArgsConstructor 사용이 가능하다.
      */
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // JPQL : 엔티티 객체에 대한 쿼리를 작성한다는 점이 SQL과 다름
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name) // jpql 문에 name 바인딩 시키기 위한 메소드
                .getResultList();
    }
}
