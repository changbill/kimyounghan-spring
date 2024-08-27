package jpabook.jpashop.repository;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 컬렉션을 반환하는 경우 Result로 감싸서 반환할 것
 * 직접적으로 반환하면 배열 외에 데이터를 추가하기 어려움
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private T data;
}