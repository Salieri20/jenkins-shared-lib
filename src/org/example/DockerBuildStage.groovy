package org.example

class DockerBuildStage {
    static void run(script, String dockerUser) {
        script.stage('Docker Build') {
            def imgTag = "${dockerUser}/java-app:${script.env.BUILD_NUMBER}"
            script.echo "Building image ${imgTag}"
            script.docker.build(imgTag)
            script.sh "docker tag ${imgTag} ${dockerUser}/java-app:latest || true"
        }
    }
}
