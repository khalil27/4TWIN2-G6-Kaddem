pipeline {
    agent any
    
    // Set up environment variables
    environment {
        NODE_VERSION = 'v18.16.0' // Specify a stable Node version
        NPM_CONFIG_CACHE = "${WORKSPACE}/.npm-cache" // Custom npm cache location
    }
    
    stages {
        stage('Setup Environment') {
            steps {
                echo 'Setting up environment...'
                // Use NVM if available to manage Node version
                sh '''
                    export NVM_DIR="$HOME/.nvm" || true
                    [ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh" || true
                    nvm use ${NODE_VERSION} || nvm install ${NODE_VERSION} || true
                    
                    # Verify environment
                    node -v
                    npm -v
                    npm config list
                    ls -la
                '''
            }
        }
        
        stage('Install dependencies') {
            steps {
                echo 'Installing npm dependencies...'
                // Clean npm cache and perform fresh install with retry mechanism
                sh '''
                    # Clear caches to avoid issues with corrupted packages
                    npm cache clean --force
                    rm -rf node_modules package-lock.json
                    
                    # Install with retry mechanism
                    for i in 1 2 3; do
                        echo "Attempt $i: Installing dependencies..."
                        npm install --no-fund --prefer-offline && break || {
                            echo "Attempt $i failed, retrying..."
                            if [ $i -eq 3 ]; then
                                echo "All attempts failed. Exiting..."
                                exit 1
                            fi
                            sleep 5
                        }
                    done
                '''
            }
        }
        
        
        stage('Build application') {
            steps {
                echo 'Building the application...'
                sh '''
                    # Make sure environment variables are properly set for build
                    export NODE_ENV=development
                    npm run build-dev
                '''
            }
        }
    }
    
    post {
        always {
            echo 'Cleaning up workspace...'
            // Archive artifacts if needed
            archiveArtifacts artifacts: 'dist/**/*', allowEmptyArchive: true
        }
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Please check the logs for more information.'
        }
    }
}
