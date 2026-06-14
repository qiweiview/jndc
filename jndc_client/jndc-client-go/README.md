# jndc-client-go

Go implementation of `jndc_client`, intended to interoperate with the existing Java `jndc_server`.

## Build

```bash
cd /Users/liuqiwei/IdeaProjects/jndc/jndc_client/jndc-client-go
go build ./cmd/jndc-client-go
```

## Run

The client reuses the existing runtime layout:

- `~/.jndc/client/conf/config.yml`
- `~/.jndc/client/conf/client_id`
- `~/.jndc/client/conf/client_auth_key`

Run with the default config path:

```bash
./jndc-client-go
```

Or pass an explicit YAML file path:

```bash
./jndc-client-go /path/to/config.yml
```
