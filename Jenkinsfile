pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                bat 'C:\\maven\\bin\\mvn.cmd compile'
            }
        }
        stage('Test') {
            steps {
                bat 'C:\\maven\\bin\\mvn.cmd test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Package') {
            steps {
                bat 'C:\\maven\\bin\\mvn.cmd package -DskipTests'
            }
        }
    }
}
