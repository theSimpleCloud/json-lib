pipeline {
    agent any
    stages {
        stage('Clean') {
            steps {
                sh 'chmod +x ./gradlew';
                sh './gradlew clean';
            }
        }
        stage('Build') {
            steps {
                sh './gradlew jar';
            }
        }
        stage('Sources') {
            steps {
                sh './gradlew sourceJar';
            }
        }
        stage('Publish') {
           steps {
                sh './gradlew publish';
           }
        }
    }
}