FROM jenkins/jenkins:lts

# Pre-install the following plugins
COPY ./plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# Setup the Configuration as Code plugin
#COPY ./casc-config/jenkins.yaml /var/jenkins_home/jenkins.yaml
ENV CASC_JENKINS_CONFIG /var/jenkins_home/casc-config/
RUN mkdir -p /var/jenkins_home/casc-config
COPY ./casc-config/* /var/jenkins_home/casc-config/

# Skip the Setup Wizard
RUN echo 2.7.1 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state
RUN echo 2.7.1 > /usr/share/jenkins/ref/jenkins.install.InstallUtil.lastExecVersion