def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    if (config.dockerBuilds == null) {
        error "dockerBuilds parameter was not specified. This is required. Ex: dockerBuilds = [imageName : contextDirectory]"
    }

    if (config.dockerRegistry == null) {
        config.dockerRegistry = env.DOCKER_REGISTRY ?: ''
    }
    if (config.dockerRegistryCredentialsId == null) {
        config.dockerRegistryCredentialsId = 'docker-registry'
    }
    if (config.dockerHost == null) {
        config.dockerHost = env.DOCKER_HOST
    }
    if (config.version == null) {
        config.version = env.BRANCH_NAME
    }

    pipeline {
        agent {
            node {
                label 'docker'
            }
        }
        stages {
            stage('Checkout Source Code') {
                checkout scm
            }
            dockerBuild {
                dockerBuilds = config.dockerBuilds
                dockerRegistry = config.dockerRegistry
                dockerRegistryCredentialsId = config.dockerRegistryCredentialsId
                dockerHost = config.dockerHost
                version = config.version
            }
        }
    }
}