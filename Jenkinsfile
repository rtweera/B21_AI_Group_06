pipeline {
    agent any

    parameters {
        // Pass a Cucumber tag expression to control which tests run.
        // Default runs every tester's tests.
        // Examples:
        //   "@215565L"            - only your tests
        //   "@215565L and @API"   - only your API tests
        //   "@215565L and @UI"    - only your UI tests
        //   "@215552U or @215565L"- both testers (same as default)
        string(
            name: 'TAGS',
            defaultValue: '@215552U or @215565L or @215527A or @215564H',
            description: 'Cucumber tag expression - controls which scenarios run'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running tests with tags: ${params.TAGS}"
                bat """
                    mvn clean test ^
                        -Dcucumber.filter.tags="${params.TAGS}" ^
                        -Dallure.results.directory=allure-results
                """
            }
            post {
                always {
                    echo "Tests finished (pass or fail). Proceeding to report..."
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo "Generating Allure report from results..."
                allure([
                    includeProperties: false,
                    jdk              : '',
                    results          : [[path: 'allure-results']],
                    reportBuildPolicy: 'ALWAYS',
                    report           : 'allure-report'
                ])
            }
        }
    }

    post {
        always {
            echo "Pipeline complete. Open the Allure Report link in the Jenkins build sidebar."
        }
        failure {
            echo "One or more tests FAILED. Check the Allure report for details."
        }
        success {
            echo "All selected tests PASSED."
        }
    }
}
