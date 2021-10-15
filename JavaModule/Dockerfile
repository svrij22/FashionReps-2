# ubuntu-openjdk-8-jdk
FROM ubuntu:bionic
MAINTAINER Sep Vrij <svrij22@test.com>

# This is in accordance to : https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04
RUN apt-get update && \
	apt-get install -y openjdk-11-jdk && \
	apt-get install -y ant && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk11-installer;

# Fix certificate issues, found as of
# https://bugs.launchpad.net/ubuntu/+source/ca-certificates-java/+bug/983302
RUN apt-get update && \
	apt-get install -y ca-certificates-java && \
	apt-get clean && \
	update-ca-certificates -f && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk11-installer;

# Setup JAVA_HOME, this is useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME

#Step 1 : Install the pre-requisite
RUN apt-get update
RUN apt-get install -y curl
RUN apt-get install -y p7zip \
    p7zip-full \
    unace \
    zip \
    unzip \
    bzip2

#Version numbers
ARG FIREFOX_VERSION=78.0.2
ARG CHROME_VERSION=83.0.4103.116
ARG CHROMDRIVER_VERSION=83.0.4103.39
ARG FIREFOXDRIVER_VERSION=0.26.0

#Step 2: Install Chrome
RUN curl http://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_$CHROME_VERSION-1_amd64.deb -o /chrome.deb
RUN apt-get install -y /chrome.deb
RUN rm /chrome.deb
#May cause mismatch? Check when done what version it is
#Step 3: Install chromedriver for Selenium
RUN mkdir -p /app/bin
RUN curl https://chromedriver.storage.googleapis.com/$CHROMDRIVER_VERSION/chromedriver_linux64.zip -o /tmp/chromedriver.zip \
    && unzip /tmp/chromedriver.zip -d /app/bin/ \
    && rm /tmp/chromedriver.zip
RUN chmod +x /app/bin/chromedriver
#Step 4 : Install firefox
#RUN wget --no-verbose -O /tmp/firefox.tar.bz2 https://download-installer.cdn.mozilla.net/pub/firefox/releases/$FIREFOX_VERSION/linux-x86_64/en-US/firefox-$FIREFOX_VERSION.tar.bz2 \
#  && bunzip2 /tmp/firefox.tar.bz2 \
#  && tar xvf /tmp/firefox.tar \
#  && mv ./firefox /opt/firefox-$FIREFOX_VERSION \
#  && ln -s /opt/firefox-$FIREFOX_VERSION/firefox /usr/bin/firefox
#Step 5: Install Geckodriver
#RUN wget https://github.com/mozilla/geckodriver/releases/download/v$FIREFOXDRIVER_VERSION/geckodriver-v$FIREFOXDRIVER_VERSION-linux64.tar.gz \
#    && tar -xf geckodriver-v0.26.0-linux64.tar.gz \
#    && cp geckodriver /app/bin/geckodriver
#RUN chmod +x /app/bin/geckodriver
#Step 6: Install Maven
# 1- Define Maven version
ARG MAVEN_VERSION=3.6.3
# 2- Define a constant with the working directory
ARG USER_HOME_DIR="/root"

# 3- Define the SHA key to validate the maven download
ARG SHA=c35a1803a6e70a126e80b2b3ae33eed961f83ed74d18fcd16909b2d44d7dada3203f1ffe726c17ef8dcca2dcaa9fca676987befeadc9b9f759967a8cb77181c0

# 4- Define the URL where maven can be downloaded from
ARG BASE_URL=https://mirror.koddos.net/apache/maven/maven-3/${MAVEN_VERSION}/binaries

# 5- Create the directories, download maven, validate the download, install it, remove downloaded file and set links
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && echo "Downloading maven" \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  \
  && echo "Checking download hash" \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  \
  && echo "Unzipping maven" \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  \
  && echo "Cleaning and setting links" \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# 6- Define environmental variables required by Maven, like Maven_Home directory and where the maven repo is located
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

#Step 7: Copy our project
COPY .. /app

#Making our working directory as /app
WORKDIR /app

#Clean install
RUN mvn clean install
ENTRYPOINT ["./entrypoint.sh"]