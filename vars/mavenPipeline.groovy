def call(body) {

    def config = [:]
    def mvnCmd = "mvn -Ddockerfile.skip=true -DskipITs=true"
    def version
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    if (config.applicationName == null) {
        error "applicationName parameter must be specified in pipeline configuration"
    }
    if (config.dockerContext == null) {
        config.dockerContext = '.'
    }

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
            dockerBuild {
                dockerBuilds = config.dockerBuilds
            }

            stage('Promote to Production') {
                when {
                    branch 'master'  
                }
                steps {
                    echo 'Running Master Branch Pipeline'
                }
            }
        }
    }
}