package me.xep.kotlintutorial.repository

import me.xep.kotlintutorial.model.Product
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.data.relational.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


//문법 몇개 터지던건 참조하는 부분을 잘못 넣어서 그럼
//import org.springframework.r2dbc.core.DatabaseClient
//import org.springframework.data.r2dbc.core.DatabaseClient -> deprecated 되었으니 따라가지 변경하는걸로

// TODO 이런식으로 생성자 선언이 가능하고, 디펜던시 주입받는거구나 -> 확인 필요
@Repository
class ProductRepository(private val template: R2dbcEntityTemplate) {
    //https://stackoverflow.com/questions/64647566/spring-r2dbc-databaseclient-as
    //DatabaseClient 바뀌면서 샘플에 있던 as 문법 없어짐
    //R2dbcEntityTemplate 쓰라고 함

    //적당한 샘플이 없어서 참조
    //https://github.com/kakaohairshop/spring-r2dbc-study/blob/main/src/main/kotlin/kr/co/hasys/springr2dbcstudy/shop/ShopControllerV2.kt

    fun findById(id: Long): Mono<Product> {
        return template.selectOne(query(where("id").`is`(id)), Product::class.java)
                .switchIfEmpty(Mono.error(RuntimeException()))
    }

    fun findAll(): Flux<Product> {
        return template
                .select(Product::class.java)
                .all()
    }

    fun insert(entity: Product): Mono<Product> {
        return template
                .insert(Product::class.java)
                .using(entity)
    }

    //save 로 하나로 퉁치는게 맞을것 같은데 일단은 뭐
    //아직까진 생각보다 조금 낯선것 같은데
    fun update(entity: Product): Mono<Int> {

        //여기서 이런 체크를 해야하는게 맞나 아니면 서비스레이어에서 해야하는거 아닐까 싶기도 한데
        if (entity.id == null) {
            return Mono.error(RuntimeException("invalid id"))
        }

        return template.selectOne(query(where("id").`is`(entity.id!!)), Product::class.java)
                .switchIfEmpty(Mono.error(RuntimeException()))
                .flatMap {
                    template.update(Product::class.java)
                            .matching(query(where("id").`is`(it.id!!)))
                            .apply(Update.update("name", entity.name)
                                    .set("price", entity.price))
                }
    }

    fun delete(entity: Product): Mono<Void> {
        return template.delete(entity)
                .then()
    }

    fun deleteById(id: Long) : Mono<Int> {
        return template.delete(Product::class.java)
                .matching(query(where("id").`is`(id)))
                .all()
                .onErrorMap { RuntimeException() }
    }
}