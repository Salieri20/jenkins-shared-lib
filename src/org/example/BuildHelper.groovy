package org.example

class BuildHelper implements Serializable {
    def script
    def dockerImage

    BuildHelper(script) {
        this.script = script
    }

    void runBuild(boolean skipIfNoTests = true, boolean skipTests = false) {
        script.echo "Checking for test files in the project..."

        def testsExist = script.sh(
            script: "find src/test -type f \\( -name '*.java' -o -name '*.groovy' \\) | grep -q .",
            returnStatus: true
        ) == 0

        if (!testsExist && skipIfNoTests) {
            script.echo "No test files found — skipping tests in build."
            skipTests = true
        }

        script.echo "Running Maven build (skip tests: ${skipTests})..."
        script.sh './mvnw -B clean package || mvn -B clean package -Dmaven.test.skip=' + skipTests
        script.archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
    }

    
    void buildDockerImage(String imageName, String tag) {
        dockerImage = script.docker.build("${imageName}:${tag}")
    }

    
    void pushDockerImage(String imageName, String tag, String credsId) {
        script.docker.withRegistry('https://index.docker.io/v1/', credsId) {
            dockerImage.push()
            dockerImage.push('latest')
        }
    }
    void updateDeploymentManifest(String manifestPath, String imageName, String tag) {
        script.echo "Updating Kubernetes manifest: ${manifestPath} with image ${imageName}:${tag}"

        script.sh """
            sed -i "s|image: .*|image: ${imageName}:${tag}|" ${manifestPath}

            git config user.email "jenkins@example.com"
            git config user.name "Jenkins CI"
            git remote set-url origin https://${script.env.GITHUB_CREDS_USR}:${script.env.GITHUB_CREDS_PSW}@github.com/Salieri20/java-app.git
            git add ${manifestPath}
            git commit -m "Update image to ${imageName}:${tag} [ci skip]" || echo "No changes to commit"
            git push origin HEAD:main
        """
    }

    
    void cleanupDocker() {
        script.sh 'docker image prune -f || true'
    }
}
