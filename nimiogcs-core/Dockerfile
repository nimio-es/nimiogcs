FROM wnameless/oracle-xe-11g

RUN echo "echo '*** FIN DE LA INICIALIZACION ***'" >> /usr/sbin/startup.sh \
  && echo "touch /tmp/dbinitialized"

ADD sql/init.sql /docker-entrypoint-initdb.d/

