@Library('jenkins-shared-lib') _

javaDockerPipeline(
    dockerCreds: 'dockerhub-creds',
    mavenTool: 'M3'
)
