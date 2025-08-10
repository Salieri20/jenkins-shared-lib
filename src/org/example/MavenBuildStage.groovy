package org.example

class MavenBuildStage {
    static void run(script, String mavenTool) {
        script.stage('Build (Maven)') {
            def mvnHome = script.tool name: mavenTool, type: 'hudson.tasks.Maven$MavenInstallation'
            script.sh "${mvnHome}/bin/mvn -B clean package"
            script.archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
        }
    }
}
