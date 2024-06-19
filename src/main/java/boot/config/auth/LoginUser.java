package boot.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  1. @Target(ElementType.PARAMETER)
 *  - 어노테이션이 생성 될 수 있는 위치 지정
 *  - PARAMETER 로 지정하였으므로 메서드에 파라미터로 선언된 객체에서만 사용가능
 *  - 이 외에도 클래스 선언문에서 쓸 수 있는 TYPE 등이 있음.
 *
 *  2. @interface
 *  - 이 파일을 어노테이션 클래스로 지정
 *  - @LoginUser 가 생겼다고 보면 됨.
 *
 *  3. @Retention
 *  - 애노테이션의 라이프 사이클
 *  - 즉 애노테이션이 언제까지 살아 있을지 결정하는 것.
 *  - SOURCE : 소스 코드 (.java) (ex. lombok - getter / 컴파일 하면 사라지는 이유!)
 *  - CLASS : 클래스 파일(.class) = 바이트 코드 (이거 ) (만약에 내가 애노테이션을 jar 배포해서 애노테이션을 잠재적으로 더 활용 가능하게 하기 위해.. )
 *  - RUNTIME : 런타임 까지 (= 즉 안사라짐)  (ex. @Controller, @Service, @Autowired 등 스프링이 컴포넌트 스캔을 하는 방법(Reflection)이 애노테이션을 찾아서 사용.)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
