pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = "192.168.33.10:8083"  // Your Nexus Docker registry URL
        DOCKER_IMAGE = "kaddem-app:latest"      // Docker image name
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
                dir('kaddem/kaddem') {
                    sh '''
                        echo "📦 Construction de l'image Docker"
                        docker build -t dhiaghouma/kaddem-app:latest .
                    '''
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        sh '''
                            echo "🔐 Connexion à Docker Hub"
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                            echo "📤 Push de l'image vers Docker Hub"
                            docker push dhiaghouma/kaddem-app:latest

                            echo "🚪 Déconnexion de Docker Hub"
                            docker logout
                        '''
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
                stage('Check Monitoring (Prometheus & Grafana)') {
            steps {
                script {
                    echo "📡 Vérification de l'état de Prometheus et Grafana"
                    sh '''
                        echo "🔍 Checking Prometheus at http://192.168.33.10:9090/targets"
                        curl -s -o /dev/null -w "%{http_code}" http://192.168.33.10:9090/targets

                        echo "📊 Checking Grafana at http://192.168.33.10:3000"
                        curl -s -o /dev/null -w "%{http_code}" http://192.168.33.10:3000
                    '''
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
