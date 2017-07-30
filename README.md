# MacroHard Word Processor

<p align="center">
  <img src="https://github.com/abewheel/MacrohardWordProcessor/blob/master/utils/login.PNG" alt="Login"/>
</p>


## Description
	
	Macrohard Word is a word processor with a variety of features beyond a vanilla text editor:
		- User login
		- Tabbed editor
		- Spell check
		- Network capabilities
		- Shared files and permissioning system
		- Supports concurrent modifications of same file by multiple users


<p align="center">
   <img src="https://github.com/abewheel/MacrohardWordProcessor/blob/master/utils/spell.PNG" alt="Editor"/>
</p>


## About the Project
	
	This repository contains the client and server code for Macrohard Word. 
	Requires a MySQL database on the server machine.
	I wrote this program in 2016.


<p align="center">
  <img src="https://github.com/abewheel/MacrohardWordProcessor/blob/master/utils/add.PNG" alt="Login"/>
</p>


<p align="center">
  <img src="https://github.com/abewheel/MacrohardWordProcessor/blob/master/utils/open.PNG" alt="Login"/>
</p>

		
## Technical Details
	
	Macrohard Word is implemented in Java.
	It stores files in a MySQL database and uses the google-diff-match-patch library 
	to resolve concurrent modifications of the same file.
	
	To run:
		1. Clone the repository to your machine. 
		2. Start a MySQL server
		3. Run database.SQL found in the project root directory on the MySQL server
		4. Run both client and server main classes
	Main Classes:
		Client - MacrohardWordProcessor/src/client/Client.java
		Server - MacrohardWordProcessor/src/server/MacroHardServer.java


<p align="center">
  <img src="https://github.com/abewheel/MacrohardWordProcessor/blob/master/utils/collab.PNG" alt="Login"/>
</p>
