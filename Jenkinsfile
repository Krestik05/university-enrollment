pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                bat 'C:\\maven\\bin\\mvn.cmd compile'
            }
        }
        stage('Static Analysis') {
            steps {
                bat 'C:\\maven\\bin\\mvn.cmd checkstyle:check'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/checkstyle-result.xml', allowEmptyArchive: true
                }
            }
        }
        stage('Test') {
            steps {
                bat 'C:\\maven\\bin\\mvn.cmd test'
            }
            post {
                always {

                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/logs/*.log', allowEmptyArchive: true
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
