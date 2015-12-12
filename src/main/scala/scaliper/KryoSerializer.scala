package scaliper

import scala.reflect._

import com.esotericsoftware.kryo.{Kryo, KryoException}
import com.esotericsoftware.kryo.io.{Input => KryoInput, Output => KryoOutput}
import com.twitter.chill.{AllScalaRegistrar, EmptyScalaKryoInstantiator}

object KryoSerializer {

  def kryo[T: ClassTag]: Kryo = {
    val instantiator = new EmptyScalaKryoInstantiator
    val kryo = instantiator.newKryo()
    val classLoader = Thread.currentThread.getContextClassLoader

    kryo.register(classTag[T].runtimeClass)

    new AllScalaRegistrar().apply(kryo)

    kryo.setClassLoader(classLoader)
    kryo
  }

  private lazy val input = new KryoInput()
  private lazy val output = new KryoOutput(64000, 64000000)

  def serializeToFile[T: ClassTag](t: T, p: String): Unit = {
    val s = new String(serialize(t).map(_.toChar))
    IO.write(p, s)
  }

  def deserializeFromFile[T: ClassTag](p: String): T = {
    val s = IO.read(p)
    deserialize(s.toCharArray.map(_.toByte))
  }

  def serialize[T: ClassTag](t: T): Array[Byte] = {
    output.clear()
    try {
      kryo[T].writeClassAndObject(output, t)
    } catch {
      case e: KryoException if e.getMessage.startsWith("Buffer overflow") =>
        throw new Exception(s"Kryo serialization failed: ${e.getMessage}. To avoid this, " +
          "increase spark.kryoserializer.buffer.max value.")
    }

    output.toBytes
  }

  def deserialize[T: ClassTag](bytes: Array[Byte]): T = {

    input.setBuffer(bytes)
    kryo[T].readClassAndObject(input).asInstanceOf[T]
  }

}

