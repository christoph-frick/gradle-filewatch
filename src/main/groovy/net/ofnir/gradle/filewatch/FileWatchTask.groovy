package net.ofnir.gradle.filewatch

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.GradleBuild
import org.gradle.api.tasks.TaskAction
import rx.subjects.BehaviorSubject

import java.util.concurrent.TimeUnit

class FileWatchTask extends DefaultTask {

    final static String NAME = 'filewatch'

    final static Long THROTTLE = 1000

    class Monitor {
        final private Project project
        final private FileWatchConfig config
        final private FileAlterationMonitor monitor
        final private BehaviorSubject<String> taskObserver
        final private BehaviorSubject<String> fileObserver

        Monitor(Project project, FileWatchConfig config) {
            this.project = project
            this.config = config

            if (!firstTask) {
                throw new InvalidUserDataException("Task '${config.name}' does not exist in this project")
            }

            monitor = new FileAlterationMonitor(THROTTLE)
            taskObserver = BehaviorSubject.create(null as String)
            fileObserver = BehaviorSubject.create(null as String)

            final listener = new FileAlterationListenerAdaptor() {
                @Override
                void onFileChange(File file) {
                    super.onFileChange(file)
                    // TODO: make this a configuration
                    // this should be an filter, but it did not work for the BehaviorSubject
                    if (!file.toString().endsWith('.swp')) {
                        fileObserver.onNext(file.toString())
                    }
                }
            }

            (config.paths?:[config.path]).each{ path ->
                logger.info "Listen for file changes in $path for task $config.name"
                monitor.addObserver(new FileAlterationObserver(project.file(path)).with{
                    addListener(listener)
                    return it
                })
            }

            // FIXME: the handling here should move out of here into the task itself, so we can queue the tasks to call
            fileObserver.throttleFirst(THROTTLE, TimeUnit.MILLISECONDS)
            rx.Observable.zip(taskObserver, fileObserver, {task, file -> file}).subscribe(
                    {
                        logger.info "Running $config.name for $it"
                        try {
                            runTask()
                        }
                        catch (Exception|Throwable e) {
                            logger.debug e.message, e
                        }
                        taskObserver.onNext(config.name)
                    },
                    { logger.error "Error running $config" }
            )
        }

        private Task getFirstTask() {
            def tasks = project.getTasksByName(config.name, false)
            if (tasks) {
                return tasks.first()
            }
            return null
        }

        void start() { monitor.start() }
        void stop() { monitor.stop() }

        private runTask() {
            final taskName = config.name
            final t = project.tasks.create("again$taskName", GradleBuild, {
                tasks = [taskName]
            })
            try {
                t.execute()
            }
            finally {
                project.tasks.remove(t)
            }
        }

    }

    @TaskAction
    void run() {
        def monitors = project.filewatches.collect{ new Monitor(project, it) }
        monitors*.start()
        addShutdownHook{ monitors*.stop() }
        synchronized(this){ this.wait() }
    }

}
