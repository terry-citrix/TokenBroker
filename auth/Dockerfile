FROM ubuntu:18.04

# Exit immediately if a command exits with a non-zero status.
RUN set -e

# Print commands and their arguments as they are executed.
RUN set -x

# To skip interactive post-install configuration steps
RUN export DEBIAN_FRONTEND=noninteractive

# Fetches the list of available updates
RUN apt-get update

# Installs Java (Open JDK version)
RUN apt-get install -y openjdk-8-jre

# Display date / time in history
RUN echo 'export HISTTIMEFORMAT="%d/%m/%y %T "' >> /etc/bash.bashrc

EXPOSE 8081:8081

COPY build/libs/auth.war app.war

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xms512m","-Xmx2048m","-jar","/app.war"]