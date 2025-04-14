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
                
                // Display workspace contents and find build files
                sh '''
                    echo "Workspace contents:"
                    ls -la
                    
                    echo "Looking for build files:"
                    find . -name "pom.xml" -o -name "build.gradle"
                    
                    echo "Java version:"
                    java -version || echo "Java not found"
                '''
                
                // Install Maven if not present
                sh '''
                    if ! command -v mvn &> /dev/null; then
                        echo "Installing Maven..."
                        sudo apt-get update -y || true
                        sudo apt-get install -y maven || true
                    fi
                '''
            }
        }
        
        stage('Build') {
            steps {
                script {
                    // Look for pom.xml in subdirectories
                    def pomFile = sh(script: 'find . -name "pom.xml" | head -1', returnStdout: true).trim()
                    def gradleFile = sh(script: 'find . -name "build.gradle" | head -1', returnStdout: true).trim()
                    
                    if (pomFile) {
                        echo "Found Maven project at: ${pomFile}"
                        def pomDir = sh(script: "dirname ${pomFile}", returnStdout: true).trim()
                        
                        echo "Building Maven project in directory: ${pomDir}"
                        sh "cd ${pomDir} && mvn clean package -DskipTests"
                    } else if (gradleFile) {
                        echo "Found Gradle project at: ${gradleFile}"
                        def gradleDir = sh(script: "dirname ${gradleFile}", returnStdout: true).trim()
                        
                        echo "Building Gradle project in directory: ${gradleDir}"
                        sh "cd ${gradleDir} && chmod +x ./gradlew || true"
                        sh "cd ${gradleDir} && ./gradlew build -x test || gradle build -x test"
                    } else if (fileExists('kaddem')) {
                        echo "Checking kaddem directory for build files"
                        
                        if (fileExists('kaddem/pom.xml')) {
                            echo "Building Maven project in kaddem directory"
                            sh "cd kaddem && mvn clean package -DskipTests"
                        } else if (fileExists('kaddem/build.gradle')) {
                            echo "Building Gradle project in kaddem directory"
                            sh "cd kaddem && chmod +x ./gradlew || true"
                            sh "cd kaddem && ./gradlew build -x test || gradle build -x test"
                        } else {
                            error "No build files found in kaddem directory"
                        }
                    } else {
                        error "Could not find pom.xml or build.gradle in any directory. Unable to determine build system."
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Archive the build artifacts (search in all directories)
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
