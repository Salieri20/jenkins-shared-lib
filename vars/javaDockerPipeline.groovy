def call(Map config = [:]) {
    node {
        withCredentials([usernamePassword(
            credentialsId: config.dockerCreds ?: 'dockerhub-creds',
            usernameVariable: 'DOCKERHUB_USER',
            passwordVariable: 'DOCKERHUB_PASS'
        )]) {

            stage('Checkout') {
                checkout scm
            }

            def mvnHome = tool name: config.mavenTool ?: 'M3', type: 'hudson.tasks.Maven$MavenInstallation'

            stage('Build (Maven)') {
                sh "${mvnHome}/bin/mvn -B clean package"
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }

            stage('Docker Build') {
                def imgTag = "${DOCKERHUB_USER}/java-app:${env.BUILD_NUMBER}"
                echo "Building image ${imgTag}"
                docker.build(imgTag)
                sh "docker tag ${imgTag} ${DOCKERHUB_USER}/java-app:latest || true"
            }

            stage('Docker Push') {
                sh "echo ${DOCKERHUB_PASS} | docker login -u ${DOCKERHUB_USER} --password-stdin"
                sh "docker push ${DOCKERHUB_USER}/java-app:${env.BUILD_NUMBER}"
                sh "docker push ${DOCKERHUB_USER}/java-app:latest || true"
            }

            stage('Cleanup') {
                sh "docker image prune -f || true"
            }
        }
    }
}
