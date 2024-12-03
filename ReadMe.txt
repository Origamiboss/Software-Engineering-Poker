run the project.sql file to set up the database on your MySQL host (source c:\filepath\project.sql)

make sure you keep your MySQL host running

Extract the source files from the zip file from the GitHub repository (it is the same as the one we submitted on blackboard)

open papyrus and click file->open projects from file system

click directory then select the directory that you put the source files in. click finish

before running it, right click on the project in the papyrus UI and click properties. make sure to remove all the jar files that are in the class path (NOT JUnit 4)

and then click add external JARs and add the msql-connector and the ocsf JARs that come with the source code (for some reason papyrus doesn't like it unless you manually set the classpath) and click apply and close

after that double click the batch file (PokerStarter.bat) and run it