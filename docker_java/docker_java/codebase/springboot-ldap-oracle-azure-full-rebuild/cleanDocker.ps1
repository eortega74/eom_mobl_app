# Stop and remove all containers, networks, and volumes defined in docker-compose.yaml
docker-compose down -v

# Remove dangling images (optional, only if you want to free space)
docker image prune -f

# Build images without cache
docker-compose build --no-cache

# Start services
docker-compose up