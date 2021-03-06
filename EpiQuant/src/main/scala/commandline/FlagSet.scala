package commandline

import scala.collection.generic.IndexedSeqFactory
import scala.collection.generic.GenericCompanion
import scala.collection.generic.GenericTraversableTemplate

/*
 *  Extending with the IndexedSeqFactory (along with GenericTraversableTemplate) allows the nested type to be 
 *  flexible in a list (Just like in a scala Vector)
 */
object FlagSet extends IndexedSeqFactory[FlagSet] {
  /**
   * Prints a neatly formatted summary of a given option
   */
  def printOptSummary(option: Opt) {
    // Prints the long name with a '--' before it and the short (if available) with a '-'
    option.shortName match {
      case "" => println("  --" + option.longName)
      case s  => println("  --" + option.longName + ", -" + s)
    }
    // Print the description
    println("      Description: " + option.description)
    
    // Print the valid inputs and default values if it is a ValueOpt, or default values only otherwise
    option match {
      case valOpt: ValueOpt[_] => {
        valOpt.default match {
          case "" => println("      Input: " + valOpt.validInputTypes + "\n")
          case d  => println("      Input: " + valOpt.validInputTypes + " { default: " + d + " }\n")
        }
      }
      case sOpt: SwitchOpt => println("      Default: " + sOpt.default + "\n")
    }
  }
}

class FlagSet[T](val flagSets: List[FlagSet[T]], val loneFlags: List[Opt])(implicit converter: TypeConverter[T]) 
    extends Iterable[T] with GenericTraversableTemplate[T, FlagSet] {
  
  override def companion: GenericCompanion[FlagSet] = FlagSet

  //def apply(flagSets: FlagSet[T]*)(flags: Opt*) = new FlagSet(flagSets:_*)(flags:_*)
  
  /**
   * Return the Opt class with a given flag name
   *   The flag's longName must be used with this method
   */
  def apply(flagName: String): Opt = {
    val flag = flags.find(f => f.longName == flagName)
    flag match {
      case Some(i) => {
        if (i.isInstanceOf[ValueOpt[_]]) i.asInstanceOf[ValueOpt[_]]
        else i.asInstanceOf[SwitchOpt]
      }
      case None => throw new Error(flag + " is not a valid flag")
    }
  }
  //def iterator() = flags.iterator
  private[commandline] val flags: Seq[Opt] = flagSets.flatMap(_.flags) ++ loneFlags

  def copy(): FlagSet[Opt] = FlagSet(List.empty[Opt], List(flags:_*))
  

  /* Map where keys are flag's short names and values are the corresponding long names
   *   This is a lazy val, meaning that if no short names ever need to be converted to its long name, 
   *   this map will not be created
   */   
  private lazy val flagNameMap = {
    val tupleList = new collection.mutable.ListBuffer[(String, String)]
    flags.foreach(flag => {
      // Add the short and long names to list only if the shortName is not an empty string
      if (flag.shortName != "") tupleList += Tuple2(flag.shortName, flag.longName)
    })
    // Turn the tuple list into a map
    tupleList.toMap
  }
  
  /**
   * Looks up a flag's long name given its short name, and throws an error if the shortname is not found
   *   Assumes the leading '-' are already trimmed off
   */
  def lookupLongName(shortName: String): String = {
    try {
      flagNameMap(shortName)
    }
    catch {
      case e: NoSuchElementException => throw new Error(shortName + " is not a valid flag")
    }
  }
  
  private val line = "=============================="
  private val printBlock = (s: String) => println(" " + s + "\n" + line)

  /**
   * Determines whether the flag exists
   *   Works with both long and short flag names, but assumes leading '-' characters are trimmed off
   */
  def flagExists(flagName: String): Boolean = {
    val longNames = flags.map(_.longName)
    val shortNames = flags.map(_.shortName)
    if ((longNames ++ shortNames).contains(flagName)) true else false
  }
  
  /**
   * Turn a SwitchOpt from disabled to enabled
   *   Will through an error if called on a ValueOpt (which has no enabled/disabled switch)
   * 
   *   This function is called for its side-effect
   */
  private[commandline] def flipSwitchValue(flagName: String): Unit = {
    val option: Option[Opt] = flags.find(x => x.longName == flagName)
    val verifiedOpt: SwitchOpt = {
      option match {
        // Throw error if a switch option is followed by values
        case Some(i) if i.isInstanceOf[ValueOpt[_]] => throw new Error("The " + flagName + " flag does not take values")
        case Some(i) => i.asInstanceOf[SwitchOpt]
        case None => throw new Error(flagName + " is not a valid flag name")
      }
    }
    // (THIS IS A SIDE-EFFECT, as it is reassigning to the enabled variable)
    verifiedOpt.enabled = true
  }
  
  /**
   * Store a value with the option with a given flag name; also ensures value is of the right type 
   *   The flag's long name must be used with this method
   *   This method will throw an Error if called on a SwitchOpt (which should not have any values)
   *   
   *   This function is called for its side effect
   */
  private[commandline] def addValueToFlag[T](flagName: String, value: String)(implicit converter: TypeConverter[T]): Unit = {
    val option: Option[Opt] = flags.find(x => x.longName == flagName)
    val verifiedOpt: ValueOpt[T] = {
      option match {
        // Throw error if a switch option is followed by values
        case Some(i) if i.isInstanceOf[SwitchOpt] => throw new Error("The " + flagName + " does not take values")
        case Some(i) => i.asInstanceOf[ValueOpt[T]]
        case None => throw new Error(flagName + " is not a valid flag name")
      }
    }
    // Try to add this value to the flags list of values (will throw error if value is not castable to the correct type)
    //   (THIS IS A SIDE-EFFECT, as it is adding a value to a mutable collection)
    try {
      val v = value
      verifiedOpt.addValue(value)
    }
    catch {
      case e: Error => throw new Error
    }
  }
  
  /**
   * Prints the usage of all of a neatly formatted format
   */
  def printUsage {
    val valueFlags = flags.filter(_.isInstanceOf[ValueOpt[_]]).map(_.asInstanceOf[ValueOpt[_]])
    val switchFlags = flags.filter(_.isInstanceOf[SwitchOpt]).map(_.asInstanceOf[SwitchOpt])
    
    val required = valueFlags.filter(_.required)
    // All switchFlags are optional
    val optional: Seq[Opt] = switchFlags ++ valueFlags.filterNot(_.required)
    
    // If there are no optional flags, do not print the Required and Optional labels
    if (optional.isEmpty) required.foreach(FlagSet.printOptSummary(_))
    else {
      printBlock("Required")
      required.foreach(FlagSet.printOptSummary(_))
      printBlock("Optional")
      optional.foreach(FlagSet.printOptSummary(_))
    }
  }
}