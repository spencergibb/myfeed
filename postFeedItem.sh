#!/bin/sh
USER=${1:-spencergibb}
TEXT=${2:-Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit}
URL_BASE=${3:-http://feed.myfeed.com:11060}
curl -i -X POST -H "Content-Type: application/json" ${URL_BASE}/@${USER} --data-binary "$TEXT"
