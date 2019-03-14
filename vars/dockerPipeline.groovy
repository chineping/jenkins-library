def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent {
            node {
                label 'docker'
            }
        }
        stages {
            stage('Building Containers from Compose file') {
                when {
                    anyOf {
                        expression { return fileExists 'docker-compose.yaml' }
                        expression { return fileExists 'docker-compose.yml' }
                    }
                    
                }
                steps {
                    sh "docker-compose build"
                }
            }
        }
    }
}