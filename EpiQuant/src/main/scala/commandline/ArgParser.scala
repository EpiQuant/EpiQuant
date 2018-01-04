package commandline

import collection.mutable.ListBuffer

/*
  1. Find flag
  2. Determine whether it is a short or long flag
  3. See if flag is expected in the flag set
        if not, then throw exception
  4. Gather all flag entries (iterate until another flag is found)
  5. Validate that all of the entries can be cast to the correct type
  6. Add to the list in the value field of the parsed set
 */

object ArgParser {
  
  def parseArgs(args: Array[String], flagSet: FlagSet): FlagSet = {
    val flagSetCopy = flagSet.copy()
    val collectedFlags = collectFlagsWithValues(args)
    
    // Parse all of the flags
    collectedFlags.foreach(flag => {
      val flagName = flag._1
      val flagValues = flag._2
      
      val flagLongName = {
        // Trim the leading '--' or '-' off depending on the type of flag
        if (isLongFlag(flagName)) flagName.drop(2)
        else if (isShortFlag(flagName)) flagSet.lookupLongName(flagName.drop(1))
        else throw new Error(flagName + " is not a valid flag")
      }
      // Get the type of the Opt (requires looking up the actual opt from just the long name)
      val flagOpt: Opt = flagSetCopy.get(flagLongName)
      flagOpt match {
        // Opt is a sealed class, so these cases are exhaustive
        case switchOpt: SwitchOpt => {
          // Perform check on the SwitchOpt
          if (!flagValues.isEmpty) throw new Error("The switch " + flagName + " cannot be followed by values")
          /*
           *  Enable this switch flag
           */
          else flagSetCopy.flipSwitchValue(flagLongName)
        }
        case valOpt: ValueOpt[_] => {
          if (flagValues.length == 0) throw new Error("The " + flagName + "flag needs to be followed by a value")
          else if (flagValues.length > 1 && !valOpt.multipleEntriesAllowed) {
            throw new Error("The " + flagName + " does not accept multiple values")
          }
          else {
            // Indicate this flag was present
            valOpt.flagWasPresent = true
            // Add all of the values to the flagSetCopy
            addValuesToFlagSet(flagName, flagSetCopy, flagValues)
          }
        }
      }
    })
    // Verify that all of the required ValueOpts were present
    flagSetCopy.flags.foreach(flag => {
      flag match {
        case f: SwitchOpt => // do nothing, as switch opts are never required
        case f: ValueOpt[_] => if (f.required && !f.flagWasPresent) throw new Error("Required flag: " + flag.longName + " was absent")
      }
    }) 
    // Return the copy of the flagSet, which now has all of the values filled in and switches enabled
    return flagSetCopy
  }
  
  protected def isLongFlag(arg: String): Boolean = if (arg.startsWith("--")) true else false
  protected def isShortFlag(arg: String): Boolean = if (arg.startsWith("-") && !isLongFlag(arg)) true else false
  protected def isFlag(arg:String): Boolean = if (isLongFlag(arg) || isShortFlag(arg)) true else false
  
  /**
   * Takes an array of strings with flags followed by values, and return a List of tuples where (flagName, List(values))
   *   If the flag is simply an "on/off" switch, the value list will be empty
   */
  private def collectFlagsWithValues(args: Array[String]): List[(String, List[String])] = {
    val argsIterator = args.iterator.buffered
    val flagValuePairs: ListBuffer[(String, List[String])] = ListBuffer()
    
    while (argsIterator.hasNext) {
      /*
       * This will ensure that the first argument encountered must be a flag (after 1 iteration of this while 
       *   loop, it is obvious that the first flag is not a value, as that is what causes the internal loop 
       *   to terminate in the first place
       */
      val currentFlag = argsIterator.next()
      if (!isFlag(currentFlag)) throw new Error(currentFlag + " is not a valid flag")
         
      val currentValues: ListBuffer[String] = ListBuffer()
      
      // The head function on a buffered iterator lets one "peek" at the upcoming value without advancing the iterator
      // This collects all of the values for the current flag
      while (argsIterator.hasNext && !isFlag(argsIterator.head) ) currentValues += argsIterator.next()
      
      // Add the (flag, values) tuple to the flagValuePairs collection
      flagValuePairs += Tuple2(currentFlag, currentValues.toList)
    }
    return flagValuePairs.toList
  }

  /**
   *  Method is called on each chuck of the input arguments, i.e. the flag and all non-flag
   *    values that follow it
   *  The method ensures the values are an appropriate type for the flag given, and
   *    adds the values to the FlagSet
   *  
   */
  private def addValuesToFlagSet(flag: String, flagSet: FlagSet, flagValues: List[String]): Unit = {
    // Throw error if flag is not a valid flag
    if (!isFlag(flag)) throw new Error(flag + " is not a valid flag")
    
    val flagLongName = {
      flag match {
        // Trim long name
        case f if (isLongFlag(f)) => flag.drop(2)
        // Convert short name to long name
        case f => {
          val shortTrimmed = f.drop(1)
          flagSet.lookupLongName(shortTrimmed)
        }
      }
    }
    
    // Throw error if the flag does not exist for this FlagSet
    if (!flagSet.flagExists(flagLongName)) throw new Error(flag + " flag does not exist for the given set of flags")
    
    // Try to add each value to the Opt in the FlagSet (will throw an Error if a value is not the correct type)
    flagValues.foreach( value => flagSet.addValueToFlag(flagLongName, value) )
  }
}