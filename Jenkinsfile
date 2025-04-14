pipeline {
    agent {
        docker {
            image 'node:18'
            args '-u root'
        }
    }
    stages {
        stage('Install dependencies') {
            steps {
                script {
                    sh 'npm install'
                }
            }
        }
        stage('Unit Test') {
            steps {
                script {
                    sh 'npm test'
                }
            }
        }
        stage('SonarQube Analysis') {
          steps {
            script {
              def scannerHome = tool 'scanner'
              withSonarQubeEnv('SonarQube') {
                sh "${scannerHome}/bin/sonar-scanner"
              }
            }
          }
        }

        stage('Build application') {
            steps {
                script {
                    sh 'npm run build-dev'
                }
            }
        }
    }
}