package commandline

object ArgCollections {
  
  val sparkFlags = new FlagSet()(
    Opt[String]("sparkMaster",
                required = true,
                description = "This is a very long description of the tool in question..................the description",
                validInputs = "<String>",
                default = "local"
    ),
    Opt[Int]("numExecutors",
             required = true,
             description = "This is another long description of the tool in question... pfghje the description",
             validInputs = "<Int>",
             default = "local"
    )
  )
  
  val threadFlags = new FlagSet()(
    Opt[Int]("threads",
             description = "This is a very long description of the tool in question..................the description",
             validInputs = "<String>",
             default = "local"
    ),
    Opt[String]("threadType",
                description = "This is a tool in question.............the description",
                validInputs = "<String>",
                default = "local"
    )
  )
  
}