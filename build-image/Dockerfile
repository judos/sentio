FROM ghcr.io/graalvm/native-image-community:21-ol9

# Install required build tools
RUN microdnf install -y \
        findutils \
        coreutils \
        gcc \
        gcc-c++ \
        make \
        zip \
        unzip \
        tar \
        gzip \
        which \
        git \
    && microdnf clean all

# Ensure a normal shell exists
SHELL ["/bin/bash", "-c"]

# ENTRYPOINT must accept CMD from docker run (required by Jenkins)
ENTRYPOINT ["/bin/bash", "-c"]
CMD ["sleep infinity"]
