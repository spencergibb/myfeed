# myfeed
Myfeed is a non-trivial sample cloud native application build using:

* spring-cloud
* spring-boot
* spring-data

# build

Java 8. We use the [takari/maven-wrapper](https://github.com/takari/maven-wrapper).

```
./mvnw clean package
```

or (on windows)

```
mvnw.bat clean package
```

## TODO

- [X] RxJava Sample
- [X] Following
- [X] login/logout
- [X] Posting
- [X] Post feed items to following users
- [X] Profile view
- [ ] Create UI View using stream, replace live aggregating with uiview
- [ ] Security
- [ ] RedisSession
- [ ] Spring Restdocs
- [ ] Websockets update feed: https://spring.io/guides/gs/messaging-stomp-websocket/
- [ ] Non-java service
- [ ] Unfollowing
- [ ] Profile edit

## Services

infrastucture apps (id: default port)

* myfeed-config: 11010
* myfeed-discovery: 11020
* myfeed-router: 11080
* myfeed-turbine: 11090

user apps (id: default port)

* myfeed-admin: 11050
* myfeed-feed: 11060
* myfeed-user: 11070
* myfeed-ui: 11040

## external requirements

* redis
* github account

## /etc/hosts entries

    127.0.0.1   www.myfeed.com
    127.0.0.1   discovery.myfeed.com
    127.0.0.1   config.myfeed.com

## or setup dnsmasq on a mac

like so (in dnsmasq.conf)

    address=/myfeed.com/127.0.0.1
    listen-address=127.0.0.1

and add the following to `/etc/resolver/myfeed.com`

    nameserver 127.0.0.1


