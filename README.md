# microservices

### Build and Run
_*Note: all commands assume repo directory for working dir_

Requirements:
* docker
  * Make sure to add the `/data` directory to the list of directories that
  can be bind mounted into Docker containers,
  * Copy microservices-data/data-rest-test/dump.rdb to /data/microservices/dump.rdb to seed the database with defaults

To build all the targets:
```
docker run --rm -v "$PWD":/microservices -w /microservices maven:3.3.3-jdk-8 mvn clean install
```
To run:
```
docker-compose up
```
_*Note:_ This command will run using the cached docker images. You need to add --build to regenerate the docker images with new changes
```
docker-compose up --build
```
To run by service type - for example start up redis :
```
docker-compose up redis
```

### Data
To test basic CRUD for the data backend
```
docker-compose up data-rest-test
```
Swagger documentation:
`http://{docker_host}:9090/swagger-ui.html`

### Reservation Service
Reservation services for making and listing reservations
```
docker-compose up service-reservation
```
Swagger documentation:
`http://{docker_host}:9080/swagger-ui.html`

### Journal Service
Journal service for messaging ... not really used :)
```
docker-compose up service-journal
```
Swagger documentation:
`http://{docker_host}:9081/swagger-ui.html`

### Approval Service
Approval Service for making and listing approvals of reservation requests
```
docker-compose up service-approval
```
Swagger documentation:
`http://{docker_host}:9082/swagger-ui.html`

### Microservices Webapp
Microservices Webapp that serves as front end for service consumption (Currently only Reservation scheduling)
```
docker-compose up webapp
```
Web UI
`http://{docker_host}:8080
_*Click connect to get reservation listtings and enable realtime updates

### Test
To create the SoapUI testing Docker image:
```
docker build -t soaptest --rm -f test/Dockerfile-soapUI .
```
To run the SoapUI tests:
1. Get the gateway for your service:
```
gateway=$(docker inspect \
  --format='{{range .NetworkSettings.Networks}}{{.Gateway}}{{end}}' \
microservices_service-approval_1)
```
2. Run the tests, passing in your retrieved gateway var:
```
docker run --rm -e SERVICE=${gateway} soaptest
```
