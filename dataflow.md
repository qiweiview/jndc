## Data Flow Case

### 1. client start scenario
```
jndc-client ---> jndc-server
* connect to jndc server
* timeout or connect success

jndc-server(inner)
* [server active event]create session context and store it in the channel attribute

jndc-client <--- jndc-server
* [server active event]send server timestamp message to jndc client

jndc-client ---> jndc-server
* [data read event]send client timestamp message to jndc server

jndc-server(inner)
* [server data read event]calculate the time difference between the client and the server

jndc-client <--- jndc-server
* send channel ready message to jndc client
```

### 2. client register service scenario
```
jndc-client ---> jndc-server
* send service register message to jndc server

jndc-server(inner)
* [server data read event]judge whether use client expect service port
* create tcp server and bind service port
* bind tcp server stop method to channel inactive event

jndc-client <--- jndc-server
* [data read event]send service service port bound message to jndc client
```

### 3. client unregister service scenario
```
jndc-client ---> jndc-server
* send service unregister message to jndc server

jndc-clien(innner)
* [server data read event]find tcp server by service id
```

### 3. client stop scenario
```
jndc-server(innner) 
* [server inactive event]

