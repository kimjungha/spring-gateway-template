# SpringGatewayTemplate
### Gateway

- CPU 작업이 아닌 네트워크 I/O (inbound/outbound) 를 기다리는데 쓰는 서버이다.
- 즉 gateway 는 I/O 최적화 서버가 필요하다. (I/O 대기중에 스레드를 점유하지 않는 모델이 유리) 
- 코드기반 라우팅은 `@Configuration + RouteLocator Bean` 을 만들고 "라우팅전용패키지"에 둔다.


### Tomcat vs Netty

| 항목 | Tomcat(MVC) | Netty(WebFlux) |
| --- | --- | --- |
| 모델 | Thread-per-request | Event Loop |
| I/O | Blocking | Non-blocking |
| 동시 처리량| 설정해놓은 최대 스레드 수에 의존 | 적은 스레드로 수만 개의 연결처리가 가능 |
| Context Switch | 많음 | 거의 없음 |
| 메모리 | 스레드마다 스택 할당 | 스레드 수가 적어서 효율적 |

Java 21 이상에서 spring.threads.virtual.enabled=true를 설정하면, MVC도 I/O 대기 중에 OS 스레드를 점유하지 않는다.

### Tomcat MVC

의존성 : `implementation 'org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc'`

- `Thread-per-request` : 요청 1개 → 스레드 1개
    - 요청에 대해 응답이 올때까지 스레드 점유
- 1000 개 동시 요청 → 설정해놓은 최대 스레드 수 고갈로 인한 대기와 지연 증가

### Netty WebFlux

의존성: `implementation 'org.springframework.cloud:spring-cloud-starter-gateway-server-webflux'`

- `Event Loop`: 적은 수의 스레드가 이벤트가 생길때만 처리하는 방식
    - 요청 기다리는동안 스레드 점유 안함
- 1000개 동시 요청 → 스레드 수 거의 증가 없음

### ServerWebExchange (WebFlux 전용)

- 하나의 HTTP 요청,응답, 그 요청을 처리하는 동안 필요한 모든 상태를 하나로 묶어둔 객체
- HTTP 요청 1개  ↔  ServerWebExchange 1개

### AbstractGatewayFilterFactory (WebFlux 전용) 

- Gateway 라우트에 붙일 수 있는 “재사용가능한 커스텀 필터” 를 만들기 위한 추상클래스
- GatewayFilter 를 깔끔하게+설정 가능하도록 만든 베이스클래스

### Gateway 가 필터를 생성하는 방식 (WebFlux 방식)
Spring Cloud Gateway 는 내부적으로 아래 단계를 거친다. 
1. FilterFactory Bean 스캔
2. YAML 필터 설정 파싱
3. Config 객체 생성 
4. apply(Config) 호출 -> 실제 GatewayFilter 생성 
