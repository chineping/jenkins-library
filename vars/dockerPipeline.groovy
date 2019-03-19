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
            stage('Building Docker Containers') {
                steps {
                    script {
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
            stage('Testing') {
                parallel {
                    stage('Functional Testing') {
                        when {
                            not {
                                branch 'master'
                            }  
                        }
                        steps {
                            echo 'Not Yet Implemented...'
                        }
                    }
                    stage('Performance Testing') {
                        when {
                            not {
                                branch 'master'
                            }  
                        }
                        steps {
                            echo 'Not Yet Implemented...'
                        }
                    }
                }
            }
            stage('Deploy Review Instance') {
                when {
                    not {
                        branch 'master'
                    }  
                }
                steps {
                    echo 'Not Yet Implemented...'
                }
            }
            stage('Deploy to Staging') {
                when {
                    branch 'master' 
                }
                steps {
                    echo 'Not Yet Implemented...'
                }
            }
            stage('Deploy to Production') {
                when {
                    branch 'master' 
                }
                steps {
                    echo 'Not Yet Implemented...'
                }
            }
        }
    }
}