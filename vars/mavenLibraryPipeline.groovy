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
            stage('Deploy to Repository') {
                when {
                    branch 'master'
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