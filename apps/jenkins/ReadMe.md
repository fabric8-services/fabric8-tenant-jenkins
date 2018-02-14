Jenkins CI is a leading open-source continuous integration server. Built with Java, it provides 985 plugins to support building and testing virtually any project.


Configurations:
--------------------------------------
 - `JENKINS_ROOT_URL` : By default, the Jenkins base URL for the build log is determined via the OpenShift route of the Jenkins service. To override and configure this base URL, an environment variable `JENKINS_ROOT_URL` can be set through [openshift deployment-config yaml](https://github.com/fabric8-services/fabric8-tenant-jenkins/blob/master/apps/jenkins/src/main/fabric8/openshift-deployment.yml)

[http://jenkins-ci.org/](http://jenkins-ci.org/)
