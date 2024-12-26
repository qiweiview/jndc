## Data Flow Case

### 1. client start scenario
```
jndc-client ---> jndc-server
* connect to jndc server
* timeout or connect success

jndc-server(inner)
* [jndc server active event]create session context and store it in the ndc-channel attribute

jndc-client <--- jndc-server
* send ndc-channel ready message to jndc client
```

### 2. client register service scenario
```
* jndc-client(inner)
* check whether the ndc-channel is ready

jndc-client ---> jndc-server
* send service register message to jndc server

jndc-server(inner)
* [jndc server data read event]judge whether use client expect service port,create tcp server and bind service port,generate unique service id and set it to the tcp server
* bind tcp server stop method to ndc-channel inactive event
* set ndc-channel into tcp server

jndc-client <--- jndc-server[service id]
* [jndc client data read event]send service port bound message to jndc client

jndc-client(inner)
* store service id and update service relational information(bingding port,service id,service status)
```

### 3. client unregister service scenario
```
jndc-client ---> jndc-server[service id]
* send service unregister message to jndc server with service id
* set service status to stoping


jndc-server(inner)
* [jndc server data read event]find tcp server by service id
* stop tcp server and release connection resource

jndc-client <--- jndc-server[service id]
* notice client service port unbound

jndc-client(inner)
* [jndc client data read event]update service status to stoped
```

### 4. client stop scenario

```
jndc-server(inner) 
* [jndc server inactive event] find all service that ndc-channel binded and stop them
* record the stop time
```

### 5. server stop scenario

```
jndc-client(inner)
* [jndc client inactive event] set all service status to stoped
* find all tcp client and close them 
```

### 6. tcp server accept connection scenario
```
tcp-server(inner)
* [tcp server active event]send open client messageto ndc-channel with tcp-channel id

jndc-client <--- jndc-server[tcp server id/tcp channel id]
* [jndc client data read event]send open tcp client message to jndc client

jndc-client(inner)
* use tcp-channel id to start tcp client
```

### 7. tcp server read data scenario

```
tcp-server(inner)
* [tcp server data read event]generate tcp transfer message and send it to ndc-channel

jndc-client <--- jndc-server
* [jndc client data read event]find tcp client by tcp-channel id and send tcp data to tcp client
```

### 8. tcp-server-channel stop scenario

```
tcp-server ---> tcp-client[tcp server id/tcp channel id]
* [tcp-server-channel inactive event]send tcp-server-channel message with tcp-channel id t to ndc-channel

tcp-clinet(inner)

```

### 9. tcp client read data scenario

```
tcp-client(inner)
* [tcp client data read event]generate tcp transfer message and send it to ndc-channel

jndc-server ---> jndc-client[tcp server id/tcp channel id]
* [jndc server data read event]find tcp server and tcp channel by tcp server id and tcp-channel id,send tcp data to tcp-channel
```

### 10. tcp-client-channel stop scenario

```
tcp-client ---> tcp-server[tcp server id/tcp channel id]
* [tcp client inactive event]send close tcp client message to ndc-channel

tcp-server(inner)
* [jndc server data read event]find tcp-channel by tcp server id and tcp channel id,close tcp-channel 
```