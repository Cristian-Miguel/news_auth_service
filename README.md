## Auth service for the news app

To create SSL key run this commands

```
keytool -keystore kafka.broker.keystore.jks -alias kafka-broker -keyalg RSA -validity 365 -genkey -keypass your_key_password -storepass your_keystore_password -dname "CN=localhost, OU=YourUnit, O=YourOrganization, L=YourCity, ST=YourState, C=YourCountry"
```

```
keytool -export -alias kafka-broker -keystore kafka.broker.keystore.jks -file kafka-broker-cert.crt -storepass your_keystore_password -rfc
```

```
keytool -keystore kafka.client.truststore.jks -alias kafka-broker -importcert -file kafka-broker-cert.crt -storepass your_truststore_password
```

```
keytool -keystore kafka.client.keystore.jks -alias kafka-client -keyalg RSA -validity 365 -genkey
```

Check the data from the cert
```
keytool -list -v -keystore kafka.broker.keystore.jks -storepass your_keystore_password
```