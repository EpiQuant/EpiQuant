package examplePrograms

import commandline._
import ArgCollections._
import commandline.CommandLineProgram

object SimpleProgram2 extends CommandLineProgram {
  val properties = Properties(
      name = "SimpleProgram2",
      shortDescription = "Another short description"
  )
      
  val flagSet = new FlagSet(sparkFlags) (
    Opt[String]("inputX", "i", required = true,
                description = "This is a very long description of the tool in question the description",
                validInputs = "<String>",
                default = "~/po.txt"
    ),
    Opt[String]("outputX", "o", required = true,
                description = "This is the discription of the output",
                validInputs = "<String>"
    ), Opt[String]("optionalValueX", required = true,
                description = "This value is optional",
                validInputs = "<String>"
    )
  )
  
  def programMain(args: Array[String]) {
    println(flagSet.get("sparkMaster").longName)
  } 

}