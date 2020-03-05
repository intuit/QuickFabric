FROM node:lts-alpine

LABEL Description="Serverless Docker image for deploying quickfabric"
LABEL MAINTAINER="opensource@intuit.com"

COPY src ./
ENV SERVERLESS_VERSION=1.60.4
RUN apk upgrade --no-cache && apk add python3

RUN pip3 install --upgrade pip && \
    pip3 install -r requirements.txt

# Skip serverless version upgrade check https://github.com/serverless/serverless/issues/4319
RUN npm install -g try-thread-sleep && \
    npm install --global serverless@${SERVERLESS_VERSION} --ignore-scripts spawn-sync

RUN chmod +x post_harden.sh && ./post_harden.sh

ENTRYPOINT ["/bin/sh", "-c"]
