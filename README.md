gradle-filewatch
================

Watch directories in the build and executes tasks on file changes

Usage
-----

Add the plugin to the `buildscript`:
	
	buildscript {
		dependencies {
			classpath 'net.ofnir.gradle:gradle-filewatch:<version>'
		}
	}

Apply the plugin:

	apply plugin: 'net.ofnir.gradle.filewatch'

Example to watch things: *`compileGroovy`, if there are changes in 

	filewatches {
		compileGroovy {
			path = 'src/main/groovy'
		}
	}


ToDos
-----

 - If there are changes to files in different tasks, queue the tasks

 - Allow defining a global and a per-task ignore pattern (currently only
   hard-coded vim `.swp` files are ignored)

 - deduce the path(s) to watch from the sourceSet of the task
