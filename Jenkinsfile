pipeline {
    agent any

    stages {

        stage('Debug Info') {
            steps {
                echo 'Checking Node and NPM versions...'
                sh 'node -v'
                sh 'npm -v'
                sh 'ls -la'
            }
        }

        stage('Install dependencies') {
            steps {
                echo 'Installing npm dependencies...'
                sh 'npm install --verbose'
            }
        }

        stage('Unit Test') {
            steps {
                echo 'Running unit tests...'
                sh 'npm test || exit 1'
            }
        }

        stage('Build application') {
            steps {
                echo 'Building the application...'
                sh 'npm run build-dev || exit 1'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
