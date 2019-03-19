def call(body) {

    def config = [:]
    def mvnCmd = "mvn -Ddockerfile.skip=true -DskipITs=true"
    def version
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline {
        agent {
            node {
                label 'maven'
            }
        }
        stages {
            stage('Building Application') {
                steps {
                    script {
                        def pom = readMavenPom file: "pom.xml"
                        version = pom.version
                        if (version == null) {
                            version = pom.parent.version
                        }
                    }
                    echo "Building version $version..."
                    withMaven() {
                        sh "${mvnCmd} clean package"
                    }
                }
                post {
                    always {
                        junit '**/target/surefire-reports/*.xml'
                    }
                }
            }
            stage('Source Code Analysis') {
                parallel {
                    stage('Sonar Analysis') {
                        steps {
                            echo "Running Sonar Analysis..."
                        }
                    }
                    stage('Secure Scanning') {
                        steps {
                            echo "TBD..."
                        }
                    }
                }
            }
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