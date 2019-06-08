package ru.itmo.qcore

import ru.itmo.qcore.utils.Represent
import ru.itmo.qcore.Vector.Empty
import ru.itmo.qcore.Vector.NonEmpty

interface TensorValue<T> : Ring<T>, Represent

interface Tensorable<T : TensorValue<T>>: Represent {
    fun toTensor(): Tensor<T>

    override fun represent(): String = toTensor().represent()

    operator fun plus(other: Tensorable<T>): Tensor<T> = toTensor() + other.toTensor()

    operator fun times(other: Tensorable<T>): Tensor<T> = toTensor() * other.toTensor()

    infix fun mult(other: Tensorable<T>): Tensor<T> = toTensor() mult other.toTensor()
}

class Tensor<T : TensorValue<T>>(val value: Vector<Vector<T>>) : Tensorable<T> {
    override fun plus(other: Tensorable<T>): Tensor<T> =
        Tensor(toTensor().value.biZipWithUnsafe(other.toTensor().value, { w, v -> w + v }, { v -> v }, { w -> w }))

    override fun times(other: Tensorable<T>): Tensor<T> {
        val t = this.toTensor()
        return when {
            t.value is NonEmpty && t.value.x is NonEmpty -> Tensor(t.value
                .map { v ->
                    other.toTensor().value
                        .map { w ->
                            v
                                .zipWith(w) { i, j -> i.times(j) }
                                .foldr(t.value.x.x.addN())
                                { i, acc -> i.plus(acc) }
                        }
                })
            else -> Tensor(Empty)
        }
    }

    override fun mult(other: Tensorable<T>): Tensor<T> = Tensor(toTensor().value
        .map { v -> v.map { e -> other.toTensor().map { k -> e.times(k) } } }
        .map { v ->
            v
                .foldr(Tensor(Empty as Vector<Vector<T>>))
                { m, n -> m + n }.value
        }
        .foldr(Empty as Vector<Vector<T>>)
        { v, w -> v + w }
    )


    override fun toTensor(): Tensor<T> = this

    override fun represent(): String =
        map { a -> SBox(a.represent()) }
            .value
            .map { v ->
                SBox("| ")
                    .plus(v
                        .foldr(SBox(""))
                        { a, b -> SBox("${a.value} | ${b.value}") })
            }
            .foldr(SBox(""))
            { v, acc -> SBox("${v.value}\n${acc.value}") }.value


    fun transpose(): Tensor<T> {
        return when (value) {
            Empty -> Tensor(Empty)
            is NonEmpty -> Tensor(
                value.x.zipWithUnsafe(
                    Tensor(value.xs).transpose().value,
                    { e, v -> NonEmpty(e, v) },
                    { e -> NonEmpty(e, Empty) }
                )
            )
        }
    }
}

fun <A : TensorValue<A>, B : TensorValue<B>> Tensor<A>.map(f: (A) -> B): Tensor<B> =
    Tensor(value.map { v -> v.map(f) })

fun <T : TensorValue<T>> List<List<T>>.toTensor(): Tensor<T> =
    Tensor((map { e -> e.toVec() }).toVec())

fun <R, T : TensorValue<T>> List<List<R>>.toTensor(boxer: (R) -> T): Tensor<T> =
    Tensor((map { e -> e.toVec().map(boxer) }).toVec())


fun <T : TensorValue<T>> Tensor<T>.toList(): List<List<T>> = value.map { e -> e.toList() }.toList()


operator fun <B : Ring<B>> Tensor<FBox<B>>.invoke(arg: Vector<B>): Vector<B> = when (arg) {
    is NonEmpty -> value.map { v -> v.zipWith(arg) { f, e -> f.value(e) }.foldr(arg.x.addN()) { a, b -> a + b } }
    else -> Empty
}

