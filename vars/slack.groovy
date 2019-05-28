
def sendBuildStatus(String buildStatus = 'STARTED', String channel = null) {
    // build status of null means successful
    buildStatus =  buildStatus ?: 'SUCCESS'
    previousStatus = currentBuild.getPreviousBuild() ? currentBuild.getPreviousBuild().result : 'SUCCESS'

    // Default values
    def colorCode = '#FF0000'
    def msg = "${buildStatus} - Job ${env.JOB_NAME} [${env.BUILD_NUMBER}]"

    // Override default values based on build status
    if (buildStatus == 'STARTED') {
        colorCode = '#0000FF'
    } else if (buildStatus == 'SUCCESS') {
        colorCode = '#00FF00'
    } else if (buildStatus == 'UNSTABLE') {
        colorCode = '#FFFF00'
    } else {
        colorCode = '#FF0000'
    }

    //Send notifications of build state
    echo "Notifying Slack that build was a ${buildStatus}"
    return message(msg, colorCode, channel)
}

def message(String msg, String colorCode = null, String channel = null) {
    //Wrap this in a try/catch block so we don't fail the build if we can't send Slack messages
    //Also allows Jenkins installations without the slack plugin to not blow up here.
    def resultBefore = 'SUCCCESS'
    if (currentBuild != null) {
        resultBefore = currentBuild.currentResult
    }
    try {
        def response = slackSend (color: colorCode, message: msg, channel: channel)
        return response.threadId
    } catch(ex) {
        if (currentBuild != null) {
            currentBuild.result = resultBefore
        }
    }
    return null
}
