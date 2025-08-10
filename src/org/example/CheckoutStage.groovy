package org.example

class CheckoutStage {
    static void run(script) {
        script.stage('Checkout') {
            script.checkout script.scm
        }
    }
}
