pipeline {
    agent any
    
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
                
                // Display workspace contents and environment info
                sh '''
                    echo "Workspace contents:"
                    ls -la
                    
                    echo "Java version:"
                    java -version || echo "Java not found"
                    
                    echo "Maven version:"
                    mvn -v || echo "Maven not found"
                    
                    echo "Gradle version:"
                    gradle -v || echo "Gradle not found"
                '''
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
                        sh 'chmod +x ./gradlew || true'
                        sh './gradlew build -x test || gradle build -x test'
                    } else {
                        error 'Could not find pom.xml or build.gradle. Unable to determine build system.'
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
