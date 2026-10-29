FROM jenkins/jenkins:lts-jdk17
USER root

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# Docker CLI only (the daemon is your host's, via the mounted socket)
RUN ARCH=$(uname -m) \
 && curl -fsSL "https://download.docker.com/linux/static/stable/${ARCH}/docker-27.3.1.tgz" \
  | tar -xz -C /usr/local/bin --strip-components=1 docker/docker

# kubectl
RUN ARCH=$(dpkg --print-architecture) \
 && curl -fsSL -o /usr/local/bin/kubectl \
    "https://dl.k8s.io/release/$(curl -fsSL https://dl.k8s.io/release/stable.txt)/bin/linux/${ARCH}/kubectl" \
 && chmod +x /usr/local/bin/kubectl

# Helm and Trivy
RUN curl -fsSL https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
RUN curl -sfL https://raw.githubusercontent.com/aquasecurity/trivy/main/contrib/install.sh | sh -s -- -b /usr/local/bin

# Run as root in this lab setup so Jenkins can use the mounted Docker socket
USER root
