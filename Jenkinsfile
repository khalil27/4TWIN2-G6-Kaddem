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
            }
        }
        
        stage('Setup Maven') {
            steps {
                // Download and set up Maven locally without sudo
                sh '''
                    if ! command -v mvn &> /dev/null; then
                        echo "Setting up Maven locally..."
                        
                        # Create a directory for Maven
                        mkdir -p $WORKSPACE/maven
                        
                        # Download Maven
                        curl -s https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz -o $WORKSPACE/maven.tar.gz
                        
                        # Extract Maven
                        tar -xzf $WORKSPACE/maven.tar.gz -C $WORKSPACE/maven --strip-components=1
                        
                        # Verify installation
                        $WORKSPACE/maven/bin/mvn -version
                    fi
                '''
            }
        }
        
        stage('Build') {
            steps {
                script {
                    def pomFile = sh(script: 'find . -name "pom.xml" | head -1', returnStdout: true).trim()
                    
                    if (pomFile) {
                        echo "Found Maven project at: ${pomFile}"
                        def pomDir = sh(script: "dirname ${pomFile}", returnStdout: true).trim()
                        
                        echo "Building Maven project in directory: ${pomDir}"
                        sh """
                            cd ${pomDir}
                            export MAVEN_OPTS="-Xmx1024m"
                            
                            # Try using Maven if installed globally
                            if command -v mvn &> /dev/null; then
                                mvn clean package -DskipTests
                            else
                                # Use our local Maven installation
                                $WORKSPACE/maven/bin/mvn clean package -DskipTests
                            fi
                        """
                    } else {
                        error "Could not find pom.xml in any directory. Unable to determine build system."
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Archive the build artifacts
            archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
        }
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Please check the logs for more information.'
        }
    }
}
