package me.xep.kotlintutorial.model

import org.springframework.data.annotation.Id

//일단 idea 가 만들어준 코드 그대로 사용해봄
class Product {
    //nullable 하게 만들어 두면 안되는구나
    @Id
    var id: Long = 0
    var name: String = ""
    var price: Float = 0.0f

    constructor() {}
    constructor(id: Long = 0L, name: String = "", price: Float = 0.0f) {
        this.id = id
        this.name = name
        this.price = price
    }

    init {
        println("product construct $id, $name, $price")
    }
}