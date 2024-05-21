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
                withCredentials([file(credentialsId: 'GCP_SERVICE_ACCOUNT_KEY', variable: 'SERVICE_ACCOUNT_KEY')]) {
                    withEnv(['GCLOUD_PATH=/Users/subhash/google-cloud-sdk/bin']) {
                        // Debug: Print environment variables
                        sh 'echo "GCLOUD_PATH: $GCLOUD_PATH"'
                        sh 'echo "SERVICE_ACCOUNT_KEY: $SERVICE_ACCOUNT_KEY"'
                        sh 'ls -l $SERVICE_ACCOUNT_KEY'

                        // Authenticate with GCP
                        sh 'gcloud auth activate-service-account --key-file=$SERVICE_ACCOUNT_KEY'
                        
                        // Debug: Print active service account
                        sh 'gcloud auth list'
                        
                        // Debug: Print the current access token
                        sh 'gcloud auth print-access-token'

                        // Configure Docker to use gcloud as a credential helper
                        sh 'gcloud auth configure-docker australia-southeast1-docker.pkg.dev'

                        // Debug: Print Docker configuration
                        sh 'cat ~/.docker/config.json'

                        script {
                            // Define project ID, repository name, and image tag
                            def projectId = 'adept-array-424007-i3'
                            def repoName = 'project-repo'
                            def imageTag = 'v1.0'

                            // Tag the Docker image
                            sh "docker tag my-app australia-southeast1-docker.pkg.dev/${projectId}/${repoName}/my-app:${imageTag}"

                            // Authenticate Docker using access token
                            sh 'gcloud auth print-access-token | docker login -u oauth2accesstoken --password-stdin https://australia-southeast1-docker.pkg.dev'

                            // Push the Docker image to Artifact Registry
                            sh "docker push australia-southeast1-docker.pkg.dev/${projectId}/${repoName}/my-app:${imageTag}"
                        }
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
