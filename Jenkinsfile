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
                withEnv(['GCLOUD_PATH=/Users/subhash/google-cloud-sdk/bin']) {
                    // Authenticate with GCP
                    sh '$GCLOUD_PATH/gcloud auth activate-service-account --key-file=/Users/subhash/Downloads/sit737-24t1-subhash-c10ae83-d47db93d86a4.json'
                    sh '$GCLOUD_PATH/gcloud auth configure-docker australia-southeast1-docker.pkg.dev'

                    script {
                        // Define project ID, repository name, and image tag
                        def projectId = 'sit737-24t1-subhash-c10ae83'
                        def repoName = 'calculator-microservice'
                        def imageTag = 'v1.0'

                        // Tag the Docker image
                        sh "docker tag my-app australia-southeast1-docker.pkg.dev/${projectId}/${repoName}/my-app:${imageTag}"

                        sh '$GCLOUD_PATH/gcloud auth print-access-token | docker login -u oauth2accesstoken --password-stdin https://australia-southeast1-docker.pkg.dev'

                        // Push the Docker image to Artifact Registry
                        sh "docker push australia-southeast1-docker.pkg.dev/${projectId}/${repoName}/my-app:${imageTag}"
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