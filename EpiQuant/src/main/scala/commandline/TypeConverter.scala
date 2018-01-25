package commandline

import scala.reflect.runtime.universe.{typeTag, TypeTag}

sealed trait TypeConverter[T] {
  val thisType: TypeTag[T]
  val convert: (String => T)
}

/**
 * Takes a string and converts it to an Int, Float, Double, Long, or String depending on the context in which the
 *   type converter is called
 * 
 * Follows Scala's so-called "Type-class Implicits" Design Pattern" as described here:
 *   http://www.lihaoyi.com/post/ImplicitDesignPatternsinScala.html
 */
object TypeConverter { 
  implicit object IntConverter extends TypeConverter[Int] {
    val thisType = typeTag[Int]
    val convert = (t: String) => t.toInt
  }
  implicit object UnitConverter extends TypeConverter[Unit] {
    val thisType = typeTag[Unit]
    val convert = (t: String) => "Unit"
  }
  /*implicit object FloatConverter extends TypeConverter[Float] {
    val thisType = typeTag[Float]
    val convert = (t: String) => t.toFloat
  }
  implicit object DoubleConverter extends TypeConverter[Double] {
    val thisType = typeTag[Double]
    val convert = (t: String) => t.toDouble
  }
  implicit object LongConverter extends TypeConverter[Long] {
    val thisType = typeTag[Long]
    val convert = (t: String) => t.toLong
  }*/
  implicit object StringConverter extends TypeConverter[String] {
    val thisType = typeTag[String]
    val convert = (t: String) => t.toString
  }
  /*implicit object BigDecimalConverter extends TypeConverter[BigDecimal] {
    val thisType = typeTag[BigDecimal]
    val convert = (t: String) => BigDecimal(t)
  }*/
}