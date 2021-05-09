# kotlin tutorial
* from
    * https://www.baeldung.com/kotlin/spring-boot-kotlin-coroutines
* Spring Initializr
    * Spring Reactive Web
    * H2 Database
    * Spring Data R2DBC
    * Lombok
* 튜토리얼의 버전을 그대로 안쓰다보니 코드 작성 시점의 버전이랑 안맞는 부분이 꽤 있어서 개조
  * 특히 r2dbc 가 버전이 많이 바뀜
  
# 작업노트
## org.springframework.data.r2dbc.dialect.DialectResolver 의 static 초기화 중 터짐
* org.springframework.data.relational.core.dialect.OracleDialect 가 없어서 초기화 하는 도중 터짐
* 버전 문제일것 같음
* org.springframework.data.relational.core.dialect.OracleDialect 은 spring-data-relation:2.0.9 에 없음
```
<!-- https://mvnrepository.com/artifact/org.springframework.data/spring-data-relational -->
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-relational</artifactId>
    <version>2.2.0</version>
</dependency>
```
* 그냥 spring boot version 을 올리고 해결 2.4.5
  
### h2 보드 안뜸
* webflux 에서는 원래 안뜬다고 함
* 수동으로 띄움
  * https://stackoverflow.com/questions/52949088/h2-db-not-accessible-at-localhost8080-h2-console-when-using-webflux


### h2 콘솔 띄워도 접속이 안됨
* 찾긴 찾았는데 이유를 정확하게는 모르겠음 (단순 프로토콜 차이라고 보기에는...)
  * h2 콘솔에서 접속 시도한 url
    * jdbc:h2:mem:testdb
    * Database "mem:testdb" not found, either pre-create it or allow remote database creation (not recommended in secure environments) [90149-200] 90149/90149
* ConnectionFactory 선언 과정 중에 차이가 있음
* 정상 동작 하면서 h2 콘솔에서 접속 가능 
```
return H2ConnectionFactory(H2ConnectionConfiguration.builder()
        .url("mem:testdb;DB_CLOSE_DELAY=-1;")
        .username("sa")
        .build())
```
* 정상 동작은 하지만 h2 콘솔에서 접속되지 않음
  * 일단 위의 설정으로 되는건 확인했으니 AbstractR2dbcConfiguration 없애고 autoconfigure 로 걸어놔도 될 듯
    * 안됨 
```
return H2ConnectionFactory(H2ConnectionConfiguration.builder()
        .url("r2dbc:h2:mem:///test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
        .username("sa")
        .build())
```
* spring.r2dbc.url=mem:testdb;DB_CLOSE_DELAY=-1;
  * URL mem:testdb;DB_CLOSE_DELAY=-1; does not start with the r2dbc scheme
* spring.r2dbc.url=r2dbc:h2:mem:///test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
  * 정상 동작하지만 h2 console 에서는 여전히 안됨
* 이게 중요한게 아니므로.... 적당히 스킵
