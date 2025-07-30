#!/bin/sh
echo "Waiting for Duende IdentityServer..."
until curl -s http://identity-server:5000 > /dev/null; do
  sleep 2
done
echo "Duende is up, starting the app..."
exec java -jar app.jar
