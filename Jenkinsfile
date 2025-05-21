pipeline {
  agent any

  environment {
    DOCKERHUB_CREDENTIALS = credentials('dockerhub')
    DOCKER_REPO = 'nguyenthanhnhut13/trotot_backend'
    IMAGE_TAG = '1.0'
  }

  stages {
    stage('Check Branch') {
      steps {
        script {
          // Detect current branch if BRANCH_NAME is null
          def branchName = env.BRANCH_NAME ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
          if (branchName != 'feature/deploy') {
            echo "Pipeline only runs on feature/deploy branch. Current branch is ${branchName}. Skipping."
            currentBuild.result = 'SUCCESS'
            return
          }
          env.BRANCH_NAME = branchName // Set BRANCH_NAME for later stages
        }
      }
    }

    stage('Detect Changes') {
      when { expression { env.BRANCH_NAME == 'feature/deploy' } }
      steps {
        script {
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
      when { expression { env.BRANCH_NAME == 'feature/deploy' } }
      matrix {
        axes {
          axis {
            name 'SERVICE'
            values 'address-service', 'api-gateway', 'auth-service', 'discovery', 'media-service', 'notification-service', 'payment-service', 'recommendation-service', 'report-service', 'review-service', 'room-service', 'user-service'
          }
        }

        when {
          expression { changedServices.contains(SERVICE) }
        }

        stages {
          stage('Build') {
            steps {
              dir("${SERVICE}") {
                sh """
                  docker build -t ${DOCKER_REPO}-${SERVICE}:${IMAGE_TAG} .
                """
              }
            }
          }

          stage('Push') {
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