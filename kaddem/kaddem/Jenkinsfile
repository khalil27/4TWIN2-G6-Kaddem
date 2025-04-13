pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean install'
      }
    }
    stage('SonarQube Analysis') {
      steps {
        script {
          def scannerHome = tool 'scanner'
          withSonarQubeEnv('scanner') {
            sh "${scannerHome}/bin/sonar-scanner"
          }
        }
      }
    }
    stage('Docker Build') {
      steps {
        sh 'docker-compose build'
      }
    }
    stage('Push to Nexus') {
      steps {
        script {
          docker.withRegistry('http://192.168.33.10:8083', 'nexus') {
            sh 'docker push $registry/kaddem:1.0'
          }
        }
      }
    }
  }
  environment {
    registry = "192.168.33.10:8083"
    registryCredentials = "nexus"
  }
}
