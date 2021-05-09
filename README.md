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
  