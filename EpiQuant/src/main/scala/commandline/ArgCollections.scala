package commandline

object ArgCollections {
  
  val sparkFlags = new FlagSet()(
    new ValueOpt[String]("sparkMaster",
                         required = true,
                         description = "This is a very long description of the tool in question..................the description",
                         validInputTypes = "<String>",
                         default = "local"
    ),
    new ValueOpt[Int]("numExecutors",
                      required = true,
                      description = "This is another long of the tool in question... pfghje the description",
                      validInputTypes = "<Int>",
                      default = "local"
    )
  )
  
  val threadFlags = new FlagSet()(
    new ValueOpt[Int]("threads",
                      description = "This is a very long description of the tool in question...the description",
                      validInputTypes = "<String>",
                      default = "local"
    ),
    new ValueOpt[String]("threadType",
                         description = "This is a tool in question..dfgjhdfghetyhbr4356789...........the description",
                         validInputTypes = "<String>",
                         default = "local"
    )
  )
  
}