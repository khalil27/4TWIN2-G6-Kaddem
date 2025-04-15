pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = "docker.io"
        DOCKER_IMAGE = "dhiaghouma/kaddem-app:latest"  // Your Docker Hub username/repo name
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
            }
        }

        stage('Download Maven') {
            steps {
                sh '''
                    mkdir -p $HOME/maven-local

                    if [ ! -f $HOME/maven-local/bin/mvn ]; then
                        echo "Downloading Maven..."
                        wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
                        tar -xzf apache-maven-3.9.6-bin.tar.gz -C $HOME/maven-local --strip-components=1
                        rm apache-maven-3.9.6-bin.tar.gz
                    fi

                    $HOME/maven-local/bin/mvn -version
                '''
            }
        }

        stage('Build') {
            steps {
                sh '''
                    cd kaddem/kaddem
                    $HOME/maven-local/bin/mvn clean package -DskipTests
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def customImage = docker.build("${DOCKER_IMAGE}", "kaddem/kaddem/")
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", "docker-hub-credentials") {
                        def customImage = docker.image("${DOCKER_IMAGE}")
                        customImage.push("latest")
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
                                -Dsonar.sources=src \
                                -Dsonar.java.binaries=target/classes
                            '''
                        }
                    }
                }
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
