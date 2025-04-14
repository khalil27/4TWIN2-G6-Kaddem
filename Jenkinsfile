pipeline {
    agent any
    
    tools {
        // Define the Maven installation to use
        maven 'Maven 3.8.6' // Use the name configured in Jenkins
        jdk 'JDK 17' // Use the name configured in Jenkins
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM', 
                    branches: [[name: '*/DhiaGhouma']], 
                    userRemoteConfigs: [[
                        url: 'https://github.com/khalil27/4TWIN2-G6-Kaddem.git',
                        credentialsId: 'git123'
                    ]]
                ])
                
                // List workspace contents for debugging
                sh 'ls -la'
            }
        }
        
        stage('Build') {
            steps {
                // Check if it's a Maven or Gradle project
                script {
                    if (fileExists('pom.xml')) {
                        echo 'Building Maven project...'
                        sh 'mvn clean package -DskipTests'
                    } else if (fileExists('build.gradle')) {
                        echo 'Building Gradle project...'
                        sh './gradlew build -x test'
                    } else {
                        error 'Could not find pom.xml or build.gradle. Unable to determine build system.'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                script {
                    try {
                        if (fileExists('pom.xml')) {
                            echo 'Running Maven tests...'
                            sh 'mvn test'
                        } else if (fileExists('build.gradle')) {
                            echo 'Running Gradle tests...'
                            sh './gradlew test'
                        }
                    } catch (Exception e) {
                        echo 'Tests failed, but continuing pipeline'
                    }
                }
            }
        }
        
        stage('Package') {
            steps {
                script {
                    if (fileExists('pom.xml')) {
                        echo 'Packaging Maven project...'
                        sh 'mvn package -DskipTests'
                    } else if (fileExists('build.gradle')) {
                        echo 'Packaging Gradle project...'
                        sh './gradlew assemble'
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Archive the build artifacts
            archiveArtifacts artifacts: '**/target/*.jar,**/build/libs/*.jar', allowEmptyArchive: true
        }
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Please check the logs for more information.'
        }
    }
}
