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
	It does not contain all configuration files and libraries necessary to run with full functionality.
	Requires a configured MySQL database on the server machine.
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
	
	Main Classes:
		Client - MacrohardWordProcessor/src/client/Client.java
		Server - MacrohardWordProcessor/src/server/MacroHardServer.java


<p align="center">
  <img src="https://github.com/abewheel/MacrohardWordProcessor/blob/master/utils/collab.PNG" alt="Login"/>
</p>
