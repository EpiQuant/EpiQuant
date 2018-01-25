 
/*
  // List of command-line programs
  val programs: List[CommandLineProgram] = List(SimpleProgram1)
  
  def main(args: Array[String]) {
   // programs.foreach (_.displayHelp())
   
    SimpleProgram1.programMain(args)

  }


package examplePrograms
*/
package examplePrograms

import commandline._
import commandline.ArgParser
import commandline.ArgCollections._

import scala.collection.mutable.ListBuffer
import scala.collection.generic.GenericTraversableTemplate
import scala.collection.generic.IndexedSeqFactory
import scala.collection.generic.GenericCompanion

/*
sealed trait TypeConverter[T] { val convert: (String => T) }

object TypeConverter { 
  implicit object IntConverter    extends TypeConverter[Int]    { val convert = (t: String) => t.toInt    }
  implicit object StringConverter extends TypeConverter[String] { val convert = (t: String) => t          }
  implicit object DoubleConverter extends TypeConverter[Double] { val convert = (t: String) => t.toDouble }
}


/*
 *  The option class
 */
class Opt[T](val name: String, val values: ListBuffer[T] = ListBuffer.empty[T])(implicit converter: TypeConverter[T]) {}

// Extending with the indexedSeqFactory allows the nested type to be flexible in a list (Just like in a scala Vector)
object OptSet extends IndexedSeqFactory[OptSet]

/**
 *  The option collection class
 *  
 *  Extending the GenericTraversableTemplate trait allows the nested type to be flexible
 */
class OptSet[T](val optList: Opt[T]*)(implicit converter: TypeConverter[T]) extends Seq[T] with GenericTraversableTemplate[T, OptSet] {
  override def companion: GenericCompanion[OptSet] = OptSet
  
  def copy(): OptSet[T] = new OptSet(optList:_*)
  //def apply(opts: Opt[T]*) = new OptSet(opts:_*)
  // Called for side-effect: adds a value to a ListBuffer
  def addValue(name: String, value: String): Unit = {
    // This is just an example, if no match is found, this function doesn't do anything
    optList.foreach{ opt =>
      if (opt.name == name) {
        opt.values += converter.convert(value)
      }
    }
  }
}

object Parser {
  def parseArgs[T: TypeConverter](nameValuePairs: Array[(String, String)], optList: OptSet[T]): OptSet[T] = {
    val copy = optList.copy()
    
    nameValuePairs.foreach{ pair => 
      copy.addValue(pair._1, pair._2)
    }
    return copy
  }
}
*/
object mainProgram {

	  // List of command-line programs
  val programs: List[CommandLineProgram] = List(SimpleProgram1)
  
  def main(args: Array[String]) {
   // programs.foreach (_.displayHelp())
   
    SimpleProgram1.programMain(args)

  }
/*
  val a = new Opt[Int]("intArg")
  val b = new Opt[String]("stringArg")
  val c = new Opt[Double]("po")
  
  val set = OptSet(a, b, c)
  val iset = IndexedSeq(a, b, c)
  val vset = Vector(a, b, c)

  val as = Vector(Vector("po"), Vector(9))
  
  val flagSet = ArgCollections.sparkFlags
  
*/  
  
  
  
  
  
}

    
