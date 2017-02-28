package ajk.gradle.redis

import org.gradle.api.Project
import org.gradle.util.Configurable

import static org.gradle.util.ConfigureUtil.configure

class StopRedisExtension implements Configurable<StopRedisExtension> {
    private Project project

    StopRedisExtension(Project project) {
        this.project = project
    }

    @Override
    StopRedisExtension configure(Closure closure) {
        configure(closure, new StopRedisAction(project)).execute()

        return this
    }
}
