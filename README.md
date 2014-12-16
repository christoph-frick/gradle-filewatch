gradle-filewatch
================

Watches (or monitors if you like) directories in the Gradle build and executes tasks on file changes.  It uses RxGroovy to throttle file change bursts and sequentialize the builds.

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

Example to watch things: *`compileGroovy`, if there are changes in `src/main/groovy`*

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
