package ajk.gradle.redis

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

import static ajk.gradle.redis.RedisPlugin.*

class StartRedisAction {
    @Input
    @Optional
    String redisVerion

    @Input
    @Optional
    Integer port

    @Input
    @Optional
    File dataDir

    private Project project
    private AntBuilder ant

    StartRedisAction(Project project) {
        this.project = project
        ant = project.ant
    }

    void execute() {
        dataDir = dataDir ?: new File("$project.buildDir/redis")

        def pidFile = new File(dataDir, "redis.image-id")
        if (pidFile.exists()) {
            println "${YELLOW}* redis:$NORMAL Redis seems to be running, see Docker container id ${pidFile.text}"
            println "${YELLOW}* redis:$NORMAL please check $pidFile"
            return
        }

        port = port ?: 6379
        redisVerion = redisVerion ?: 'latest'

        println "${CYAN}* redis:$NORMAL starting Redis version '$redisVerion' on port $port using volume at $dataDir"

        ant.delete(failonerror: true, dir: dataDir)
        dataDir.mkdirs()

        def command = "docker run --rm -d -v $dataDir:/data -p $port:6379 redis:$redisVerion"

        def sout = new StringBuilder(), serr = new StringBuilder()
        def proc = command.execute()
        proc.consumeProcessOutput(sout, serr)
        proc.waitForOrKill(60 * 1000)

        sout >> pidFile

        println "${CYAN}* redis:$NORMAL waiting for Redis to start"
        ant.waitfor(maxwait: 1, maxwaitunit: "minute", timeoutproperty: "redisTimeout") {
            socket(server: "localhost", port: port)
        }

        if(ant.properties['redisTimeout'] != null) {
            println "${RED}* redis:$NORMAL could not start Redis"
            println serr
            throw new RuntimeException("failed to start Redis")
        } else {
            println "${CYAN}* redis:$NORMAL ${GREEN}Redis is now up on port $port"
        }
    }
}
