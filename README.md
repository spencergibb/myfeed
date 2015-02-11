# myfeed
Myfeed is a sample cloud native application build using spring-cloud

infrastucture apps: port

* myfeed-config: 11010
* myfeed-discovery: 11020
* myfeed-router: 11080

user apps: port

* myfeed-admin: 11050
* myfeed-feed: 11060
* myfeed-user: 11070
* myfeed-ui: 11070

## external requirements

* mongodb
* redis
* rabbitmq
* github account

## /etc/hosts entries

    127.0.0.1   www.myfeed.com
    127.0.0.1   discovery.myfeed.com
    127.0.0.1   config.myfeed.com

## or setup dnsmasq on a mac

like so (in dnsmasq.conf)

    address=/myfeed.com/127.0.0.1

and add the following to `/etc/resolver/myfeed.com`

    nameserver 127.0.0.1

