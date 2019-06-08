package ru.itmo.qcore

import ru.itmo.qcore.Complex.*
import ru.itmo.qcore.utils.Represent
import ru.itmo.qcore.utils.format
import java.lang.Math.*
import kotlin.math.atan2

interface Ring<T> : SemiRing<T> {
    fun multN(): T
    fun addN(): T
    operator fun unaryMinus(): T
}

interface SemiRing<T> {
    operator fun times(r: T): T
    operator fun plus(r: T): T
}

operator fun <T : Ring<T>> T.minus(other: T): T = this + (-other)

sealed class Complex : Represent {
    class Cartesian(val real: Double, val im: Double) : Complex()
    class Radial(val r: Double, val phi: Double) : Complex()

    override fun represent(): String = when (this) {
        is Cartesian -> "${real.format()} + ${im.format()}i"
        is Radial -> "${r.format()}*e$${phi.format()}i"
    }

    fun toCartesian(): Cartesian = when (this) {
        is Cartesian -> this
        is Radial -> Cartesian(r * cos(phi), r * sin(phi))
    }

    fun toRadial(): Radial = when (this) {
        is Cartesian -> Radial(sqrt(real * real + im * im), atan2(im, real))
        is Radial -> this
    }
}

val i = CBox(Cartesian(0.0, 1.0))

class CBox(val value: Complex) : TensorValue<CBox> {
    fun abs(): Double = value.toRadial().r

    fun pow(p: Double): CBox = if ((this - addN()).abs() < 0.01) {
        addN()
    } else {
        val t = value.toRadial()
        CBox(Radial(pow(t.r, p), t.phi / p))
    }

    override fun unaryMinus(): CBox {
        val c = value.toCartesian()
        return CBox(Cartesian(-c.real, -c.im))
    }

    fun toCartesian(): CBox = CBox(value.toCartesian())

    fun toRadial(): CBox = CBox(value.toRadial())

    override fun multN(): CBox = CBox(Cartesian(1.0, 0.0))

    override fun addN(): CBox = CBox(Cartesian(0.0, 0.0))

    override fun times(r: CBox): CBox {
        val a = value.toRadial()
        val b = r.value.toRadial()
        return CBox(Radial(a.r * b.r, a.phi + b.phi))
    }

    override fun plus(r: CBox): CBox {
        val a = value.toCartesian()
        val b = r.value.toCartesian()
        return CBox(Cartesian(a.real + b.real, a.im + b.im))
    }

    override fun represent(): String = value.toCartesian().represent()
}

fun fromDouble(d: Double): CBox = CBox(Cartesian(d, 0.0))

class IBox(val value: Int) : TensorValue<IBox> {
    override fun unaryMinus(): IBox = IBox(-value)

    override fun represent(): String = value.toString()

    override fun addN(): IBox = IBox(0)

    override fun multN(): IBox = IBox(1)

    override fun plus(r: IBox): IBox = IBox(value + r.value)

    override fun times(r: IBox): IBox = IBox(value * r.value)
}

class DBox(val value: Double) : TensorValue<DBox> {
    override fun unaryMinus(): DBox = DBox(-value)

    override fun represent(): String = value.format()

    override fun multN(): DBox = DBox(1.0)

    override fun addN(): DBox = DBox(0.0)

    override fun plus(r: DBox): DBox = DBox(value + r.value)

    override fun times(r: DBox): DBox = DBox(value * r.value)
}

class FBox<B : Ring<B>>(val r12n: String, val value: (B) -> B) :
    TensorValue<FBox<B>> {
    override fun unaryMinus(): FBox<B> = FBox("-$r12n") { -value(it) }

    override fun represent(): String = r12n

    override fun multN(): FBox<B> = FBox("Z") { it.multN() }

    override fun addN(): FBox<B> = FBox("O") { it.addN() }

    override fun plus(r: FBox<B>): FBox<B> = FBox(
        "(" + r12n + " + " + r.r12n + ")"
    ) { value(it).plus(r.value(it)) }

    override fun times(r: FBox<B>): FBox<B> = FBox(
        r12n + " * " + r.r12n
    ) { value(it).times(r.value(it)) }
}

class SBox(val value: String) : TensorValue<SBox> {
    override fun unaryMinus(): SBox = SBox("")

    override fun multN(): SBox = addN()

    override fun addN(): SBox = SBox("")

    override fun times(r: SBox): SBox = plus(r)

    override fun plus(r: SBox): SBox = SBox(value + r.value)

    override fun represent(): String = value
}

fun <B : Ring<B>> const(p: B): FBox<B> = FBox("(x*$p)") { x -> x * p }