#!/bin/bash

set -x

BRANCH="master"

# Are we on the right branch?
if [ "$TRAVIS_BRANCH" = "$BRANCH" ]; then 
    # Is this not a Pull Request? 

    if [ "$TRAVIS_PULL_REQUEST" = false ]; then 
       # Is this not a build which was triggered by setting a new tag? 

       if [ -z "$TRAVIS_TAG" ]; then

           echo -e "Starting to tag commit.\n" 
           git config --global user.email "memellis@gmail.com"
           git config --global user.name "memellis" 

           # Add tag and push to master.

           git tag -a v${TRAVIS_BUILD_NUMBER} -m "Travis build $TRAVIS_BUILD_NUMBER pushed a tag." 
           git push https://${GH_TOKEN}@github.com/${TRAVIS_REPO_SLUG} --tags
           git fetch origin

           echo -e "Done magic with tags.\n"
        fi
    fi
fi
