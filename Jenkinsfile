pipeline {
    agent any

    environment {
        MAVEN_HOME = '/var/lib/jenkins/maven-local'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Building Maven project'
                    def pomFile = ''
                    sh 'echo Workspace contents:'
                    sh 'ls -la'
                    sh 'echo Looking for build files:'
                    sh 'find . -name pom.xml -o -name build.gradle'
                    pomFile = sh(script: 'find . -name pom.xml', returnStdout: true).trim()
                    def pomDir = pomFile ? dirname(pomFile) : ''
                    echo "Building Maven project in directory: ${pomDir}"
                    dir(pomDir) {
                        sh "/var/lib/jenkins/maven-local/bin/mvn clean package -DskipTests"
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo 'Running SonarQube analysis'
                    dir('kaddem/kaddem') {
                        withSonarQubeEnv('scanner') {
                            sh '''
                                /var/lib/jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/scanner/bin/sonar-scanner \
                                -Dsonar.projectKey=sonar \
                                -Dsonar.projectName=sonar \
                                -Dsonar.sources=kaddem/kaddem/src \
                                -Dsonar.java.binaries=kaddem/kaddem/target/classes
                            '''
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up workspace'
            cleanWs()
        }
        success {
            echo 'Build and analysis completed successfully'
        }
        failure {
            echo 'Build or analysis failed. Check logs for more information.'
        }
    }
}
