#!/usr/bin/env bash

# SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
# SPDX-License-Identifier: MIT

set -e

cd "$(dirname "$0")"
cp /code/home/assets/sttc/settings.xml .
git add settings.xml
git commit -m 'settings for dokku' --no-verify
trap 'git reset HEAD~1 && rm settings.xml' EXIT
git push heroku master -f
