pipeline {
    agent any 
    tools {
        maven '3.9.6'
    }
    stages {
        stage('Build') {
            steps {
                // Build steps, e.g., compile code, create JAR/WAR file
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                // Test steps, e.g., run unit tests
                sh 'mvn test'
            }
        }

        stage('Code Quality Analysis') {
            steps {
                // Code quality analysis steps, e.g., run SonarQube
                withSonarQubeEnv('SonarQube Server') {
                    sh 'mvn clean verify sonar:sonar \
                        -Dsonar.projectKey=Online-book-store \
                        -Dsonar.projectName="Online book store" \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.token=sqp_c00359ecd060ea26e73d1e34e8a1c90ef0f77f22'
                }
            }
        }

        stage('Deploy to Test') {
            steps {
                // Deploy to test environment, e.g., deploy to staging server or Docker container
                sh 'docker build -t my-app .'
                sh 'docker run -d --name test-app -p 8080:8080 my-app'
            }
        }

        stage('Release to Production') {
            steps {
                // Authenticate with Docker registry
                withCredentials([usernamePassword(credentialsId: 'e86c801b-404a-4e23-90eb-1ef5566e9aa5', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                    // Push the Docker image to your Docker registry
                    sh 'docker push my-registry.com/my-app:latest'
                }
            }
        }

        stage('Monitoring and Alerting') {
            steps {
                // Monitoring and alerting steps, e.g., set up monitoring with Datadog
                sh 'datadog-agent install'
                sh 'datadog-agent start'
            }
        }
    }

    post {
        always {
            // Clean up steps, e.g., remove Docker containers, notify team
            sh 'docker stop test-app'
            sh 'docker rm test-app'
            mail to: 'team@example.com',
                 subject: "Pipeline Status: ${currentBuild.fullDisplayName}",
                 body: "${currentBuild.result}"
        }
    }
}
