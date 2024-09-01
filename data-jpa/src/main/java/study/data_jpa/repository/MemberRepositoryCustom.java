package study.data_jpa.repository;

import study.data_jpa.entity.Member;

import java.util.List;

/**
 * CustomRepository 구현
 * 인터페이스를 만든 후 구현체 클래스 별도로 만들기.
 * 그리고 인터페이스를 상속받아 메소드를 사용하면 JPA가 자동으로 구현체의 구현 메소드 로직을 가져온다
 * -> 구현체 클래스 이름은 {Repository 이름} + Impl
 * 하지만 구현체는 관심사 분리가 아니다. 해당 repository의 크기가 더 커질 뿐이다.
 * 화면 로직 쿼리와 비지니스 로직 쿼리를 분리하고 싶다면 별도의 Repository를 만들자.
 */
public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
