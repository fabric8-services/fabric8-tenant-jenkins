#!/bin/bash
set -xe

cd $(dirname $(readlink -f $0))/../
source .cico/setup.sh

setup

deploy
