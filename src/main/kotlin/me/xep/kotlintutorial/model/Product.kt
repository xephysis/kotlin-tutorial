package me.xep.kotlintutorial.model

import org.springframework.data.relational.core.mapping.Table

class Product {
    var id: Long = 0L
    var name: String = ""
    var price: Float = 0.0f
}