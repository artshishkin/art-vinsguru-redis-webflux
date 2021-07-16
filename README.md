# art-vinsguru-redis-webflux
Redis with Spring WebFlux - Tutorial from Vinoth Selvaraj (Udemy)

####  Section 3: Redis - Crash Course

#####  6. Redis Setup Using Docker

1.  Save this docker-compose.yml
2.  Run this command: `docker-compose up`
3.  Launch a separate terminal to access redis-cli
    -  `docker exec -it redis bash`
4.  Start Redis CLI
    -  type `redis-cli`
5.  Ping 
    -  `ping` -> Response `PONG`    

#####  8. Storing Simple Key Values

Commands:
-  `set a b`
    -  a - key
    -  b - value
-  `get a`
    -  `"b"`
-  `get c`
    -  `(nil)`
-  `set a 1`
-  `get a`
    -  `"1"` - String
-  `set ....................bigkey............... somevalue`
-  `set 1 10` (1 is a key)   
-  `set true false`             
Naming best practices:
-  API `/user/1/name` -> key `user:1:name`   
-  Spaces in values
    -  `set somekey some value`
        -  `(error) ERR syntax error`
    -  `set somekey "some value"`
-  Spaces in keys
    -  `set "some key" somevalue`    


