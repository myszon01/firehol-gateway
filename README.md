## Running locally
````
./mvnw clean install
./mvnw mn:run
````

## Running opensearch locally

Pull docker image
```
docker pull opensearchproject/opensearch
```

Run command
```
docker run -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" opensearchproject/opensearch:latest
```

Checking if opensearch is up and running
```
curl -XGET https://localhost:9200 -u admin:admin --insecure
curl -XGET https://localhost:9200/_cat/nodes?v -u admin:admin --insecure
curl -XGET https://localhost:9200/_cat/plugins?v -u admin:admin --insecure
```