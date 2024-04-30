pipeline {
    agent any

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
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Deploy to Test') {
            steps {
                // Deploy to test environment, e.g., deploy to staging server or Docker container
                sh 'docker build -t my-app .'
                sh 'docker run -d --name test-app my-app'
            }
        }

        stage('Release to Production') {
            steps {
                // Release to production environment, e.g., deploy to production server
                sh 'docker push my-app:latest'
                sh 'ansible-playbook site.yml -i production'
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