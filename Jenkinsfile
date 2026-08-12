pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
    }

    environment {
      JAR_PATH = 'documentmngr-app/target/documentmngr-app.jar'
      REPOSITORY_URL = 'ssh://root@192.168.56.104/root/git/bck-document-mngr.git'
      IMAGE_NAME = 'documentmngr-app'
      IMAGE_TAG = 'latest'
      CONTAINER_NAME = 'documentmngr-app'
      HOST_PORT = '8081'
      CONTAINER_PORT = '8080'
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'cr-020-enable-pagination-for-documents', description: 'Branch name')
    }

    stages {

        stage('Checkout') {
            steps {
                git url: "${REPOSITORY_URL}", branch: "${BRANCH_NAME}"
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Verify Artifact') {
            steps {
                sh '''
                    echo "Checking built artifact..."
                    test -f ${JAR_PATH}
                    ls -lh ${JAR_PATH}
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                '''
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker rm -f ${CONTAINER_NAME} || true'
                sh '''
                    docker run -d -p ${HOST_PORT}:${CONTAINER_PORT} --name ${CONTAINER_NAME} ${IMAGE_NAME}:${IMAGE_TAG}
                '''
            }
        }
    }
}