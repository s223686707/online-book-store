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
                    sh '/opt/homebrew/bin/wget https://your-artifact-repo.com/dd-java-agent.jar'
                    def containerId = sh(script: "docker run -d -p 8095:8095 -v \$(pwd)/dd-java-agent.jar:/dd-java-agent.jar my-app:latest java -javaagent:/dd-java-agent.jar -Ddd.logs.injection=true -Ddd.env=staging -jar /target/Menu-Driven-0.0.1-SNAPSHOT.jar", returnStdout: true).trim()
                    echo "Container ID: ${containerId}"
                    def ipAddress = sh(script: "docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ${containerId}", returnStdout: true).trim()
                    def url = "http://${ipAddress}:8095"
                    echo "Application URL: ${url}"
                    sleep(time: 5, unit: "MINUTES")
                    sh "docker stop ${containerId}"
                    sh "docker rm ${containerId}"
                    
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