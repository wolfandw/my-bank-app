FROM hashicorp/consul:1.20
WORKDIR /app
RUN mkdir /tmp/bootstrap

COPY consul/bootstrap/* /tmp/bootstrap/
RUN chmod 755 /tmp/bootstrap/*
RUN dos2unix /tmp/bootstrap/*

CMD /tmp/bootstrap/start.sh