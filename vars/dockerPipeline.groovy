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
        config.version = [env.BRANCH_NAME]
    }

    if (env.BRANCH_NAME == 'master') {
        tags = [config.version]
        tags.add('latest') 
    } else {
        //Ignore specified version for non releasable branches
        tags = [env.BRANCH_NAME]
    }

    pipeline {
        agent {
            node {
                label 'docker'
            }
        }
        stages {
            dockerBuild {
                dockerBuilds = config.dockerBuilds
                dockerRegistry = config.dockerRegistry
                dockerRegistryCredentialsId = config.dockerRegistryCredentialsId
                dockerHost = config.dockerHost
                tags = this.tags
            }
        }
    }
}