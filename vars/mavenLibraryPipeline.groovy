def call(body) {

    def config = [:]
    def mvnCmd = "mvn"
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
            stage('Building Library') {
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
                            script {
                                sonar()
                            }
                        }
                    }
                    stage('Security Scanning') {
                        steps {
                            echo "TBD..."
                        }
                    }
                }
            }
            stage('Deploy to Repository') {
                when {
                    not {
                        changeRequest()
                    }
                }
                steps {
                    withMaven() {
                        sh "${mvnCmd} -DskipTests deploy"
                    }
                }
            }
        }
    }
}