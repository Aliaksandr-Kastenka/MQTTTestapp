Consumer:
java -jar target/MQTTTest.jar -a subscribe -t newTopic -b 10.177.178.135 -p 1883

Producer:
java -jar target/MQTTTest.jar -a publish -m 'Test Messages' -t newTopic -b 10.177.178.135 -p 1883