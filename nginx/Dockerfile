FROM nginx:1.27-alpine

# подтягиваем njs
RUN apk add --no-cache nginx-mod-http-js

COPY nginx.conf /etc/nginx/nginx.conf
COPY echo.js /etc/nginx/echo.js