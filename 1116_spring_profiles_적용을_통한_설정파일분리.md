# spring.profiles.active 를 통한 설정파일 분리(2025.11.16)

```aiignore
springboot 에서 제공하는 spring.profiles.active 를 이용하여, 
개발/테스트/운영으로 설정파일을 분리한다.
```

## 1. profile 적용

### 1.1 profile 을 어떻게 읽을까?

| 용어                              | 상황                   | 표기       |
| ------------------------------- | -------------------- | -------- |
| Profile (SNS/사용자 정보)            | 일반적 의미               | **프로필**  |
| Profile (환경설정, 구성, 성능 등 기술적 의미) | Spring, Java, Config | **프로파일** |
| `spring.profiles.*`             | Spring 환경 구분         | **프로파일** |

```aiignore
Spring의 spring.profiles을 말할 때는
👉 “스프링 프로파일”
👉 “프로파일 분리”
가 맞다.
```



### 1.2 profile 을 적용하자.




```aiignore
profile 을 
prod
dev
test 
로 설정파일을 분리하자.

```

```aiignore
✅ IntelliJ 에서 spring.profiles.active 적용하는 4가지 방법
1) Run/Debug Configurations → VM 옵션에 설정 (가장 추천)

IntelliJ에서 애플리케이션 실행 시:
1. 메뉴 상단 → Run → Edit Configurations…
2. 왼쪽에서 Spring Boot 실행 설정 선택
3. VM options 칸에 아래처럼 입력:
-Dspring.profiles.active=prod

(예: prod, test 등)

✔ 특징
가장 확실하게 적용됨
JAR 실행할 때와 방법이 동일해서 운영 이전 테스트와도 잘 맞음


2) Run/Debug Configurations → Program arguments 에 설정
같은 위치에서 이번엔 Program arguments에 설정:
--spring.profiles.active=prod

✔ 특징
Spring Boot 공식 방식
VM 옵션과 달리 -- 를 사용함
Docker, 스크립트 실행과 동일한 방식이라 일관성 있음

3) application.properties 안에 미리 고정해두기
spring.profiles.active=prod

✔ 특징
코드 안에 박히므로 환경마다 바뀌는 설정에는 비추천
질문하신 목적(운영용/테스트용 분리)에는 안 맞음

4) 환경 변수(Environment variables)로 설정
Run/Debug Configurations → Environment variables 입력란에서:
SPRING_PROFILES_ACTIVE=prod

✔ 특징
리눅스 서버 등에서 자주 쓰는 방식
IntelliJ에서도 적용 가능
CI/CD 또는 Docker 환경과 일관성 있음

🔎 어떤 방법을 쓰는 게 가장 좋나?
방식	추천도	이유
VM options: -Dspring.profiles.active=xxx	⭐⭐⭐⭐⭐	가장 명확하고 오해 없음
Program args: --spring.profiles.active=xxx	⭐⭐⭐⭐	스프링 공식 옵션, 운영 배포 스크립트와 동일
Environment variable	⭐⭐⭐	Docker/K8S 환경에서 유용
application.properties	⭐	테스트/운영 분리 목적에서는 비추
📌 중요한 확인 포인트
① IntelliJ에서 실행 시 프로필이 적용되었는지 확인하려면

콘솔에서 이런 메시지가 뜸:
The following 1 profile is active: "prod"
② 프로필별 설정 파일 이름
프로퍼티 기반이라면:
application-test.properties
application-prod.properties
이 형식으로 사용해야 함
(yaml 이면 application-test.yml)
```


```aiignore
1안으로 결정한다.
Run 에서 Edit Configuration 으로 dev 를 지정하면

-- dev
2025-11-16T13:03:15.082+09:00  INFO 18464 --- [TEST_20251116_0001] [  restartedMain] c.c.s.t.Test202511160001Application      : The following 1 profile is active: "dev"

-- test
2025-11-16T13:15:51.876+09:00  INFO 29992 --- [TEST_20251116_0001] [  restartedMain] c.c.s.t.Test202511160001Application      : The following 1 profile is active: "test"

-- prod
2025-11-16T13:16:28.393+09:00  INFO 25988 --- [TEST_20251116_0001] [  restartedMain] c.c.s.t.Test202511160001Application      : The following 1 profile is active: "prod"


지정은 잘 된다.
```

```
설정파일을 분리해보자.

application-dev.properties
application-test.properties
application-prod.properties
```


### [주의] 파일명-$PROFILE.properties 시 delimiter 를 주의한다.
#### "." 이 아니다. "-" 이다.
#### "." 로 할 경우 placeholder 자체를 못찾아서 bean injection 에 실패한다.

```
application.dev.properties 
application.test.properties
application.prod.properties
```

### 설정방법 
```
-Dspring.profiles.active=dev

으로 VM 설정값으로 통제한다. (위 1안)
```


```aiignore
package com.colamanlabs.springboot2025.test_20251116_0001;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class Test202511160001Application
{
    @Value("${properties.file.name}")
    private String propertiesFileName;

    public static void main(String[] args)
    {
        SpringApplication.run(Test202511160001Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init()
    {
        log.info("propertiesFileName: {}", propertiesFileName);
    }

}
```


```aiignore
-Dspring.profiles.active=dev 
로 지정하면

C:\WORKS\WORKS_JAVA_COMMON\JDK\openjdk_corretto\amazon-corretto-17.0.16.8.1-windows-x64-jdk\jdk17.0.16_8\bin\java.exe -Dspring.profiles.active=dev -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true "-Dmanagement.endpoints.jmx.exposure.include=*" "-javaagent:C:\WORKS\WORKS_INTELLIJ_IDEA\JetBrains\IntelliJ IDEA 2025.2.1\lib\idea_rt.jar=10721" -Dfile.encoding=UTF-8 -classpath C:\WORKS\WORKS_INTELLIJ_IDEA\WORKSPACE_INTELLIJ_IDEA_GITHUB\study_springboot_2025\TEST_20251116_0001\target\classes;C:\Users\colaman\.m2\repository\org\springframework\boot\spring-boot-starter\3.5.7\spring-boot-starter-3.5.7.jar;C:\Users\colaman\.m2\repository\org\springframework\boot\spring-boot\3.5.7\spring-boot-3.5.7.jar;C:\Users\colaman\.m2\repository\org\springframework\spring-context\6.2.12\spring-context-6.2.12.jar;C:\Users\colaman\.m2\repository\org\springframework\spring-aop\6.2.12\spring-aop-6.2.12.jar;C:\Users\colaman\.m2\repository\org\springframework\spring-beans\6.2.12\spring-beans-6.2.12.jar;C:\Users\colaman\.m2\repository\org\springframework\spring-expression\6.2.12\spring-expression-6.2.12.jar;C:\Users\colaman\.m2\repository\io\micrometer\micrometer-observation\1.15.5\micrometer-observation-1.15.5.jar;C:\Users\colaman\.m2\repository\io\micrometer\micrometer-commons\1.15.5\micrometer-commons-1.15.5.jar;C:\Users\colaman\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\3.5.7\spring-boot-autoconfigure-3.5.7.jar;C:\Users\colaman\.m2\repository\org\springframework\boot\spring-boot-starter-logging\3.5.7\spring-boot-starter-logging-3.5.7.jar;C:\Users\colaman\.m2\repository\ch\qos\logback\logback-classic\1.5.20\logback-classic-1.5.20.jar;C:\Users\colaman\.m2\repository\ch\qos\logback\logback-core\1.5.20\logback-core-1.5.20.jar;C:\Users\colaman\.m2\repository\org\apache\logging\log4j\log4j-to-slf4j\2.24.3\log4j-to-slf4j-2.24.3.jar;C:\Users\colaman\.m2\repository\org\apache\logging\log4j\log4j-api\2.24.3\log4j-api-2.24.3.jar;C:\Users\colaman\.m2\repository\org\slf4j\jul-to-slf4j\2.0.17\jul-to-slf4j-2.0.17.jar;C:\Users\colaman\.m2\repository\jakarta\annotation\jakarta.annotation-api\2.1.1\jakarta.annotation-api-2.1.1.jar;C:\Users\colaman\.m2\repository\org\springframework\spring-core\6.2.12\spring-core-6.2.12.jar;C:\Users\colaman\.m2\repository\org\springframework\spring-jcl\6.2.12\spring-jcl-6.2.12.jar;C:\Users\colaman\.m2\repository\org\yaml\snakeyaml\2.4\snakeyaml-2.4.jar;C:\Users\colaman\.m2\repository\org\springframework\boot\spring-boot-devtools\3.5.7\spring-boot-devtools-3.5.7.jar;C:\Users\colaman\.m2\repository\org\springframework\boot\spring-boot-configuration-processor\3.5.7\spring-boot-configuration-processor-3.5.7.jar;C:\Users\colaman\.m2\repository\org\projectlombok\lombok\1.18.42\lombok-1.18.42.jar;C:\Users\colaman\.m2\repository\org\slf4j\slf4j-api\2.0.17\slf4j-api-2.0.17.jar com.colamanlabs.springboot2025.test_20251116_0001.Test202511160001Application

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.7)

2025-11-16T13:31:28.219+09:00  INFO 30208 --- [TEST_20251116_0001] [  restartedMain] c.c.s.t.Test202511160001Application      : Starting Test202511160001Application using Java 17.0.16 with PID 30208 (C:\WORKS\WORKS_INTELLIJ_IDEA\WORKSPACE_INTELLIJ_IDEA_GITHUB\study_springboot_2025\TEST_20251116_0001\target\classes started by colaman in C:\WORKS\WORKS_INTELLIJ_IDEA\WORKSPACE_INTELLIJ_IDEA_GITHUB\study_springboot_2025\TEST_20251116_0001)
2025-11-16T13:31:28.221+09:00  INFO 30208 --- [TEST_20251116_0001] [  restartedMain] c.c.s.t.Test202511160001Application      : The following 1 profile is active: "dev"
2025-11-16T13:31:28.251+09:00  INFO 30208 --- [TEST_20251116_0001] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2025-11-16T13:31:28.603+09:00  INFO 30208 --- [TEST_20251116_0001] [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
2025-11-16T13:31:28.624+09:00  INFO 30208 --- [TEST_20251116_0001] [  restartedMain] c.c.s.t.Test202511160001Application      : Started Test202511160001Application in 0.725 seconds (process running for 1.204)
2025-11-16T13:31:28.626+09:00  INFO 30208 --- [TEST_20251116_0001] [  restartedMain] c.c.s.t.Test202511160001Application      : propertiesFileName: application-dev.properties

Process finished with exit code 0

```

-- 끝 --
