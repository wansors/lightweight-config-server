# Lightweight Config Server
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-2-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

Lightweight Config Server is a quarkus based alternative to Spring Cloud Config Server.

Lightweight Config Server implements the same endpoints as Spring Cloud Config Server with the same outputs. However this application boots faster and consume less memory, ideal for k8s environments!.

The core uses https://github.com/smallrye/smallrye-config to generate the configurations.

This project uses Quarkus, the Supersonic Subatomic Java Framework (https://quarkus.io/).

In order to make a transition from spring cloud config server to Lightweight Config Server, the port, the monitoring path, etc are the same that on Spring.

## How it works?

### Request input
Each request to the config server has 3 parameters:
* {application} - Required - Determines the application
* {profile} - Required - Determines the profile
* {label} - Optional - Determine the Git Branch, if not inform, the default branch is used

### Finding the configuration

For each configuration request, the config server tries to read the following files on the Git repository (From less to more priority):

* 1) application.properties, (General properties that apply to all applications and all profiles)
* 2) application.yml, (General properties that apply to all applications and all profiles)
* 3) {application}.properties (Specific properties that apply to an  application-specific and all profiles)
* 4) {application}.yml (Specific properties that apply to an  application-specific and all profiles)
* 5) application-{profile}.properties (General properties that apply to all applications and profile-specific )
* 6) application-{profile}.yml (General properties that apply to all applications and profile-specific )
* 7) {application}-{profile}.properties (Specific properties that apply to an application-specific  and a profile-specific )
* 8) {application}-{profile}.yml (Specific properties that apply to an application-specific  and a profile-specific )

### Response output
With this information the config server find the configuration and merge it in a json or .porperties output.

	

## Features

### Json and .properties output

Configuration can be obtained in .properties and .json outputs. 

For JSON output, take into account that all configurations read from .properties git files are String. If you want to use other types, you shall use Yaml files.


### Same Logic as Microprofile Config 2.0 
1) Profile support on Property level

```
%dev.vehicle.name=car
%live.vehicle.name=train
%testing.vehicle.name=bike
vehicle.name=lorry
```

2) Property Expressions

```
server.url=http://${server.host:default_value}/endpoint
server.host=example.org
```

### Multirepository configurations - Filter by profile
It is possible to get a specific configuration from two different repositories.
Using the pattern-profile configuration, you can set which server will be used to get the first files using the {label}{application}{profile} from the request. Then the config server will look for the config key defined on pattern-profile-label-key and will use it to find the configuration in the other repository, using the value read and application from the previous call.

If the config field (pattern-profile) is not inform or there is no match on the request, this feature will not be active.

### Liveness Probe
http://localhost:8888/actuator/health

### Placeholders in Git Search Paths
Config Server also supports a search path with placeholders for the {application} and {profile}

### Use username/password or private key to access GIT
You can decide to use username and password or a private key to access GIT repositories.
When using private key, generate it with: ssh-keygen -t rsa -m PEM.
Then you can put the private file path on "private-key-path" inside the repository configuration

### How to avoid property expressions resolutions.

In order to avoid that the server resolves ${foo} expressions, and leaves them without processing, it can be done adding:

```
mp.config.property.expressions.enabled=false
```
 to the properties file

### Known Limitations vs Spring Cloud Config Server
- Supports only git repositories
- No encryption value support
- No Placeholders in Git URI

## How do I start?
Easy, just extend the base docker image and copy your configuration inside it:

```Docker
FROM wansors/lightweight-config-server:latest-native
COPY --chown=1001:root application.yml config/application.yml
```

Then, build the JVM image with: `docker build -f Dockerfile.native-custom -t example/lightweight-config-server:jvm .`
Then, build the native image with: `docker build -f src/main/docker/Dockerfile.native-multistage -t example/lightweight-config-server:native .`

Then run the container using: `docker run -i --rm -p 8888:8888 example/lightweight-config-server`


You can find the latest releases on: https://hub.docker.com/repository/docker/wansors/lightweight-config-server


## How supersonic is it?

Thanks to quarkus, Lightweight Config Server native image starts in seconds and with ~100MB of RAM.


## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/wansors"><img src="https://avatars.githubusercontent.com/u/15862396?v=4?s=100" width="100px;" alt=""/><br /><sub><b>wansors</b></sub></a><br /><a href="https://github.com/wansors/lightweight-config-server/commits?author=wansors" title="Tests">⚠️</a> <a href="https://github.com/wansors/lightweight-config-server/commits?author=wansors" title="Code">💻</a> <a href="#data-wansors" title="Data">🔣</a></td>
    <td align="center"><a href="https://github.com/jgspascual"><img src="https://avatars.githubusercontent.com/u/42868269?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Joan Sabater</b></sub></a><br /><a href="https://github.com/wansors/lightweight-config-server/commits?author=jgspascual" title="Tests">⚠️</a> <a href="https://github.com/wansors/lightweight-config-server/commits?author=jgspascual" title="Code">💻</a> <a href="https://github.com/wansors/lightweight-config-server/commits?author=jgspascual" title="Documentation">📖</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
