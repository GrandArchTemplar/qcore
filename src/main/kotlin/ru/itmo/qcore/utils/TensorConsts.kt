package ru.itmo.qcore.utils

import ru.itmo.qcore.*
import ru.itmo.qcore.utils.DPTensor.*
import kotlin.math.sqrt

enum class ZPTensor : Tensorable<CBox> {
    E,
    N,
    CNOT,
    I,
    X,
    Y,
    Z,
    H;

    override fun toTensor(): Tensor<CBox> = when (this) {
        E, I -> listOf(
            listOf(
                fromDouble(1.0),
                fromDouble(0.0)
            ), listOf(
                fromDouble(0.0),
                fromDouble(1.0)
            )
        ).toTensor()
        N, X -> listOf(
            listOf(
                fromDouble(0.0),
                fromDouble(1.0)
            ),
            listOf(
                fromDouble(1.0),
                fromDouble(0.0)
            )
        ).toTensor()
        CNOT -> listOf(
            listOf(
                fromDouble(1.0),
                fromDouble(0.0),
                fromDouble(0.0),
                fromDouble(0.0)
            ),
            listOf(
                fromDouble(0.0),
                fromDouble(1.0),
                fromDouble(0.0),
                fromDouble(0.0)
            ),
            listOf(
                fromDouble(0.0),
                fromDouble(0.0),
                fromDouble(0.0),
                fromDouble(1.0)
            ),
            listOf(
                fromDouble(0.0),
                fromDouble(0.0),
                fromDouble(1.0),
                fromDouble(0.0)
            )
        ).toTensor()
        Y -> listOf(
            listOf(
                CBox(Complex.Cartesian(0.0, 0.0)),
                CBox(Complex.Cartesian(0.0, -1.0))
            ),
            listOf(
                CBox(Complex.Cartesian(0.0, 0.0)),
                CBox(Complex.Cartesian(0.0, 1.0))
            )
        ).toTensor()
        Z -> listOf(
            listOf(fromDouble(1.0), fromDouble(0.0)), listOf(
                fromDouble(0.0),
                fromDouble(-1.0)
            )
        ).toTensor()
        H -> listOf(
            listOf(fromDouble(1.0), fromDouble(1.0)),
            listOf(fromDouble(1.0), fromDouble(-1.0))
        ).toTensor().map { e ->
            e * CBox(
                Complex.Cartesian(
                    1 / sqrt(2.0),
                    0.0
                )
            )
        }
    }

    operator fun invoke(v: Vector<CBox>) = toTensor().map(::const)(v)
}

enum class DPTensor {
    R
}

class DPTensorWrap(val t: DPTensor, val p: CBox) :
    Tensorable<CBox> {
    override fun toTensor(): Tensor<CBox> = when (t) {
        R -> listOf(
            listOf(cos(p), sin(p)),
            listOf(-sin(p), cos(p))
        ).toTensor()
    }
}



