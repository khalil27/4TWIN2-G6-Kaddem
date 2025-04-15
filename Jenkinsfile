pipeline {
    agent any
    environment {
        DOCKER_REGISTRY = "192.168.33.10:8083"
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
                
                sh '''
                    echo "Workspace contents:"
                    ls -la
                    echo "Looking for pom.xml and Dockerfile:"
                    find . -name "pom.xml" -o -name "Dockerfile"
                '''
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
            // Set the correct build context to "kaddem/kaddem/"
            def customImage = docker.build("kaddem-app:latest", "kaddem/kaddem/")  // <-- Context is "kaddem/kaddem/"
        }
    }
}



        stage('Push Docker Image to Nexus') {
            steps {
                script {
                    docker.withRegistry("http://${DOCKER_REGISTRY}", '') {
                        def customImage = docker.image("kaddem-app:latest")
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
