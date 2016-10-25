# BeastKeeper
**This is a project to manage all http and https proxy. If you want to use a lot of proxies to develope a spider, it is very useful.**

## Design
Based on netty.

## how to use
Configure the config file, file name : config
    
    # http proxy port for client
    request_port=4080
    # web console port
    jettyport=8008
    # request time out, milliseconds
    request_timeout=120000
    # mongodb information
    mongo_host=123.207.91.167
    mongo_port=27017
    mongo_user=root
    mongo_pass=123Yuanshuju456
    mongo_authticateDatabase=admin
    # http basic authentication string for this client
    request_authstring=yuanshuju:yuanshuju
    # max unhandled request number, exceeds will be thrown away 
    max_unhandled_request=3000
    # for each proxy request will be sended after at least this timeout. milliseonds
    min_timeout=20000
    
Run BeastKeeper.
  
    Run this class:  org.epiclouds.client.main.MainRun
    
View Console.
    
    view localhost:8008, username is admin123 password is 123Yuanshuju456

Now enjoy!
