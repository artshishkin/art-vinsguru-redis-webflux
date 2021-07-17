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

#####  14. Exists Command

-  `exists a` -> 1    
-  `exists b` -> 0
-  `set user:1:token token ex 10`
-  `exists user:1:token`
    -  1 (yes)
    -  0 (no)    

#####  15. INCR / DECR Commands

1.  Increment integer
    -  `set a 1`
    -  `incr a` -> ++a
        -  (integer) 2
    -  `get a` -> "2"
    -  `incr non-existing-key` ->
        -  `(integer) 1`
2.  Decrement integer        
    -  `decr a` -> --a
3.  Increment float
    -  `set a 1.02`
    -  `incrbyfloat a .3` -> a+=0.3
    -  "1.32"
4.  Decrement float           
    -  `incrbyfloat a -.3` -> a-=0.3
5.  Increase by certain value
    -  `incrby a 123`
6.  Decrease by certain value
    -  `decrby a 123`    

#####  16. Redis Commands Cheat Sheet Download

[Redis Commands Cheat Sheet](https://cheatography.com/tasjaevan/cheat-sheets/redis/)

#####  19/20. Hash

1.  Set hash
    -  `hset key field value`
    -  `hset user:1 name Art age 38 city Kramatorsk`
2.  Type of Value by Key
    -  `type user:1`
        -  `hash`
3.  Get field value
    -  `hget user:1 name`    
4.  Get all fields
    -  `hgetall user:1`
        -  `1) "name"`
        -  `2) "Art"`
        -  `3) "age"`
        -  `4) "38"`
        -  `5) "city"`
        -  `6) "Kramatorsk"`
5.  Create another object with different fields
    -  `hset user:2 name Kate birthYear 1983 status active`
6.  Expire object
    -  `expire user:2 10`
7.  Get all keys of hash
    -  `hkeys user:1`
        -  `1) "name"`
        -  `2) "age"`
        -  `3) "city"`    
8.  Get all values of hash
    -  `hvals user:1`
9.  Does field exist
    -  `hexists user:1 status`
10.  Delete field
    -  `hdel user:1 age`
11.  Delete entire object
    -  `del user:1`

#####  21. List & Queue

1.  Right-hand side push
    -  Insert all the specified values at the tail of the list stored at key 
    -  `rpush a 1`
    -  `rpush a 2 3 4 5`
2.  Get list length
    -  `llen a`
3.  Get elements from list
    -  `lrange a 0 3` - from index 0 to index 3 (4 total)
        -  `1) "1"`
        -  `2) "2"`
        -  `3) "3"`
        -  `4) "4"`
    -  `lrange a 0 -1` - from index 0 to the end
    -  `lrange a 0 1000` - from index 0 to 1000 but only existing
4.  Remove from list from head
    -  `lpop a` - from left remove 1 and return it
    -  `lpop a 3` - from left remove 3 and return them              

#####  23. List As Stack

1.  Push items
    -  `rpush a 1 2 3 4 5 6`
2.  Remove from list from tail
    -  `rpop a` - from right remove 1 and return it
    -  `rpop a 3` - from right remove 3 and return them
        -  `1) "5"`
        -  `2) "4"`
        -  `3) "3"`
3.  **OR** from left-hand side
    -  `lpush a 1 2 3 4`
    -  `lpop a 3`
4.  Empty list
    -  `lpop a 1000`
    -  `keys *` - (empty array) - redis deletes keys of empty list                
        
#####  24. Redis Set

1.  Add items to set
    -  `sadd users 1 2 3`
2.  Get size of the set
    -  `scard users`
3.  Get members
    -  `smembers users`
4.  Check member is present
    -  `sismember users 3`    
5.  Remove item from set
    -  `srem users 5` - only member 5
    -  `srem users 2 3` - 2 and 3
6.  Randomly pop member (and remove it from the set)
    -  `spop users` - 1 member
    -  `spop users 3` - 3 members
         

    