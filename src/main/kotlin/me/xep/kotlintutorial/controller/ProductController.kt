package me.xep.kotlintutorial.controller

import lombok.extern.slf4j.Slf4j
import me.xep.kotlintutorial.model.Product
import me.xep.kotlintutorial.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Slf4j
@RequestMapping("/product")
@RestController
class ProductController {

    //TODO autowired 안시키는 방법은 없나
    @Autowired
    lateinit var productRepository: ProductRepository

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: Int): Mono<Product> {
        return productRepository.findById(id)
    }

    @GetMapping("/", "")
    fun findAll(): Flux<Product> {
        return productRepository.findAll()
    }
}