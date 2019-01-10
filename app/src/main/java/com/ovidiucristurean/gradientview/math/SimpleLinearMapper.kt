package com.ovidiucristurean.gradientview.math

/**
 * convert a value in the interval [x,y] to its corresponding value in the interval [a,b], with the formula:
 * [x,y]->[a,b] => (value-x)*(b-a)/(y-x)+a
 */
class SimpleLinearMapper : Mapper<Double> {

    override fun map(value: Double, x: Double, y: Double, a: Double, b: Double): Double {
        return (value - x) * (b - a) / (y - x) + a
    }

}