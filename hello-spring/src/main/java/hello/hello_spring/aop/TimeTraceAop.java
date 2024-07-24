package hello.hello_spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aop(관점지향 프로그래밍)
 *
 * 공통 관심 사항(cross-cutting concern) vs 핵심 관심 사항(core concern)을 분리한다
 * 1만개의 메소드에 공통적으로 시간을 측정하는 기능을 추가하고 싶다.
 * 일일이 다 추가하기 어려우니 관심 사항을 분리하자.
 *
 * ### 장점
 * 핵심 관심 사항을 깔끔하게 유지할 수 있다.
 * 변경이 필요하면 이 로직만 변경하면 된다.
 * 원하는 적용 대상을 선택할 수 있다.
  */
@Aspect
@Component
public class TimeTraceAop {

    @Around("execution(* hello.hello_spring..*(..))")   // 적용 대상 정하는 어노테이션
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("START: " + joinPoint.toString());
        try{
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END: " + joinPoint.toString() + " " + timeMs + "ms");
        }
    }

}
