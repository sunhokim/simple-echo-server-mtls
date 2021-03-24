#!/bin/bash

mvn clean package

docker build -t simple-echo-mtls:latest .

docker run -d -p 8443:8443 --restart=always --name=echo-server-mtls simple-echo-mtls:latest