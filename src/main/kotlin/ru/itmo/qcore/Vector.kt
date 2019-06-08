package ru.itmo.qcore

import ru.itmo.qcore.Vector.*
import ru.itmo.qcore.utils.Represent
import ru.itmo.qcore.utils.log2
import java.lang.Exception
import javax.xml.crypto.dom.DOMCryptoContext
import kotlin.math.abs

sealed class Vector<out A> {
    object Empty : Vector<Nothing>()
    class NonEmpty<A>(val x: A, val xs: Vector<A>) : Vector<A>()

    fun length(): Int = foldr(0) { _, b -> b + 1 }

    fun <B> map(f: (A) -> B): Vector<B> = when (this) {
        Empty -> Empty
        is NonEmpty -> NonEmpty(f(x), xs.map(f))
    }

    fun <B> foldr(acc: B, f: (A, B) -> B): B = when (this) {
        Empty -> acc
        is NonEmpty -> f(x, xs.foldr(acc, f))
    }

    fun <B, C> zipWith(other: Vector<B>, f: (A, B) -> C): Vector<C> = when (this) {
        Empty -> Empty
        is NonEmpty -> when (other) {
            is Empty -> Empty
            is NonEmpty -> NonEmpty(f(x, other.x), xs.zipWith(other.xs, f))
        }
    }

    fun <B, C> zipWithUnsafe(other: Vector<B>, f: (A, B) -> C, g: (A) -> C): Vector<C> = when (this) {
        Empty -> Empty
        is NonEmpty -> when (other) {
            is Empty -> map(g)
            is NonEmpty -> NonEmpty(f(x, other.x), xs.zipWith(other.xs, f))
        }
    }

    fun <B, C> biZipWithUnsafe(other: Vector<B>, f: (A, B) -> C, g: (A) -> C, h: (B) -> C): Vector<C> = when (this) {
        Empty -> other.map(h)
        is NonEmpty -> when (other) {
            is Empty -> map(g)
            is NonEmpty -> NonEmpty(f(x, other.x), xs.zipWith(other.xs, f))
        }
    }

    fun head(): A = when (this) {
        Empty -> throw Exception("Empty head!")
        is NonEmpty -> x
    }
}

fun <T> List<T>.toVec(): Vector<T> = if (isNotEmpty()) {
    NonEmpty(first(), drop(1).toVec())
} else Empty

fun <T> Vector<T>.toList(): List<T> = when (this) {
    Empty -> listOf()
    is NonEmpty -> listOf(x) + xs.toList()
}

fun <T : Represent> Vector<T>.represent(): String =
    map { a -> a.represent() }
        .foldr("")
        { a, b -> a + b }

infix fun <T : Ring<T>> Vector<T>.mult(r: Vector<T>): Vector<T> {
    return map { a -> r.map { b -> a.times(b) } }
        .foldr(Empty)
        { v, w: Vector<T> -> v + w }
}

fun <T> replicate(t: T): Vector<T> = NonEmpty(t, replicate(t))

operator fun <T> Vector<T>.plus(v: Vector<T>): Vector<T> = when (this) {
    Empty -> v
    is NonEmpty -> NonEmpty(x, xs + v)
}

fun Vector<CBox>.measure(n: Int): Pair<Vector<CBox>, Pair<Double, Double>> {
    val s = length().log2()
    val k = foldr(Triple(Empty as Vector<CBox>, Empty as Vector<CBox>, 0))
    { e, acc ->
        if (acc.third and (1 shl (s - n - 1)) == 0) {
            Triple(NonEmpty(e, acc.first), acc.second, acc.third + 1)
        } else
            Triple(acc.first, NonEmpty(e, acc.second), acc.third + 1)
    }
    return Pair(
        k.first.zipWith(k.second)
        { a, b -> fromDouble((a * a).abs() + (b * b).abs()) }
            .map { v -> v.pow(0.5) },
        Pair(
            k.first.foldr(0.0) { a, b -> (a * a).abs() + abs(b * b) },
            k.second.foldr(0.0) { a, b -> (a * a).abs() + abs(b * b) })
    )
}