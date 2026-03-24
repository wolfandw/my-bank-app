#!/bin/sh

/tmp/bootstrap/init.sh &

consul agent -dev -ui -client=0.0.0.0 -log-level=ERROR