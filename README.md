# Quarkus Config Server. A Supersonic alternative to Spring Cloud Config Server.

Quarkus Config Server implements the same endpoints as Spring Cloud Config Server with the same outputs. However this application boots faster and with less memory, ideal for k8s environments!.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Known Limitations vs Spring Cloud Config Server
- Supports only git repositories
- No encryption support
- No Placeholders in Git URI

# Benchmarks
Comparison have done with hyness/spring-cloud-config-server

docker run -it -p 8883:8888 -e SPRING_CLOUD_CONFIG_SERVER_GIT_URI=https://github.com/wansors/spring-cloud-config-samples hyness/spring-cloud-config-server


docker stats

## Liveness Probe
http://localhost:8888/q/health


## How supersonic is it?

Benchmark | JVM | Native | spring-cloud-config-server 
--- | --- | --- | --- 
Memory RRS | ~200MB | ~40MB |  ~400MB 
Boot time | 2 secs | 1sec |  ~16secs 
Docker size| - | - | - 



## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quarkus-config-server-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

# Build docker image

## Native
./mvnw package -Pnative "-Dquarkus.native.container-build=true"
docker build -f src/main/docker/Dockerfile.native -t quarkus/configserver .
docker run -i --rm -p 8888:8888  --name quarkusconfigserver-native quarkus/configserver

## JVM
./mvnw clean package
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/configserver-jvm .
docker run -i --rm -p 8881:8888 --name quarkusconfigserver-jvm quarkus/configserver-jvm



## Config Files preference

With git repositories, resources with file names in application* (application.properties, application.yml, application-*.properties, and so on) are shared between all client applications. You can use resources with these file names to configure global defaults and have them be overridden by application-specific files as necessary.

* application.(properties/yml), (General properties that apply to all applications and all profiles)
* {application}.(properties/yml) (Specific properties that apply to an  application-specific and all profiles)
* application-{profile}.(properties/yml) (General properties that apply to all applications and profile-specific )
* {application}-{profile}.(properties/yml) (Specific properties that apply to an application-specific  and a profile-specific )



## Placeholders in Git Search Paths
Config Server also supports a search path with placeholders for the {application} and {profile}