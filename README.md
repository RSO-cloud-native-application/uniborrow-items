# RSO: Items microservice

## Prerequisites

```bash
docker run -d --name pg-items -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=items -p 5432:5432 postgres:13
```

## Build and run commands
```bash
mvn clean package
cd api/target
java -jar items-api-1.0.0-SNAPSHOT.jar
```
Available at: localhost:8080/v1/items

## Docker commands
```bash
docker build -t items .   
docker items
docker run items    
docker tag items mp6079/items   
docker push mp6079/items  
```

## Docker and environmental variables 
```bash
docker run --help
docker run -e MY_VAR=123
docker ps
docker build -t rso-dn
docker network ls
docker network create rso
docker network rm rso
docker rm -f pg-items
docker run -d --name pg-items -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=image-metadata -p 5432:5432 --network rso postgres:13
docker inspect pg-items
docker run -p 8080:8080 --network rso -e KUMULUZEE_DATASOURCES0_CONNECTIONURL=jdbc:postgresql://pg-items:5432/items rso-dn
```

## Consul
```bash
consul agent -dev
```
Available at: localhost:8500


## Kubernetes
```bash
kubectl version
kubectl --help
kubectl get nodes
kubectl create -f items-deployment.yaml 
kubectl apply -f items-deployment.yaml 
kubectl get services 
kubectl get deployments
kubectl get pods
kubectl logs items-deployment-6f59c5d96c-rjz46
kubectl delete pod items-deployment-6f59c5d96c-rjz46
```
