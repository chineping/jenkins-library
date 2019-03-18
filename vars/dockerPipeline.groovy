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
                        expression { return fileExists('docker-compose.yaml') }
                        expression { return fileExists('docker-compose.yml') }
                    }
                    not {
                        expression { return config.dockerBuilds != null }
                    }
                }
                steps {
                    sh "docker-compose build"
                }
            }
            stage('Publishing Container from Compose file') {
                when {
                    anyOf {
                        expression { return fileExists('docker-compose.yaml') }
                        expression { return fileExists('docker-compose.yml') }
                    }
                    not {
                        expression { return config.dockerBuilds != null }
                    }
                }
                steps {
                    script {
                        docker.withRegistry(credentialsId: 'docker-registry') {
                            sh "docker-compose push"
                        }
                    }
                }
            }
        }
    }
}