pipeline {
    agent any
    tools {
        maven '3.9.6'
    }
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Code Quality Analysis') {
            steps {
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
                sh 'docker build -t my-app .'
            }
        }

        stage('Release to Production') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'e86c801b-404a-4e23-90eb-1ef5566e9aa5', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                    sh 'docker tag my-app:latest subhash707/project:latest'
                    sh 'docker push subhash707/project:latest'
                }
            }
        }

        stage('Monitoring and Alerting') {
            steps {
                script {
                    sh 'docker run --rm -d --name my-monitoring-container -p 8095:8095 --mount type=bind,source="/Users/subhash/Downloads/check/dd-java-agent.jar",target=/app/dd-java-agent.jar subhash707/project:latest sh -c "java -Ddd.env=test -javaagent:/app/dd-java-agent.jar -Ddd.logs.injection=true -jar target/Menu-Driven-0.0.1-SNAPSHOT.jar & sleep 300 && docker stop my-monitoring-container"'
                    sh 'docker rm my-monitoring-container'
                }
            }
        }
    }

    post {
        always {
            sh 'docker stop my-app'
            sh 'docker rm my-app'
            mail to: 'team@example.com', subject: "Pipeline Status: ${currentBuild.fullDisplayName}", body: "${currentBuild.result}"
        }
    }
}