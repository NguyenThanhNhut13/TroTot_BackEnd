pipeline {
  agent none

  environment {
    DOCKERHUB_CREDENTIALS = credentials('dockerhub')
    DOCKER_REPO = 'nguyenthanhnhut13/trotot_backend'
    IMAGE_TAG = '1.0'
  }

  stages {
    stage('Detect Changes') {
      agent { label 'master' }
      steps {
        script {
          sh 'chmod +x detect-changes.sh'
          changedServices = sh(script: './detect-changes.sh', returnStdout: true).trim().tokenize()
          if (changedServices.size() == 0) {
            echo "No service changed. Skipping pipeline."
            currentBuild.result = 'SUCCESS'
            return
          }
        }
      }
    }

    stage('Build & Push Images') {
      matrix {
        axes {
          axis {
            name 'SERVICE'
            values 'address-service', 'api-gateway', 'auth-service', 'chatbox', 'config-server', 'discovery', 'media-service', 'notification-service', 'payment-service', 'recommendation-service', 'report-service', 'review-service', 'room-service', 'user-service'
          }
        }

        when {
          expression { changedServices.contains(SERVICE) }
        }

        stages {
          stage('Build') {
            agent {
              docker {
                image 'docker:20.10'
                args '-v /var/run/docker.sock:/var/run/docker.sock'
              }
            }
            steps {
              dir("${SERVICE}") {
                sh """
                  docker build -t ${DOCKER_REPO}-${SERVICE}:${IMAGE_TAG} .
                """
              }
            }
          }

          stage('Push') {
            agent {
              docker {
                image 'docker:20.10'
                args '-v /var/run/docker.sock:/var/run/docker.sock'
              }
            }
            steps {
              withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                sh """
                  echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                  docker push ${DOCKER_REPO}-${SERVICE}:${IMAGE_TAG}
                """
              }
            }
          }

          stage('Deploy to Render') {
            agent { label 'master' }
            steps {
              withCredentials([string(credentialsId: "render-${SERVICE}", variable: 'DEPLOY_HOOK')]) {
                sh """
                  curl -X POST "${DEPLOY_HOOK}"
                """
              }
            }
          }
        }
      }
    }
  }
}