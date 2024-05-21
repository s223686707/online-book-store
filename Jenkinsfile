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
                sh 'docker build -t my-app:latest .'
            }
        }

        stage('Release to Production') {
            steps {
                script {
                    // Push Docker image to Docker Hub
                    withCredentials([usernamePassword(credentialsId: 'e86c801b-404a-4e23-90eb-1ef5566e9aa5', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                        sh 'docker tag my-app:latest subhash707/project:latest'
                        sh 'docker push subhash707/project:latest'
                    }

                    // Use withKubeConfig to apply Kubernetes configurations
                    withKubeConfig([credentialsId: 'minikube-kubeconfig', serverUrl: 'https://127.0.0.1:52437']) {
                        // Apply Kubernetes deployment and service configurations to ensure the deployment exists
                        sh 'kubectl apply -f k8s/deployment.yaml'
                        sh 'kubectl apply -f k8s/service.yaml'

                        // Update the deployment image
                        sh 'kubectl set image deployment/my-app-deployment my-app=subhash707/project:latest --record'
                    }
                }
            }
        }

        stage('Run Java Application') {
            steps {
                script {
                    // Define the command
                    def command = """java -javaagent:/Users/subhash/Downloads/check/dd-java-agent.jar \\
                        -Ddd.env=testing3 \\
                        -Ddd.logs.injection=true \\
                        -jar target/Menu-Driven-0.0.1-SNAPSHOT.jar"""
                    
                    // Execute the command in background with timeout
                    try {
                        timeout(time: 3, unit: 'MINUTES') {
                            sh label: 'Run Java Application', script: command, background: true
                        }
                    } catch (err) {
                        // Catch timeout error but do nothing
                    }
                }
            }
        }
    }

    post {
        always {
            mail to: 'subhashsainani4@gmail.com', subject: "Pipeline Status: ${currentBuild.fullDisplayName}", body: "${currentBuild.result}"
        }
    }
}
