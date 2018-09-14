#!/bin/bash
#
# Build script for CI builds on CentOS CI

set -ex

function setup() {
    if [ -f jenkins-env.json ]; then
        eval "$(./env-toolkit load -f jenkins-env.json \
                FABRIC8_HUB_TOKEN \
                FABRIC8_DOCKERIO_CFG \
                FABRIC8_MAVEN_SETTINGS \
                ghprbActualCommit \
                ghprbPullId \
                GIT_COMMIT \
                BUILD_ID)"

        mkdir -p ${HOME}/.docker
        echo ${FABRIC8_DOCKERIO_CFG}|base64 --decode > ${HOME}/.docker/config.json

        mkdir -p ${HOME}/.m2
        echo ${FABRIC8_MAVEN_SETTINGS}|base64 --decode > ${HOME}/.m2/settings.xml
    fi

    # We need to disable selinux for now, XXX
    /usr/sbin/setenforce 0 || :

    yum -y install docker make golang git

    curl -L http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -o/etc/yum.repos.d/epel-apache-maven.repo
    rpm --import /etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
    yum -y install apache-maven which

    service docker start

    echo 'CICO: Build environment created.'
}

function build() {
    local version="v$(git rev-parse --short ${GIT_COMMIT})"
    local fullversion="PR-${version}-${BUILD_ID}"

    mvn -U versions:set -DnewVersion=${fullversion}
    mvn clean -B -e -U deploy -Dmaven.test.skip=false -P openshift
    mvn fabric8:push -Ddocker.push.registry=https://index.docker.io/v1/

    return
}

function deploy() {
    return
}
