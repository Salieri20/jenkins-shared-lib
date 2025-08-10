import org.example.CheckoutStage
import org.example.MavenBuildStage
import org.example.DockerBuildStage
import org.example.DockerPushStage
import org.example.CleanupStage

def call(Map config = [:]) {
    node {
        withCredentials([usernamePassword(
            credentialsId: config.dockerCreds ?: 'dockerhub-creds',
            usernameVariable: 'DOCKERHUB_USER',
            passwordVariable: 'DOCKERHUB_PASS'
        )]) {

            CheckoutStage.run(this)
            MavenBuildStage.run(this, config.mavenTool ?: 'M3')
            DockerBuildStage.run(this, env.DOCKERHUB_USER)
            DockerPushStage.run(this, env.DOCKERHUB_USER, env.DOCKERHUB_PASS)
            CleanupStage.run(this)
        }
    }
}
