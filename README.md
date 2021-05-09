# kotlin tutorial
* from
    * https://www.baeldung.com/kotlin/spring-boot-kotlin-coroutines
* Spring Initializr
    * Spring Reactive Web
    * H2 Database
    * Spring Data R2DBC
    * Lombok
* 튜토리얼에서의 버전이랑, 작업시점의 버전이랑 안맞는 부분이 꽤 있어서 개조
* org.springframework.data.r2dbc.dialect.DialectResolver 의 static 초기화 중
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
* h2 대시 보드 안뜸