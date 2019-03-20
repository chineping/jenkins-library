/*
Function to build and push a docker image. Applies all given tags to the image.
*/
def prScan() {
    withSonarQubeEnv('sonarqube') {
        //Repo parameter needs to be <org>/<repo name>
        def repoName = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]
        def orgName = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/')[-2]
        def repo =  "${orgName}/${repoName}"
        //Use Preview mode for PRs
        withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
            sh "mvn -X -Dsonar.analysis.mode=preview -Dsonar.github.pullRequest=${env.CHANGE_ID} -Dsonar.github.oauth=${GITHUB_TOKEN}  -Dsonar.github.repository=${repo} sonar:sonar"
        }
    }
}

def scan() {
    withSonarQubeEnv('sonarqube') {
        sh "mvn sonar:sonar"
    }
}

def call() {
    if (env.CHANGE_URL != null && env.CHANGE_URL.endsWith("/pull/${env.CHANGE_ID}")) {
        this.prScan()
    } else {
        this.scan()
    }
    timeout(time: 3, unit: 'MINTUES') { // Just in case something goes wrong, pipeline will be killed after a timeout
        def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
        if (qg.status != 'OK') {
            error "Pipeline aborted due to Sonar quality gate failure: ${qg.status}"
        }
    }
}