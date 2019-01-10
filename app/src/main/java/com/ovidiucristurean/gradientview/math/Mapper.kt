package com.ovidiucristurean.gradientview.math

interface Mapper<T:Number> {
    fun map(value: T, x: T, y: T, a: T, b: T): T
}