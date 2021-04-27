FROM selenium/standalone-chrome-debug
EXPOSE 5900-6000
EXPOSE 4444-4544

# Install dependencies
USER root

# Install JDK
RUN apt-get -qq update
RUN apt-get -qq -y install software-properties-common
RUN \
  echo debconf shared/accepted-oracle-license-v1-2 select true | debconf-set-selections && \
  echo debconf shared/accepted-oracle-license-v1-2 seen true | debconf-set-selections && \
  add-apt-repository ppa:linuxuprising/java && \
  apt-get update && \
  apt-get install -qq -y default-jdk && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-java11-installer

# Install JMeter
ARG JMETER_VERSION="5.1.1"
ENV JMETER_HOME /opt/apache-jmeter-${JMETER_VERSION}
ENV JMETER_BIN  ${JMETER_HOME}/bin
ENV JMETER_DOWNLOAD_URL https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz
ENV JMETER_PLUGINS_MANAGER_VERSION 1.3
ENV CMDRUNNER_VERSION 2.2

RUN apt-get update && \
  apt-get install -qq -y curl unzip && \
  mkdir -p /tmp/dependencies && \
  curl -L --silent ${JMETER_DOWNLOAD_URL} > /tmp/dependencies/apache-jmeter-${JMETER_VERSION}.tgz && \
  mkdir -p /opt && \
  tar -xzf /tmp/dependencies/apache-jmeter-${JMETER_VERSION}.tgz -C /opt && \
  rm -rf /tmp/dependencies

# Set global PATH such that "jmeter" command is found
ENV PATH $PATH:$JMETER_BIN

# Install JMeter Plugins
RUN curl --location --silent --show-error --output ${JMETER_HOME}/lib/ext/jmeter-plugins-manager-${JMETER_PLUGINS_MANAGER_VERSION}.jar http://search.maven.org/remotecontent?filepath=kg/apc/jmeter-plugins-manager/${JMETER_PLUGINS_MANAGER_VERSION}/jmeter-plugins-manager-${JMETER_PLUGINS_MANAGER_VERSION}.jar \
 && curl --location --silent --show-error --output ${JMETER_HOME}/lib/cmdrunner-${CMDRUNNER_VERSION}.jar http://search.maven.org/remotecontent?filepath=kg/apc/cmdrunner/${CMDRUNNER_VERSION}/cmdrunner-${CMDRUNNER_VERSION}.jar \
 && curl --location --silent --show-error --output ${JMETER_HOME}/lib/json-lib-${JSON_LIB_FULL_VERSION}.jar https://search.maven.org/remotecontent?filepath=net/sf/json-lib/json-lib/${JSON_LIB_VERSION}/json-lib-${JSON_LIB_FULL_VERSION}.jar \
 && java -cp ${JMETER_HOME}/lib/ext/jmeter-plugins-manager-${JMETER_PLUGINS_MANAGER_VERSION}.jar org.jmeterplugins.repository.PluginManagerCMDInstaller \
 && PluginsManagerCMD.sh install jpgc-webdriver 
 #install-all-except

RUN curl --location --silent --show-error --output  ${JMETER_HOME}/lib/guava-19.0.0.jar http://search.maven.org/remotecontent?filepath=com/google/guava/guava/22.0/guava-22.0.jar

COPY launch.sh /
RUN chmod +x /launch.sh

WORKDIR ${JMETER_HOME}

ENTRYPOINT ["/launch.sh"]
