package commandline

sealed trait ProgramCategory
case object Supported extends ProgramCategory
case object Experimental extends ProgramCategory
case object Hidden extends ProgramCategory

/**
 * Information about WHAT the program is
 */
case class Properties(
  name: String,
  shortDescription: String,
  detailedDescription: String = "",
  displayWithMainHelpFlag: Boolean = true,
  category: ProgramCategory = Supported
)

/**
 *  Trait that each command-line program should extend or mix in
 *  
 *  We have a case class field that holds properties.
 *  We use a case class instead of directly storing fields so that we can pattern match against the properties
 */
trait CommandLineProgram {
  
  // What the program is
  val properties: Properties

  // What flags can be given
  val flagSet: FlagSet[Opt]
 
  // The program's "main" function
  def programMain(args: Array[String]): Unit
  
  def displayHelp() = {
    val line = "============================================================"
    val printBlock = (s: String) => println(line + "\n " + s + "\n" + line + "\n Usage:\n")
    
    printBlock(properties.name)
    flagSet.printUsage
  }
}
