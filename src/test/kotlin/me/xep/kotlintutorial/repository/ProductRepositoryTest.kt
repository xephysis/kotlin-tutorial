package me.xep.kotlintutorial.repository

import me.xep.kotlintutorial.model.Product
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.util.function.Tuple2


//일단 DB 까지 연동하는 통합 테스트부터
//internal keyword 는 뭐지 -> 아 접근 제어자구나
@ActiveProfiles("h2", "test")
@SpringBootTest
internal class ProductRepositoryTest {
    //Autowird 이거 안쓰고 싶기한데 일단은...

    @Autowired
    private lateinit var repository: ProductRepository

    @Autowired
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @BeforeEach
    fun before() {

    }

    @AfterEach
    fun after() {
        //이런식으로 쓸 수 있을듯?
        //https://docs.spring.io/spring-data/r2dbc/docs/current-SNAPSHOT/reference/html/#r2dbc.getting-started

//        StepVerifier.create(r2dbcEntityTemplate.databaseClient.sql("TRUNCATE TABLE PRODUCT")
//            .fetch()
//            .rowsUpdated())
//            .expectNextCount(1)
//            .verifyComplete()

        //이 문법이 조금 더 마음에 드는듯?
        r2dbcEntityTemplate.databaseClient.sql("TRUNCATE TABLE PRODUCT")
            .fetch()
            .rowsUpdated()
            .`as` { StepVerifier.create(it) }
            .expectNextCount(1)
            .verifyComplete()
    }

    //blocking 안시키고 할 방법이 필요한데
    @Test
    fun `insert with blocking`() {
        var target = Product("test-product", 0.5f)
        //!! 는 무슨 문법이지
        //https://philosopher-chan.tistory.com/58
        //아 null이 아니라고 명시
        target = repository.insert(target).block()!!
        assert(target.id != null)
        assert(target.id!! > 0L)
    }

    @Test
    fun `insert`() {
        val target = Product("target", 0.5f)
        StepVerifier.create(repository.insert(target))
            .assertNext { assert(it.id!! > 0) }
            .verifyComplete()
    }

    fun `insert mocking`() {
        //TODO 여기도 Mockito 그대로 쓸 수 있나??? -> mocking 테스트는 지금 당장 해볼건 아니니까 일단 패스
    }

    @Test
    fun `get invalid expected mono error`() {
        StepVerifier.create(repository.findById(-1))
            .expectError()
            .verify()
    }

    @Test
    fun `insert and invalid compare`() {
        val prev = Product("prev", 1.1f)
        repository.insert(prev).block()!!

        val target = Product("target", 1.1f)

        StepVerifier.create(repository.insert(target))
            .assertNext { t -> assertThat(t).isNotEqualTo(prev) }
            .verifyComplete()
    }

    @Test
    fun `insert and valid compare`() {
        val target = Product("target", 1.1f)

        StepVerifier.create(repository.insert(target))
            .assertNext { t -> assertThat(t).isEqualTo(target) }
            .verifyComplete()
    }

    @Test
    fun `insert and get`() {
        val target = Product("target", 1.1f)

        StepVerifier.create(repository.insert(target)
            .flatMap { repository.findById(it.id!!) })
            .assertNext { t -> assertThat(t).isEqualTo(target) }
            .verifyComplete()
    }

    //이런식으로 검증하는게 맞을까?
    @Test
    fun `insert multi`() {
        val target1 = Product("target1", 1.1f)
        val target2 = Product("target2", 1.2f)

        //https://www.baeldung.com/reactor-combine-streams#8-zip 이건 원하는거랑은 약간 다른데
        StepVerifier.create(
            Flux.zip(repository.insert(target1), repository.insert(target2))
        )
            .assertNext { t: Tuple2<Product, Product>? ->
                //run 이 뭐지
                run {
                    assertThat(t).isNotNull
                    assertThat(t!!.t1).isEqualTo(target1)
                    assertThat(t.t2).isEqualTo(target2)
                }
            }
            .expectComplete()
            .verify()
    }

    //테스트 돌리면서 이미 이것저것 쌓여져 있을텐데 find all 에 대한 보장을 어떻게 하지?
    //역시나 테스트 도는 순서에 따라 영향을 많이 받음
    //테스트 격리가 필요한데 -> truncate all 은 예전에 사고난걸 많이 봐서 가급적 안하고 싶.....지만 명시적으로 지우거나 @DirtiesContext 보다는 일단 지우는걸로
    //https://velog.io/@ljinsk3/%ED%85%8C%EC%8A%A4%ED%8A%B8-%EA%B2%A9%EB%A6%ACTest-Isolation 재미있는 주제네
    @Test
    fun `find all not empty`() {
        //한개 넣고 실패하나 확인
        val prev = Product("prev", 1.1f)
        repository.insert(prev).block()!!

        assertThrows<AssertionError> {
            repository.findAll()
                .`as` { StepVerifier.create(it) }
                .expectComplete()
                .verify()
        }
    }

    @Test
    fun `find all one exists`() {
        val prev = Product("prev", 1.1f)
        repository.insert(prev).block()!!

        repository.findAll()
            .`as` { StepVerifier.create(it) }
            .expectNextCount(1)
            .verifyComplete()
    }


    //이게 잘 돌았는지는 결국 다시 꺼내봐야 아는건가
    @Test
    fun update() {
        val prev = Product("prev", 1.1f)
        repository.insert(prev)
            .map { Product(it.id!!, "modified", 1.2f) }
            .flatMap { repository.update(it) }
            .`as` { StepVerifier.create(it) }
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `update blocked`() {
        val prev = Product("prev", 1.1f)
        val mono = repository.insert(prev)
            .map { Product(it.id!!, "modified", 1.2f) }
            .flatMap { repository.update(it) }

        val result = mono.block()!!
        assertThat(result).isEqualTo(1)
    }

    //update 할게 없는 상태에서 error 를 뱉는게 맞는걸까? 아니면 일단 수행하고 나서 affected row 를 0으로 반환하는게 맞는걸까?
    @Test
    fun `not update blocked`() {
        assertThrows<RuntimeException> {
            val dummy = Product(-1, "modified", 1.2f)
            val mono = repository.update(dummy)
            mono.block()!!
        }
    }

    @Test
    fun `update and get blocked`() {
        val prev = Product("prev", 1.1f)
        val updateName = "modified"
        val updatePrice = 1.2f

        val mono = repository.insert(prev)
            .map { Product(it.id!!, updateName, updatePrice) }
            .flatMap { repository.update(it) }

        val result = mono.block()!!
        assertThat(result).isEqualTo(1)

        val updated = repository.findById(prev.id!!).block()!!
        assertThat(updated.id).isEqualTo(prev.id!!)
        assertThat(updated.name).isEqualTo(updateName)
        assertThat(updated.price).isEqualTo(updatePrice)
    }

    @Test
    fun `update and get reactive`() {
        val insertMono = repository.insert(Product("prev", 1.1f))
        val modifiedObjectMono = insertMono.map { Product(it.id!!, "modified", 1.2f) }
        val updateMono = modifiedObjectMono.flatMap { repository.update(it) }
        StepVerifier.create(updateMono)
            .expectNext(1)
            .verifyComplete()
    }

    @Test
    fun `update and get reactive2`() {
        val insertMono = repository.insert(Product("prev", 1.1f))
        val modifiedObjectMono = insertMono.map { Product(it.id!!, "modified", 1.2f) }
        val updateMono = modifiedObjectMono.flatMap { repository.update(it) }
        StepVerifier.create(updateMono)
            .assertNext { t -> assertThat(t).isEqualTo(1) }
            .verifyComplete()
    }

}

