#!/usr/bin/groovy
def stage(){
  return stageProject{
    project = 'fabric8-services/fabric8-tenant-jenkins'
    useGitTagForNextVersion = true
  }
}

def approveRelease(project){
  def releaseVersion = project[1]
  approve{
    room = null
    version = releaseVersion
    console = null
    environment = 'fabric8'
  }
}

def release(project){
  releaseProject{
    stagedProject = project
    useGitTagForNextVersion = true
    helmPush = false
    groupId = 'io.fabric8.tenant'
    githubOrganisation = 'fabric8io'
    artifactIdToWatchInCentral = 'tenant-jenkins'
    artifactExtensionToWatchInCentral = 'pom'
    promoteToDockerRegistry = 'docker.io'
    dockerOrganisation = 'fabric8'
    imagesToPromoteToDockerHub = []
    extraImagesToTag = ['nexus']
  }
}

def updatefabric8Tenant(releaseVersion){
  ws{
    container(name: 'clients') {
      def flow = new io.fabric8.Fabric8Commands()
      sh 'chmod 600 /root/.ssh-git/ssh-key'
      sh 'chmod 600 /root/.ssh-git/ssh-key.pub'
      sh 'chmod 700 /root/.ssh-git'

      git 'git@github.com:fabric8-services/fabric8-tenant.git'

      sh "git config user.email fabric8cd@gmail.com"
      sh "git config user.name fabric8-cd"

      def uid = UUID.randomUUID().toString()
      sh "git checkout -b versionUpdate${uid}"

      sh "echo ${releaseVersion} > JENKINS_VERSION"
      def message = "Update fabric8-tenant-jenkins version to ${releaseVersion}"
      sh "git commit -a -m \"${message}\""
      sh "git push origin versionUpdate${uid}"

      def prId = flow.createPullRequest(message,'fabric8-services/fabric8-tenant',"versionUpdate${uid}")
      flow.mergePR('fabric8-services/fabric8-tenant',prId)
    }
  }
}
return this;
