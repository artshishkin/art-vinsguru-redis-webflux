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

#####  9. Accessing All Keys

1.  Keys (deprecated)
    -  `keys  *` - all the keys
    -  `keys user:*` - all the keys starting with user
    -  `keys user:*:name`
        -  1) "user:2:name"
        -  2) "user:1:name"
    -  `keys user:*:nan
        -  `(empty array)`
2.  Scan (better solution)
    -  `scan 0` - all keys from page 0
        -  it returns reference to the next page - `"6"`
    -  `scan 6`
        -  another portion of scan - `"27"`
    -  `scan 27`
        -  back to page `"0"`
    -  `scan 0 MATCH user:*:name COUNT 5` - match pattern, limit by count, page 0

#####  10. Removing Keys

1.  Delete one (or list)
    -  `del key [key ...]`
    -  `del user:8:name`
    -  `del user:1:name user:2:name`
2.  Delete All
    -  `flushdb`
    
#####  11. Expiring Keys

-  set expiration in seconds
    -  `set a b ex 10` - 10 seconds
    -  `get a` -> `b` -> wait for 10 sec
    -  `get a` -> `(nil)`
-  check TTL
    -  `set a b ex 10` - 10 seconds
    -  `ttl a`
-  extend TTL
    -  `expire a 60` - set new ttl to 60 seconds    
-  set expiration time at certain timestamp
    -  `set a b exat 1626465000`
    -  `ttl a` -> 269
-  set expiration time in millis
    -  `set a b px 30000`
-  change value but keep ttl
    -  wrong:
        -  `set a b ex 60`
        -  `set a c`
        -  `ttl a` -> -1 (no expiration - will keep forever)
    -  correct    
        -  `set a b ex 60`
        -  `set a c keepttl`                

#####  13. Set Options - XX/NX
    
-  NX - Only set the key if it does not already exist.
    -  `set a b nx`
        -  `(nil)` if exists
        -  `OK` if not exist
-  XX - Only set the key if it already exist.
    -  `set a c xx`
        -  `OK` if exists
        -  `(nil)` if not

    







    