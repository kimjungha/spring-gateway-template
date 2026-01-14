# SpringGatewayTemplate
### Gateway

- CPU 작업이 아닌 I/O (inbound/outbound) 작업이다.  = CPU 로 계산하는 작업이 아닌 네트워크를 기다리는서버이다.
- 즉 gateway 는 I/O 최적화 서버가 필요하다. 
- 코드기반 라우팅은 `@Configuration + RouteLocator Bean` 을 만들고 "라우팅전용패키지"에 둔다.


### Gateway Tomcat vs Netty

| 항목 | Tomcat(MVC) | Netty(Gateway) |
| --- | --- | --- |
| 모델 | Thread-per-request | Event Loop |
| I/O | Blocking | Non-blocking |
| 동시 연결스레드 수 | 스레드 수에 의존 | 수만개 가능 |
| Context Switch | 많음 | 거의 없음 |
| 메모리 | 스택 많이 사용 | 효율적 |

### Tomcat Gateway

의존성 : `implementation 'org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc'`

- `Thread-per-request` : 요청 1개 → 스레드 1개
    - 요청에 대해 응답이 올때까지 스레드 점유
- 1000 개 동시 요청 → 1000개 스레드, GC/ContextSwitch 폭증

### Netty Gateway

의존성: `implementation 'org.springframework.cloud:spring-cloud-starter-gateway'`

- `Event Loop`: 적은 수의 스레드가 이벤트가 생길때만 처리하는 방식
    - 요청 기다리는동안 스레드 점유 안함
- 1000개 동시 요청 → 스레드 수 거의 증가 없음

### ServerWebExchange

- 하나의 HTTP 요청,응답, 그 요청을 처리하는 동안 필요한 모든 상태를 하나로 묶어둔 객체
- HTTP 요청 1개  ↔  ServerWebExchange 1개

### AbstractGatewayFilterFactory

- Gateway 라우트에 붙일 수 있는 “재사용가능한 커스텀 필터” 를 만들기 위한 추상클래스
- GatewayFilter 를 깔끔하게+설정 가능하도록 만든 베이스클래스

### Gateway 가 필터를 생성하는 방식 
Spring Cloud Gateway 는 내부적으로 아래 단계를 거친다. 
1. FilterFactory Bean 스캔
2. YAML 필터 설정 파싱
3. Config 객체 생성 
4. apply(Config) 호출 -> 실제 GatewayFilter 생성 