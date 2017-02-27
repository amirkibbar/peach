package ajk.gradle.redis

import org.gradle.api.Project
import org.gradle.util.Configurable

class StartRedisExtension implements Configurable<StartRedisExtension> {
    private Project project

    StartRedisExtension(Project project) {
        this.project = project
    }

    @Override
    StartRedisExtension configure(Closure closure) {
        configure(closure, new StartRedisAction(project)).execute()

        return this
    }
}
