#!/usr/bin/groovy
@Library('github.com/fabric8io/fabric8-pipeline-library@master')
def utils = new io.fabric8.Utils()
releaseNode {
  try {
    checkout scm
    readTrusted 'release.groovy'

    if (utils.isCI()) {

      mavenCI{}

    } else if (utils.isCD()) {
      sh "git remote set-url origin git@github.com:fabric8-services/fabric8-tenant-jenkins.git"

      def pipeline = load 'release.groovy'
      def stagedProject

      stage('Stage') {
        stagedProject = pipeline.stage()
      }

      stage('Promote') {
        pipeline.release(stagedProject)
      }

      stage ('Update downstream dependencies'){
        pipeline.updatefabric8Tenant(stagedProject)
      }
    }
  } catch (err) {
    hubot room: 'release', message: "${env.JOB_NAME} failed: ${err}"
    error "${err}"
  }
}
