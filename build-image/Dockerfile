FROM ghcr.io/graalvm/native-image-community:21-ol9

# Install required build tools
RUN microdnf install -y \
        findutils \
        zip \
        unzip \
        tar \
        gzip \
        which \
        git \
    && microdnf clean all

# Install Gradle 8.14
ENV GRADLE_VERSION=8.14
ENV GRADLE_HOME=/opt/gradle
ENV PATH=${GRADLE_HOME}/bin:${PATH}

RUN curl -fsSL https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle.zip \
    && unzip gradle.zip -d /opt \
    && mv /opt/gradle-${GRADLE_VERSION} ${GRADLE_HOME} \
    && rm gradle.zip

# Ensure a normal shell exists
SHELL ["/bin/bash", "-c"]

# ENTRYPOINT must accept CMD from docker run (required by Jenkins)
ENTRYPOINT ["/bin/bash", "-c"]
CMD ["sleep infinity"]
