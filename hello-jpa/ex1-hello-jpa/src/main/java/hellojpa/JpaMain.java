package hellojpa;

import jakarta.persistence.*;

public class JpaMain {

    public static void main(String[] args) {

        // DB 당 하나만 생성, 애플리케이션 전체에서 공유
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // 요청마다 생성. 쓰레드간 공유 X
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        //code
        try {

            Member member1 = new Member();
            member1.setUsername("A");

            Member member2 = new Member();
            member2.setUsername("B");

            Member member3 = new Member();
            member3.setUsername("C");

            System.out.println("====================");
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
            System.out.println("member.id : " + member1.getId());
            System.out.println("member.id : " + member2.getId());
            System.out.println("member.id : " + member3.getId());
            System.out.println("====================");
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();


    }
}
