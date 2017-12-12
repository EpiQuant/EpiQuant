package examplePrograms

import commandline._
import ArgCollections._

object SimpleProgram1 extends CommandLineProgram {
  val properties = Properties(
      name = "SimpleProgram1",
      shortDescription = "A short description of the program"
  )

  val flagSet = new FlagSet(sparkFlags, threadFlags) (
    Opt[String]("input", "i", required = true,
                description = "This is a very long description of the tool in question..................the description",
                validInputs = "<String>",
                default = "~/po.txt"
               ),
    Opt[String]("output", "o", required = true,
                description = "This is the discription of the output",
                validInputs = "<String>"
               ),
    Opt[String]("optionalValue",
                description = "This value is optional",
                validInputs = "<String>"
               )
  )
  
  def programMain(args: Array[String]) {
    println(flagSet.get("input").longName)
  } 
}