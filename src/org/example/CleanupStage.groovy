package org.example

class CleanupStage {
    static void run(script) {
        script.stage('Cleanup') {
            script.sh "docker image prune -f || true"
        }
    }
}
