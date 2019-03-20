/*
Function to run sonar scan on PR code and put the results into the PR.
*/
def prScan() {
    withSonarQubeEnv('sonarqube') {
        //Repo parameter needs to be <org>/<repo name>
        def repoName = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]
        def orgName = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/')[-2]
        def repo =  "${orgName}/${repoName}"
        //Use Preview mode for PRs
        withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'GITHUB_TOKEN', usernameVariable: 'GITHUB_USER')]) {
            sh "mvn -X -Dsonar.analysis.mode=preview -Dsonar.github.pullRequest=${env.CHANGE_ID} -Dsonar.github.oauth=${GITHUB_TOKEN}  -Dsonar.github.repository=${repo} sonar:sonar"
        }
    }
}

/*
Function to run a sonar scan on source code and publish results to Sonar server
*/
def scan() {
    withSonarQubeEnv('sonarqube') {
        sh "mvn sonar:sonar"
    }
    timeout(time: 3, unit: 'MINUTES') { // Just in case something goes wrong, pipeline will be killed after a timeout
        def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
        if (qg.status != 'OK') {
            error "Pipeline aborted due to Sonar quality gate failure: ${qg.status}"
        }
    }
}

def call() {
    if (env.CHANGE_URL != null && env.CHANGE_URL.endsWith("/pull/${env.CHANGE_ID}")) {
        this.prScan()
    } else {
        this.scan()
    }
}