#!/bin/sh
fly -t home sp -p ik.am \
    -c `dirname $0`/pipeline.yml \
    -l `dirname $0`/credentials.yml
