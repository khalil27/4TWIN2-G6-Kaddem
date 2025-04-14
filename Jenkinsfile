pipeline {
    agent any



    stages {
        stage('Git') {
            steps {
                    branch: 'khalilayari',
                    url: 'https://github.com/khalil27/4TWIN2-G6-Kaddem.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }



        stage('MVN Sonarqube') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=Kha51300906@ -Dmaven.test.skip=true'
            }
        }





    }

}
