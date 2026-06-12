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

        stage('Start Backend') {
            steps {
                echo "Configuring and starting backend..."
                withEnv(['JENKINS_NODE_COOKIE=dontKillMe', 'BUILD_ID=dontKillMe']) {
                    bat """
                        @echo off
                        cd app
                        
                        :: Copy application.properties if it doesn't exist
                        if not exist application.properties (
                            echo Creating application.properties from example...
                            copy application.properties.example application.properties
                            powershell -Command "(gc application.properties) -replace '<root password>', 'root' -replace '8080', '8088' | Out-File -encoding ASCII application.properties"
                        )
                        
                        :: Kill any existing process on port 8088 to avoid port collision
                        echo Cleaning up any existing process on port 8088...
                        for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8088 ^| findstr LISTENING') do (
                            echo Killing process %%a on port 8088...
                            taskkill /F /PID %%a
                        )
                        
                        :: Start the backend jar in the background
                        echo Starting backend jar...
                        start "BackendApp" /B java -jar qa-training-app.jar > backend.log 2>&1
                    """
                }
                
                echo "Waiting for backend to become healthy..."
                powershell '''
                    $timeout = 60
                    $elapsed = 0
                    $url = "http://localhost:8088/swagger-ui.html"
                    while ($elapsed -lt $timeout) {
                        try {
                            $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 2
                            if ($response.StatusCode -eq 200) {
                                Write-Host "Backend is up and running!"
                                exit 0
                            }
                        } catch {
                            # Ignore errors and retry
                        }
                        Start-Sleep -Seconds 2
                        $elapsed += 2
                    }
                    Write-Error "Backend failed to start within $timeout seconds."
                    exit 1
                '''
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running tests with tags: ${params.TAGS}"
                bat """
                    mvn clean test ^
                        -Dcucumber.filter.tags="${params.TAGS}" ^
                        -Dallure.results.directory=allure-results ^
                        -Dbase.url=http://localhost:8088
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
            echo "Stopping backend..."
            bat """
                @echo off
                for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8088 ^| findstr LISTENING') do (
                    echo Killing process %%a on port 8088...
                    taskkill /F /PID %%a
                )
            """
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
