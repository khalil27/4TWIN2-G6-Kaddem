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
                
                sh '''
                    echo "Workspace contents:"
                    ls -la
                    
                    echo "Looking for build files:"
                    find . -name "pom.xml" -o -name "build.gradle"
                '''
            }
        }
        
        stage('Download Maven') {
            steps {
                sh '''
                    # Create a local directory for Maven
                    mkdir -p $HOME/maven-local
                    
                    # Download Maven binary (only if not already downloaded)
                    if [ ! -f $HOME/maven-local/bin/mvn ]; then
                        echo "Downloading Maven..."
                        wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
                        tar -xzf apache-maven-3.9.6-bin.tar.gz -C $HOME/maven-local --strip-components=1
                        rm apache-maven-3.9.6-bin.tar.gz
                    fi
                    
                    # Verify Maven is working
                    $HOME/maven-local/bin/mvn -version
                '''
            }
        }
        
        stage('Build') {
            steps {
                sh '''
                    # Find the pom.xml file
                    POM_FILE=$(find . -name "pom.xml" | head -1)
                    
                    if [ -n "$POM_FILE" ]; then
                        # Get the directory containing pom.xml
                        POM_DIR=$(dirname "$POM_FILE")
                        echo "Building Maven project in directory: $POM_DIR"
                        
                        # Navigate to the directory and build
                        cd "$POM_DIR"
                        $HOME/maven-local/bin/mvn clean package -DskipTests
                    else
                        echo "No pom.xml file found!"
                        exit 1
                    fi
                '''
            }
        }
    }
    
    post {
        always {
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
