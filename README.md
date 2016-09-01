# microservices

### Build and Run
_*Note: all commands assume repo directory for working dir_

Requirements:
* docker
  * Make sure to add the `/data` directory to the list of directories that 
  can be bind mounted into Docker containers,

To build all the targets:
```
docker run --rm -v "$PWD":/microservices -w /microservices maven:3.3.3-jdk-8 mvn clean install
```
To run:
```
docker-compose up
```
_*Note: only the data-rest-test services is currently implemented_

Swagger documentation:
`http://{docker_host}:9080/swagger-ui.html`
