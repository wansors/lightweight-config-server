# Lightweight Config Server
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

Lightweight Config Server is a quarkus based alternative to Spring Cloud Config Server.

Lightweight Config Server implements the same endpoints as Spring Cloud Config Server with the same outputs. However this application boots faster and consume less memory, ideal for k8s environments!.

This project uses Quarkus, the Supersonic Subatomic Java Framework (https://quarkus.io/).



## Features
### Known Limitations vs Spring Cloud Config Server
- Supports only git repositories
- No encryption value support
- No Placeholders in Git URI

### Multirepository configurations - Filter by profile
It is possible to get a specific configuration from two different repositories.
Using the pattern-profile configuration, you can set which server will be used to get the first files using the {label}{application}{profile} from the request. Then the config server will look for the config key defined on pattern-profile-label-key and will use it to find the configuration in the other repository, using the value read and application from the previous call, the profile will not be used on the second call.

If the field is not inform or there is no match, this feature will not be active.

### Liveness Probe
http://localhost:8888/actuator/health

### Config Files preference

With git repositories, resources with file names in application* (application.properties, application.yml, application-*.properties, and so on) are shared between all client applications. You can use resources with these file names to configure global defaults and have them be overridden by application-specific files as necessary.

* application.(properties/yml), (General properties that apply to all applications and all profiles)
* {application}.(properties/yml) (Specific properties that apply to an  application-specific and all profiles)
* application-{profile}.(properties/yml) (General properties that apply to all applications and profile-specific )
* {application}-{profile}.(properties/yml) (Specific properties that apply to an application-specific  and a profile-specific )

### Placeholders in Git Search Paths
Config Server also supports a search path with placeholders for the {application} and {profile}

## How do I start?
Easy, just extend the base docker image and copy your configuration in Yaml format on TODO



## Delete From Here

### Benchmarks
Comparison have done with hyness/spring-cloud-config-server

docker run -it -p 8883:8888 -e SPRING_CLOUD_CONFIG_SERVER_GIT_URI=https://github.com/wansors/spring-cloud-config-samples hyness/spring-cloud-config-server


docker stats




### How supersonic is it?

Benchmark | JVM | Native | spring-cloud-config-server 
--- | --- | --- | --- 
Memory RRS | ~200MB | ~40MB |  ~400MB 
Boot time | 2 secs | 1sec |  ~16secs 
Docker size| - | - | - 



### Creating a native executable

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










### Build docker image

##### Native
./mvnw package -Pnative "-Dquarkus.native.container-build=true"
docker build -f src/main/docker/Dockerfile.native -t quarkus/configserver .
docker run -i --rm -p 8888:8888  --name quarkusconfigserver-native quarkus/configserver

#### JVM
./mvnw clean package
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/configserver-jvm .
docker run -i --rm -p 8881:8888 --name quarkusconfigserver-jvm quarkus/configserver-jvm

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/jgspascual"><img src="https://avatars.githubusercontent.com/u/42868269?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Joan Sabater</b></sub></a><br /><a href="https://github.com/wansors/lightweight-config-server/commits?author=jgspascual" title="Tests">⚠️</a> <a href="https://github.com/wansors/lightweight-config-server/commits?author=jgspascual" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!