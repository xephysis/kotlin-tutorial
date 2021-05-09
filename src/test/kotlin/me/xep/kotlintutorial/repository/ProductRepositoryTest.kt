package me.xep.kotlintutorial.repository

import me.xep.kotlintutorial.model.Product
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier
import java.util.function.Consumer
import kotlin.reflect.typeOf

//일단 DB 까지 연동하는 통합 테스트부터
//internal keyword 는 뭐지 -> 아 접근 제어자구나
@ActiveProfiles("h2","test")
@SpringBootTest
internal class ProductRepositoryTest {
    //Autowird 이거 안쓰고 싶기한데 일단은...

    @Autowired
    private lateinit var repository: ProductRepository

    @BeforeEach
    fun before() {

    }

    //blocking 안시키고 할 방법이 필요한데
    @Test
    fun `insert with blocking`() {
        var target: Product = Product(0L, "test-product", 0.5f)
        //!! 는 무슨 문법이지
        //https://philosopher-chan.tistory.com/58
        //아 null이 아니라고 명시
        target = repository.insert(target).block()!!
        assert(target != null)
        assert(target.id != null)
        assert(target.id!! > 0L)
    }

    @Test
    fun `insert real`() {
        var target: Product = Product(0L, "test-product", 0.5f)
        StepVerifier.create(repository.insert(target))
                .assertNext { assert(it.id > 0) }
                .verifyComplete()
    }

    @Test
    fun `insert mocking`() {
        //TODO 여기도 Mockito 그대로 쓸 수 있나???
    }

    @Test
    fun `testInsertAndGet`() {
        var target: Product = Product(0L, "test-product", 0.5f)
        target = repository.insert(target).block()!!

    }
}

