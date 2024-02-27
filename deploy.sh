#!/bin/bash
set -e

cd $(dirname $0)
cp /code/home/assets/sttc/settings.xml .
git add settings.xml
git commit -m 'settings for dokku'
trap 'git reset HEAD~1 && rm settings.xml' EXIT
git push heroku master -f

