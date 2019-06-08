package ru.itmo.qcore.utils

import ru.itmo.qcore.CBox
import ru.itmo.qcore.DBox
import kotlin.math.abs


interface Represent {
    fun represent(): String
}

fun Double.format(digits: Int): String {
    val value = if (abs(this) < 0.000000001) {
        //return List(digits + 3) { ' ' }.fold(""){a, b -> a + b}
        0.0
    } else this
    return if (value >= 0.0) {
        " "
    } else {
        ""
    } + String.format("%.${digits}f", value)
}

fun Double.format(): String = format(2)

fun Int.log2(): Int = if (this == 1) {
    0
} else 1 + (this / 2).log2()

fun CBox.toDBox(): DBox = DBox(value.toCartesian().real)