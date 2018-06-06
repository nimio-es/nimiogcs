FROM websphere-liberty:webProfile6

RUN apt-get update \
  && apt-get install -y --no-install-recommends ssh \
  && rm -rf /var/lib/apt/lists/*

COPY server.xml /config/
COPY target/nimiogcs-ear/lib/ojdbc6-12.1.0.1-atlassian-hosted.jar /config/lib/
COPY target/nimiogcs-ear/nimiogcs-web.war /config/app/nimiogcs.war

COPY wait-for-db.sh /opt/wait-for-db/

CMD ["/opt/wait-for-db/wait-for-db.sh", "db", "/opt/ibm/docker/docker-server", "run", "defaultServer"]
