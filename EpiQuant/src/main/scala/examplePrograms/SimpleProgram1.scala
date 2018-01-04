package examplePrograms

import commandline._
import ArgCollections._

object SimpleProgram1 extends CommandLineProgram {
  val properties = Properties(
      name = "SimpleProgram1",
      shortDescription = "A short description of the program"
  )

  val flagSet = new FlagSet()(//sparkFlags, threadFlags) (
    new ValueOpt[String]("input", "i", required = true,
                description = "This is a very long description of the tool in question..................the description",
                validInputTypes = "<String>",
                default = "~/po.txt"
               ),
    new ValueOpt[String]("output", "o", required = true,
                description = "This is the description of the output",
                validInputTypes = "<String>"
               ),
    new ValueOpt[String]("optionalValue",
                description = "This value is optional",
                validInputTypes = "<String>"
               ),
    SwitchOpt("help", "h",
              description = "print the help thing",
              default = "false"
             )
  )
  
  def programMain(args: Array[String]) {
    
   val fakeArgs = Array("-h", "--input", "~/po", "--output", "~/ro")
   val parsedArgs = ArgParser.parseArgs(fakeArgs, flagSet)
   
   println(parsedArgs.get("input"))
    
  } 
}