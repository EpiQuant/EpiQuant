package examplePrograms

import commandline._
import commandline.ArgParser

object mainProgram {
  
  // List of command-line programs
  val programs: List[CommandLineProgram] = List(SimpleProgram1)
  
  def main(args: Array[String]) {
    programs.foreach (_.displayHelp())
    
    SimpleProgram1.programMain(args)
  }  
}