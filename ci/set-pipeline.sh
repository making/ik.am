#!/bin/sh
echo y | fly -t home sp -p ik.am -c pipeline.yml -l ./credentials.yml
