import ru.itmo.qcore.*
import ru.itmo.qcore.utils.DPTensor.*
import ru.itmo.qcore.utils.DPTensorWrap
import ru.itmo.qcore.utils.ZPTensor.*
import java.lang.Math.cos
import java.lang.Math.sin
import kotlin.math.PI
import kotlin.math.sqrt

fun main() {
    val t: Tensor<IBox> = listOf(listOf(1, 0), listOf(1, 1)).toTensor(::IBox)
    val i: Tensor<IBox> = listOf(listOf(1, 0), listOf(0, 1)).toTensor(::IBox)
    val j: Tensor<IBox> = listOf(listOf(0, 1), listOf(1, 0)).toTensor(::IBox)
    val m = { a: Double -> FBox<DBox>("x -> ax") { DBox(a * it.value) } }
    val x = FBox<DBox>("cos(x)") { DBox(cos(it.value)) }
    val y = FBox<DBox>("sin(x)") { DBox(sin(it.value)) }
    val F = listOf(listOf(x, y), listOf(y, x)).toTensor()
    println(F.represent())
    println("TENSOR FUCKING MULT WITH")
    println(F.transpose().represent())
    println(" |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |  |")
    println(" V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V  V")
    println((F mult F.transpose()).represent())
    println((i mult j).represent())
    println(I.toTensor().represent())
    println((H mult H mult H.toTensor()).map(CBox::toCartesian).represent())
    println((DPTensorWrap(R, fromDouble(PI / 4))).toTensor().map(CBox::toCartesian).represent())
    println(H.represent())
    val v: Vector<CBox> = listOf(1.0,0.0).toVec().map(::fromDouble)
    val k = H.toTensor().map(::const)(v)
    println(v.represent())
    println(k.represent())
    val a = listOf(sqrt(3.0)/2.0, 0.5).toVec().map(::fromDouble)
    val Q = (E mult CNOT)*(CNOT mult E)*(N mult E mult E)*(((N mult N) * CNOT) mult E)*(E mult ((N mult N) * CNOT))
    println(Q.map(::const)(a mult v mult v).measure(2).first.measure(0).first.represent())
    println(Q.map(::const)(a mult v mult v).represent())
    val b = (a mult v mult v).map(CBox::toCartesian).measure(1).first
    val c = b.measure(1).first
    val d = c.measure(0).first
    println("2432")
    println((a mult v mult v).represent())
    println(b.represent())
    println(c.represent())
    println(d.represent())
    println((a mult v mult v).map(CBox::toCartesian).measure(0).first)
    println((H(k) mult H(k)).map(CBox::toCartesian).measure(2).second.first)
    println((H mult H).map(::const)(k mult k).represent())
    println(fromDouble(1.0).toCartesian().value.represent())
    println(fromDouble(1.0).toRadial().value.represent())

    /*
       (a b)(c d) = (ac bc ad bd) => (a + b)c (a + b)d => (a^2 + b^2)c^2 + (a^2 + b^2)d^2 => (c^2 d^2)
       (1 0) => (E 0)
       (0 1) => (0 E)
     */

}