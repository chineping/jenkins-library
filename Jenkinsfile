node {
    try {
        stage('Checkout SCM') {
            checkout scm
        }

        stage('Test') {
            echo "Do something here."
        }
    } catch (ex) {
        if (currentBuild.result == null) {
            currentBuild.result = 'FAILED'
        }
        echo "Failed due to ${ex}: ${ex.message}"
    }
}
