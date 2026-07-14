FROM mcr.microsoft.com/playwright:v1.55.0-jammy

WORKDIR /app

COPY package*.json ./
RUN npm ci || npm install

COPY . .

ENV CI=true
CMD ["npm", "test"]
