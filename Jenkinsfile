pipeline {
  agent any

  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }

  parameters {
    choice(name: 'BUILD_TOOL', choices: ['maven', 'gradle'], description: 'Build tool to use')
    booleanParam(name: 'RUN_SONAR', defaultValue: false, description: 'Run SonarQube analysis (Maven only)')
  }

  environment {
    REGISTRY = 'docker.io/tenzo06'
    IMAGE    = 'javaapp'
    TAG      = "${env.BUILD_NUMBER}"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build & Test') {
      steps {
        script {
          sh 'chmod +x mvnw gradlew || true'
          if (params.BUILD_TOOL == 'gradle') {
            sh './gradlew clean build'
            sh 'cp $(ls build/libs/*.jar | grep -v plain) app.jar'
          } else {
            sh './mvnw -B clean verify'
            sh 'cp target/javaapp-*.jar app.jar'
          }
        }
      }
      post {
        always {
          junit allowEmptyResults: true,
                testResults: '**/target/surefire-reports/*.xml, **/build/test-results/test/*.xml'
        }
      }
    }

    stage('SonarQube Analysis') {
      when { expression { params.RUN_SONAR && params.BUILD_TOOL == 'maven' } }
      steps {
        withSonarQubeEnv('sonar') { sh './mvnw -B sonar:sonar' }
      }
    }

    stage('Quality Gate') {
      when { expression { params.RUN_SONAR && params.BUILD_TOOL == 'maven' } }
      steps {
        timeout(time: 5, unit: 'MINUTES') { waitForQualityGate abortPipeline: true }
      }
    }

    stage('Docker Build') {
      steps {
        sh 'docker build --pull -t $REGISTRY/$IMAGE:$TAG -t $REGISTRY/$IMAGE:latest .'
      }
    }

    stage('Trivy Scan') {
      steps {
        sh 'trivy image --scanners vuln --timeout 15m --cache-dir /var/jenkins_home/.trivy-cache --exit-code 1 --severity CRITICAL --ignore-unfixed $REGISTRY/$IMAGE:$TAG'
      }
    }

    stage('Push Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub',
                                          usernameVariable: 'DH_USER',
                                          passwordVariable: 'DH_PASS')]) {
          sh '''
            echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
            docker push $REGISTRY/$IMAGE:$TAG
            docker push $REGISTRY/$IMAGE:latest
          '''
        }
      }
    }

    stage('Deploy to Dev') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
          sh '''
            helm upgrade --install javaapp helm/javaapp \
              -n dev --create-namespace \
              -f helm/javaapp/values-dev.yaml \
              --set image.repository=$REGISTRY/$IMAGE \
              --set image.tag=$TAG \
              --wait --timeout 3m
          '''
        }
      }
    }

    stage('Approval for Prod') {
      steps {
        timeout(time: 30, unit: 'MINUTES') { input message: 'Promote to production?' }
      }
    }

    stage('Deploy to Prod') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
          sh '''
            helm upgrade --install javaapp helm/javaapp \
              -n prod --create-namespace \
              -f helm/javaapp/values-prod.yaml \
              --set image.repository=$REGISTRY/$IMAGE \
              --set image.tag=$TAG \
              --atomic --timeout 3m
          '''
        }
      }
    }
  }

  post {
    success { echo "Build ${env.BUILD_NUMBER} deployed successfully" }
    failure { echo "Pipeline failed, check the stage logs" }
    always  { sh 'docker logout || true' }
  }
}
