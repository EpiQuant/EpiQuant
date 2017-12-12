package commandline

import collection.mutable.ListBuffer

case class Opt[+T] (
  longName: String,
  shortName: String = "",
  description: String,
  validInputs: String,
  default: String = "",
  required: Boolean = false,
  multipleEntriesAllowed: Boolean = false,
  noEntries: Boolean = false, // If one would not expect values after the flag: '--help' for example
  values: ListBuffer[T] = ListBuffer()
)

object FlagSet {
  /**
   * Prints a neatly formatted summary of a given option
   */
  def printOptSummary(option: Opt[Any]) {
    option.shortName match {
      case "" => println("  --" + option.longName)
      case s  => println("  --" + option.longName + ", -" + s)
    }
    println("      Description: " + option.description)    
    option.default match {
      case "" => println("      Input: " + option.validInputs + "\n")
      case d  => println("      Input: " + option.validInputs + " { default: " + d + " }\n")
    }
  }
}

class FlagSet(flagSets: FlagSet*)(loneFlags: Opt[Any]*) extends Iterable[Opt[Any]] {
  
  def apply(flagSets: FlagSet*)(flags: Opt[Any]*) = new FlagSet(flagSets:_*)(flags:_*)
  def iterator() = flags.iterator
    
  val flags: Seq[Opt[Any]] = flagSets.flatMap(_.flags) ++ loneFlags

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
   * Return the Opt case class with a given flag name
   *   The flag's longName must be used with this method
   */
  def get(flagName: String): Opt[Any] = {
    val flag = flags.find(f => f.longName == flagName)
    flag match {
      case Some(i) => return i
      case None => throw new Error(flag + " is not a valid flag")
    }
  }
  
  def addValue(flagName: String, value: Any) = {
    val option: Option[Opt[Any]] = flags.find(x => x.longName == flagName)
    val verifiedOpt = {
      option match {
        case Some(i) => i
        case None => throw new Error(flagName + " is not a valid flag name")
      }
    }
    // Look up the type that the incoming value must be castable to
    val requiredClass = verifiedOpt.values(0).getClass()
    val castedValue = {
      if (value.getClass == requiredClass) value
      // See if the value could be cast to the acceptable value
      try {
       requiredClass.cast(value) 
      }
      catch {
        case e: ClassCastException => throw new Error(value + " has the wrong type for the " + flagName + " flag")
      }
    }
    // Add the value to the value list for the option
    verifiedOpt.values += castedValue
    
  }
  
  def printUsage {
    
    val required = flags.filter(_.required)
    val optional = flags.filterNot(_.required)
    
    // If there are no optional flags, do not print the Required and Optional labels
    if (optional.isEmpty) {
      required.foreach(FlagSet.printOptSummary(_))
    }
    else {
      printBlock("Required")
      required.foreach(FlagSet.printOptSummary(_))
      printBlock("Optional")
      optional.foreach(FlagSet.printOptSummary(_))
    }
  }
}