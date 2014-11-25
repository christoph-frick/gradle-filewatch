package net.ofnir.gradle.filewatch

import org.gradle.api.Plugin
import org.gradle.api.Project

class FileWatchPlugin implements Plugin<Project> {

	 @Override
	 public void apply(Project project) {
		 project.extensions.filewatches = project.container(FileWatchConfig)
		 project.tasks.create(name: FileWatchTask.NAME, type: FileWatchTask)
	 }

}
