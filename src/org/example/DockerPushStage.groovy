package org.example

class DockerPushStage {
    static void run(script, String dockerUser, String dockerPass) {
        script.stage('Docker Push') {
            script.sh "echo ${dockerPass} | docker login -u ${dockerUser} --password-stdin"
            script.sh "docker push ${dockerUser}/java-app:${script.env.BUILD_NUMBER}"
            script.sh "docker push ${dockerUser}/java-app:latest || true"
        }
    }
}
