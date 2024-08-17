package hellojpa;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class MemberProduct {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int count;
    private int price;
}