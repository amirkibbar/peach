package ajk.gradle.redis

import org.gradle.api.Plugin
import org.gradle.api.Project

class RedisPlugin implements Plugin<Project> {
    static final String ESC = "${(char) 27}"
    static final String CYAN = "${ESC}[36m"
    static final String GREEN = "${ESC}[32m"
    static final String YELLOW = "${ESC}[33m"
    static final String RED = "${ESC}[31m"
    static final String NORMAL = "${ESC}[0m"

    @Override
    void apply(Project project) {
        project.extensions.create('startRedis', StartRedisExtension, project)
        project.extensions.create('stopRedis', StopRedisExtension, project)
    }
}
