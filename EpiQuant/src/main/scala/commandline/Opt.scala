package commandline

import scala.collection.mutable.ListBuffer
import scala.reflect.{ClassTag, classTag}
import scala.reflect.runtime.universe._

/**
 * Sealed class that ValueOpt and SwitchOpt extend
 */
abstract sealed class Opt {
  val longName: String
  val shortName: String
  val description: String
  val default: String
  
  /**
   * Returns the values for an option
   *   For the SwitchOpt, it returns a List with only one entry: the on/off Boolean value
   */
  def getValues: List[_]
}

/**
 * Option (flag) that must be followed by values
 */
class ValueOpt[T : TypeConverter] (
  val longName: String,
  val shortName: String = "",
  val description: String,
  val default: String = "",
  // class specific
  val required: Boolean = false,
  val validInputTypes: String,
  val multipleEntriesAllowed: Boolean = false,
  private[commandline] val values: ListBuffer[T] = ListBuffer.empty[T],
  var flagWasPresent: Boolean = false 
) extends Opt {
    
  /**
   * Return the values associated with this flag
   */
  def getValues: List[_] = values.toList.map(_.asInstanceOf[T])
  
  /**
   * Adds an input string to this options values list after converting it to the proper type (if possible)
   */
  private[commandline] def addValue(value: String)(implicit converter: TypeConverter[T]): Unit = {
    val neededType = converter.thisType
    try {
      // Add the casted value to the list of values for this option
      // (THIS IS A SIDE-EFFECT, as it is adding a value to a mutable collection)
      values += converter.convert(value)
    }
    catch {
      case e: NumberFormatException => {
        throw new Error("Cannot cast " + value.toString() + "; " + longName + " requires values of type " + neededType.toString)
      }
    }
  }
}

/**
 * Option (flag) that acts as an "on/off switch", i.e., the flag is not followed by values, but is either
 *   enabled or disabled
 *   (--help/-h is a good example)
 */
class SwitchOpt(
  val longName: String,
  val shortName: String = "",
  val description: String,
  val default: String = "",
  // case specific
  private[commandline] var enabled: Boolean = false // This variable stores whether this switch is enabled or not
) extends Opt {
    /**
     * Returns a list with 1 item, a boolean which states whether this switch is enabled or not 
     */
    def getValues: List[Boolean] = List(enabled)
}
