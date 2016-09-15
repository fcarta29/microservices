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
_*Note: This command will run using the cached docker images. You need to add --build to regenerate the docker images with new changes
```
docker-compose up --build
```
To run by service type - for example start up redis :
```
docker-compose up redis
```

Swagger documentation:
`http://{docker_host}:9080/swagger-ui.html`
