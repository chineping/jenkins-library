def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    if (config.dockerRegistry == null) {
        config.dockerRegistry = env.DOCKER_REGISTRY ?: ''
    }
    if (config.dockerRegistryCredentialsId == null) {
        config.dockerRegistryCredentialsId = 'docker-registry'
    }
    if (config.dockerHost == null) {
        config.dockerHost = env.DOCKER_HOST
    }

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
                    script {
                        withCredentials([usernamePassword( credentialsId: config.dockerRegistryCredentialsId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                            sh "docker login -u ${USERNAME} -p ${PASSWORD}"
                        }
                    }
                    sh "docker-compose build"
                    sh "docker-compose push"
                }
            }
        }
    }
}