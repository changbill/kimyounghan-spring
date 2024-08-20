package hellojpa;

import jakarta.persistence.*;
import org.h2.command.ddl.AlterDomainDropConstraint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

            Member member = new Member();
            member.setUserName("Hello");
            member.setHomeAddress(new Address("homecity","street", "zipcode"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            // insert into ADDRESS 쿼리 3개는 AddressEntity와 맵핑되어있는 ADDRESS table
            member.getAddressHistory().add(new AddressEntity("city1", "street1", "zipcode1"));
            member.getAddressHistory().add(new AddressEntity("city2", "street2", "zipcode2"));
            member.getAddressHistory().add(new AddressEntity("city3", "street3", "zipcode3"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("==============START===============");
            Member findMember = em.find(Member.class, member.getId());
//            List<AddressEntity> addressList = findMember.getAddressHistory();
//
//            for(AddressEntity address : addressList) {
//                System.out.println("address = " + address.);
//            }
//
//            Set<String> favoriteFoods = findMember.getFavoriteFoods();
//            for(String favoriteFood : favoriteFoods) {
//                System.out.println("favoriteFood = " + favoriteFood);
//            }
            tx.commit();
        } catch(Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();


    }
}
