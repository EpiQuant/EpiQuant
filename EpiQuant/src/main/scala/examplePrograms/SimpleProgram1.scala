package examplePrograms

import commandline._
import ArgCollections._

object SimpleProgram1 extends CommandLineProgram {
  val properties = Properties(
      name = "SimpleProgram1",
      shortDescription = "A short description of the program"
  )

  val flagSet = FlagSet(List(sparkFlags, threadFlags),
    List(
      new ValueOpt[Int]("input", "i", required = true,
                         description = "This is a very long description of the tool in question..................the description",
                         validInputTypes = "<Int>"
                        ),
      new ValueOpt[String]("output", "o", required = true,
                         description = "This is the description of the output",
                         validInputTypes = "<String>"
                        ),
      new ValueOpt[String]("optionalValue",
                         description = "This value is optional",
                         validInputTypes = "<String>"
                        ),
      new SwitchOpt("help", "h",
                  description = "print the help thing",
                  default = "false"
                 )
    )
  )
  
  def programMain(args: Array[String]) {
    
   val fakeArgs = Array("--input", "abe", "--output", "~/ro")
   val parsedArgs = ArgParser.parseArgs(fakeArgs, flagSet)
   
   println(parsedArgs("input").getValues(0).getClass())
    
  } 
}