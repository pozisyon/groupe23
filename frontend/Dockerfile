FROM node:18-alpine AS build
WORKDIR /app
COPY package.json package-lock.json* yarn.lock* pnpm-lock.yaml* ./
RUN npm install || true
COPY . .
RUN npm run build
FROM node:18-alpine
WORKDIR /app
COPY --from=build /app /app
EXPOSE 5173
CMD ["npm","run","preview","--","--host","0.0.0.0","--port","5173"]
