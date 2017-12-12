package commandline

import exceptions.InvalidInputArgumentException

object ArgParser {

 

  protected def isLongFlag(arg: String): Boolean = if (arg.startsWith("--")) true else false
  protected def isShortFlag(arg: String): Boolean = if (arg.startsWith("-") && !isLongFlag(arg)) true else false
  protected def isFlag(arg:String): Boolean = if (isLongFlag(arg) || isShortFlag(arg)) true else false

  /*def parseArgs(args: Array[String], flagSet: FlagSet): FlagSet = {
    val parsedSet = flagSet.clone()

    val argIterator = args.iterator

    // The very first argument must be a flag, so throw an exception if it is not
    val firstArg = argIterator.next()
    if (!isFlag(firstArg)) throw new InvalidInputArgumentException(firstArg + " is not a valid input")
    // Continue processing the flags
    else {
      if (isLongFlag(firstArg)) {
        val trimmed = firstArg.drop(2)
          flagSet.get(trimmed)

      }
    }




  }
*/
  
   /*
  1. Find flag
  2. Determine whether it is a short or long flag
  3. See if flag is expected in the flag set
        if not, then throw exception
  4. Gather all flag entries (iterate until another flag is found)
  5. Validate that all of the entries can be cast to the correct type
  6. Add to the list in the value field of the parsed set

   */

  def validateFlag(flag: String, flagSet: FlagSet, flagValues: List[Any]): Unit = {
    if (isLongFlag(flag)) {
      val trimmed = flag.drop(2)
      if (flagSet.flagExists(flag)) {
        
      }
      else throw new Error("Flag does not exist")
    }



  }

}