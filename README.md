# Sample code to demonstrate the ability of leveraging multimple schemas in the same topic using Scala code

- Start the basic infrastructure defined in the `docker-compose.yml` file:
```bash
$ docker-compose up 
```
- Verify that the service are running:
```bash 
$ docker-compose ps                                                                                                                                                                                                                                 ✘ 1
              Name                        Command            State                Ports
  ---------------------------------------------------------------------------------------------------
  kafka                          /etc/confluent/docker/run   Up      0.0.0.0:9092->9092/tcp
  multiple-schemas_sr_1          /etc/confluent/docker/run   Up      8081/tcp, 0.0.0.0:8085->8085/tcp
  multiple-schemas_zookeeper_1   /etc/confluent/docker/run   Up      2181/tcp, 2888/tcp, 3888/tcp
```

- Start the `io.confluent.Consumer` class
- Start the `io.confluent.Producer` class
- Check out the standard consumer output to verify messages are correctly deserialized.  