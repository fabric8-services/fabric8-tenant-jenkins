#!/usr/bin/groovy
@Library('github.com/fabric8io/fabric8-pipeline-library@master')
def utils = new io.fabric8.Utils()
def flow = new io.fabric8.Fabric8Commands()
releaseNode {
  try {
    checkout scm
    readTrusted 'release.groovy'

    if (utils.isCI()) {

      def version = mavenCI{}
      // hard coded for now
      def mvnRepo = "http://nexus.cd.k8s.fabric8.io/content/repositories/snapshots"
      def message = "snapshot YAML now available: `JENKINS_VERSION=${version}` `YAML_MVN_REPO=${mvnRepo}`"

      stage('notify'){
          def pr = env.CHANGE_ID
          if (!pr){
              error "no pull request number found so cannot comment on PR"
          }
          container('clients'){
              flow.addCommentToPullRequest(message, pr, 'fabric8-services/fabric8-tenant-jenkins')
          }
      }
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
        pipeline.updatefabric8Tenant(stagedProject[1])
      }
    }
  } catch (err) {
    hubot room: 'release', message: "${env.JOB_NAME} failed: ${err}"
    error "${err}"
  }
}
