

# Continuous Delivery Pipeline
This pipeline models the "Develop off of master branch" philosophy. All development work is done on short-lived feature branches and a Pull Request (PR) is openned when the feature is considered to be complete. The PR is built, testing using automated test cases and then reviewed for completeness. When the PR is approved, it is merged to master. Commits to master are considered to be production ready. The code is built, tested, released and promoted through Development, Staging and Production environments.

## Using the Pipeline Library
Create a Jenkins file at the root of your repository like the example below. Add any configuration properties that are needed.
```groovy
mavenPipeline {
    applicationName = 'sample-app'
}
```

### Configuration Properties
* applicationName - Name of the container image to create. Required.
* dockerContext - Directory for Docker build context. Optional. Defaults to the root directory of your repository.

## Triggers
The pipeline is triggered under the following scenarios:
* PR opened against the `master` branch
* Commit to the `master` branch


![Pipeline Image](img/pr-pipeline.png "Gitflow Pipeline")

## Pull Request Pipeline
When a PR is opened against the `development` branch, this portion of the pipeline is triggered.
1. **Checkout and Compile Source Code**</br>
Checkout source code from the PR branch in Github and compile the source code.
1. **Unit Test Execution**</br>
Execute Unit tests and attached test results to the build.
1. In Parallel:
    * **Sonar Code Analysis**</br>
    Sonar code findings added to PR as comments. Fail build if Sonar Quality Gate is not passed. 
    * **Security Scanning**</br>
    Static code analysis looking for known code vulnerabilities and patterns.
1. **Package Application**</br>
Done by running `mvn package`
1. **Build Container**</br>
Build the application container using `docker build`, tagging it with the PR number. Push this image to the Docker registry.
1. In Parallel, deploy the application container to the following ephemiral testing environments and execute the associated test cases:
    * Function Test Environment
    * Performance Test Environment
    * End-to-End Integrated Test Environment
1. **Update PR with build success**</br>

If any failures occur or quality gates are not passed, the build will be failed and [Notifications](notifications.md) sent out.