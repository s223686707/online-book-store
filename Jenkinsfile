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
                script {

                    // Push the artifact to Octopus Deploy
                    def octopusServer = 'https://project.octopus.app'
                    def octopusApiKey = 'API-E7T1ONUCUAEKPUWL4ZYNBKKKOEYRTXFM'
                    def spaceId = 'https://project.octopus.app/app#/Spaces-2'
                    def projectId = 'https://project.octopus.app/app#/Spaces-2/projects/project-123'
                    def packageVersion = "${env.BUILD_NUMBER}"
                    def packagePath = 'target/*.jar' // Use wildcard to match the JAR file

                    sh "./octo pack --id=${projectId} --format=zip --version=${packageVersion} --basePath=target ${packagePath}"
                    sh "./octo push --server=${octopusServer} --apiKey=${octopusApiKey} --space=${spaceId} --package=target/${projectId}.${packageVersion}.zip"

                    // Create a new release
                    sh "./octo create-release --server=${octopusServer} --apiKey=${octopusApiKey} --space=${spaceId} --project=${projectId} --version=${packageVersion} --package=${projectId}.${packageVersion}.zip"

                    // Deploy the release to the production environment
                    def environmentId = 'YOUR_PRODUCTION_ENVIRONMENT_ID'
                    sh "./octo deploy-release --server=${octopusServer} --apiKey=${octopusApiKey} --space=${spaceId} --project=${projectId} --version=${packageVersion} --environment=${environmentId}"
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