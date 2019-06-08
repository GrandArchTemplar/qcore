package ru.itmo.qcore.utils

import ru.itmo.qcore.*

fun exp(c: CBox): CBox {
    val cart = c.value.toCartesian()
    return CBox(Complex.Radial(kotlin.math.exp(cart.real), cart.im))
}

fun cos(c: CBox): CBox = (exp(c * i) + exp(
    c * (-i)
)) * fromDouble(0.5)

fun sin(c: CBox): CBox = (exp(c * i) - (exp(
    c * (-i)
))) * fromDouble(-0.5) * i