/*
Function to build and push a docker image. Applies all given tags to the image.
*/
def build(String imageName, ArrayList tags, String context) {
    stage(imageName) {
        dir(context) {
            image = docker.build(imageName)
            for (tag in tags) {
                image.push(tag)
            }
        }
    }
}

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

    if (env.BRANCH_NAME == 'master') {
        tags = [config.version]
        tags.add('latest') 
    } else {
        //Ignore specified version for non releasable branches
        tags = [env.BRANCH_NAME]
    }

    stage('Building Docker Containers') {
        when {
            expression { return config.dockerBuilds != null }
        }
        steps {
            script {
                docker.withServer(config.dockerHost) {
                    docker.withRegistry(config.dockerRegistry, config.dockerRegistryCredentialsId) {
                        def builds = [:]
                        for (image in config.dockerBuilds.keySet()) {
                            builds[image] = {
                                this.build(image, tags, config.dockerBuilds[image])
                            }
                        }
                        parallel builds
                    }
                }
            }
        }
    }
}