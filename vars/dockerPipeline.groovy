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