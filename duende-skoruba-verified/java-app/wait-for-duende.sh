#!/bin/sh

echo "⏳ Waiting for Duende IdentityServer to be ready..."

until curl -sSf http://identity-server:5000/.well-known/openid-configuration > /dev/null; do
  echo "⏱ Still waiting for IdentityServer at http://identity-server:5000..."
  sleep 2
done

echo "✅ IdentityServer is UP, starting Java app..."
exec java -jar app.jar
