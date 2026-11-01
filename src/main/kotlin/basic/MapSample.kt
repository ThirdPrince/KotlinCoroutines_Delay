package org.example.basic

fun main() {
    val bookName = listOf("Thinking in java","Dive into Kotlin").map {  it.first() }
    println(bookName)
}