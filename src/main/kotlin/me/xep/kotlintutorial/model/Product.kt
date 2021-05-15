package me.xep.kotlintutorial.model

import org.springframework.data.annotation.Id

//일단 idea 가 만들어준 코드 그대로 사용해봄
class Product {
    //nullable 하게 만들어 두면 안되는구나
    //아니야 id 는 Nullable 하게 두어야 해
    @Id
    var id: Long? = null
    var name: String = ""
    var price: Float = 0.0f

    constructor() {}
    constructor(name: String = "", price: Float = 0.0f) {
        this.name = name
        this.price = price
    }

    constructor(id: Long = 0L, name: String = "", price: Float = 0.0f) {
        this.id = id
        this.name = name
        this.price = price
    }

    init {
        println("product construct $id, $name, $price")
    }

    //코틀린에서도 이런식으로 구현을... 해줘야하는구나 아니면 lombok 에서 data 달듯이 data 를 달아두던가
    //https://stackoverflow.com/questions/45772946/equality-in-kotlin
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Product) {
            return false
        }

        return id == other.id && name == other.name && price == other.price
    }
}