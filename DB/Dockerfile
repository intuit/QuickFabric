# Derived from official mysql image (our base image)
FROM mysql
# Add a database
#ENV MYSQL_DATABASE company
# Add the content of the sql-scripts/ directory to your image
# All scripts in docker-entrypoint-initdb.d/ are automatically
# executed during container startup
COPY quickfabric_bootstrap.sql /docker-entrypoint-initdb.d/
COPY patch /patch
RUN chmod 755 /patch/patch.sh
