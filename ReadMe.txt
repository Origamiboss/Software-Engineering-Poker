run the project.sql file to set up the database on your MySQL host. make sure to run it inside a database called student_space (or modify the db.properties file in the sweProject package) (source c:\filepath\project.sql)

make sure you keep your MySQL host running

Extract the source files from the zip file from the GitHub repository (it is the same as the one we submitted on blackboard)

open papyrus and click file->open projects from file system

click directory then select the directory that you put the source files in. click finish

before running it, right click on the project in the papyrus UI and click properties. click java build path and select libraries at the top. select all the jar files and then click remove (NOT JUnit 4)

and then select the classpath and click add external JARs and add the msql-connector and the ocsf JARs that come with the source code (for some reason papyrus doesn't like it unless you manually set the classpath) and click apply and close

after that run the DataServer.bat file to start our database server (you will need to the ip of the machine you are running the database server on) and then the PokerStarter.bat to start the game. any other machines joining the game will only need to run the pokerstarter.bat file
