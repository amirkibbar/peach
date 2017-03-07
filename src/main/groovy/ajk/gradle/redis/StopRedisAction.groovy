package ajk.gradle.redis

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

import static ajk.gradle.redis.RedisPlugin.*

class StopRedisAction {
    @Input
    @Optional
    Integer port

    private AntBuilder ant
    private Project project

    StopRedisAction(Project project) {
        this.project = project
        ant = project.ant
    }

    void execute() {
        port = port ?: 6379
        def pidFile = new File(project.buildDir, "redis-${port}.image-id")

        if (!pidFile.exists()) {
            println "${RED}* redis:$NORMAL couldn't find $pidFile, can't stop Redis without this file, please stop it manually"
            throw new RuntimeException("failed to stop Redis")
        }

        def command = "docker stop ${pidFile.text}"

        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = command.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill(60 * 1000)

        println "${CYAN}* redis:$NORMAL waiting for Redis to stop"
        ant.waitfor(maxwait: 1, maxwaitunit: "minute", timeoutproperty: "redisTimeout") {
            not {
                socket(server: "localhost", port: port)
            }
        }

        if (ant.properties['redisTimeout'] != null) {
            println "${RED}* redis:$NORMAL could not stop Redis"
            println serr
            throw new RuntimeException("failed to stop Redis")
        } else {
            println "${CYAN}* redis:$NORMAL ${GREEN}Redis on port $port has stopped$NORMAL"
        }

        if (!pidFile.delete()) {
            println "${YELLOW}* redis:$NORMAL warning - can't remove $pidFile"
        }
    }
}
