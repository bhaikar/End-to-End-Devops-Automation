# Java DevOps Project

End-to-end DevOps automation for a Spring Boot app using Maven, Gradle, Jenkins, Docker, Kubernetes and Helm.

```
Git push -> Jenkins -> Maven/Gradle build+test -> Docker image -> Trivy scan -> Docker Hub
         -> Helm deploy (dev) -> manual approval -> Helm deploy (prod) on Kubernetes
```

## Layout
- `src/` Spring Boot app (`/hello`, `/actuator/health`, `/actuator/prometheus`)
- `pom.xml`, `build.gradle` Maven (primary) and Gradle (alternative) builds
- `Dockerfile` multi-stage image, non-root runtime
- `jenkins/Dockerfile` Jenkins image with docker, kubectl, helm, trivy
- `Jenkinsfile` declarative pipeline
- `helm/javaapp/` chart with dev/prod values

## Setup roadmap
1. Install prerequisites
2. Build and test locally (Maven and Gradle)
3. Build and run the Docker image
4. Push the code to GitHub
5. Create a local Kubernetes cluster
6. Deploy manually with Helm
7. Run Jenkins and configure credentials
8. Create the pipeline job and webhook
9. Add SonarQube
10. Monitoring and rollback

Replace `YOUR_DOCKERHUB_USER` in `Jenkinsfile` and `helm/javaapp/values.yaml` with your Docker Hub username.

#Jenkins url
http://localhost:8090/