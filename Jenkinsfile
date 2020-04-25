pipeline {

  options {
      timeout(time: 1, unit: 'HOURS')
      buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
  }

  agent {
    docker {
      image '616slayer616/jdk-selenium'
      alwaysPull true
    }
  }

  stages {

    stage('build') {
      steps {
        sh 'java -version'
        sh './gradlew clean assemble'
      }
    }
    stage('Test') {
      steps {
        sh './gradlew check'
      }
    }

    stage('Archive .jar'){
      steps {
        archiveArtifacts  'build/libs/**/*.jar'
      }
    }

    stage('release'){
      when{
        branch 'master'
      }
      steps {
        withCredentials([
        file(credentialsId: 'gradle-properties	', variable: 'FILE_PROPERTIES'),
        file(credentialsId: 'keyfile', variable: 'FILE_KEY')
        ]) {
          sh 'cp $FILE_PROPERTIES .'
          sh 'cp $FILE_KEY .'
        }
        sh './gradlew publish -Dorg.gradle.internal.publish.checksums.insecure=true'
      }
    }
  }

  post {
    success {
      echo "Build successful"
    }
    always {
      junit 'build/test-results/**/*.xml'
      cleanWs()
    }
  }
}
