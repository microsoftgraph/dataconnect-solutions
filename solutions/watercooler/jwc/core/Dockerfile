FROM java:8-jdk-alpine
MAINTAINER graphdataconnect.com

ENV SERVICE_USER=graphdataconnect
ENV JWC_CONF_DIR=/etc/graphdataconnect/jwc/conf/
ENV JWC_WORK_DIR=/usr/graphdataconnect/jwc
ENV JWC_LOG_DIR=/var/log/graphdataconnect/jwc
ENV PORT 8080
ENV SSH_PORT 2222

ENV TERM=xterm-256color \
 LANG=en_US.UTF-8 \
 LANGUAGE=en_US \
 LC_CTYPE="en_US.UTF-8" \
 LC_NUMERIC="en_US.UTF-8" \
 LC_TIME="en_US.UTF-8" \
 LC_COLLATE="en_US.UTF-8" \
 LC_MONETARY="en_US.UTF-8" \
 LC_MESSAGES="en_US.UTF-8" \
 LC_PAPER="en_US.UTF-8" \
 LC_NAME="en_US.UTF-8" \
 LC_ADDRESS="en_US.UTF-8" \
 LC_TELEPHONE="en_US.UTF-8" \
 LC_MEASUREMENT="en_US.UTF-8" \
 LC_IDENTIFICATION="en_US.UTF-8" \
 DEBIAN_FRONTEND=noninteractive

RUN apk update && apk add ca-certificates && update-ca-certificates && apk add openssl && apk add --no-cache bash curl nano net-tools \
        && apk add openssh \
        && ssh-keygen -f /etc/ssh/ssh_host_rsa_key -N '' -t rsa \
        && echo "root:Docker!" | chpasswd \
        && rm -rf /var/cache/apk/* \
        && rm -rf "/tmp/"*

RUN adduser -h /home/$SERVICE_USER -g 'GraphDataConnect Service User' -D $SERVICE_USER $SERVICE_USER  && \
        echo "$SERVICE_USER ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

RUN mkdir -p ${JWC_LOG_DIR} ${JWC_CONF_DIR}
RUN chown -R ${SERVICE_USER} ${JWC_LOG_DIR}


COPY bin/sshd_config /etc/ssh/

EXPOSE ${SSH_PORT} ${PORT}

COPY target/jwc-core.jar ${JWC_WORK_DIR}/
COPY bin/start-jwc.sh  ${JWC_WORK_DIR}/

WORKDIR  ${JWC_WORK_DIR}

CMD ${JWC_WORK_DIR}/start-jwc.sh
