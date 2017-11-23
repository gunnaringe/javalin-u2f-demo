# Javalin U2F Demo

This is based on the demo Java implementation by Yubico

This is intended as a quick demo, so everything is stored in-memory.
Feel free to make a pull request if you want to improve something.

## Note
U2F will not work unless hosted on https.

As we host on localhost, please just ignore any certificate warnings ;)
It is using a shitty self-sign certificate included in the repo, so please don't trust it...

## Build and run using maven
```shell
mvn package && java -jar target/hackathon.jar
```

## Usage
### Register new device
https://localhost:8443/demo/register.html

### Login
https://localhost:8443/demo/login.html
