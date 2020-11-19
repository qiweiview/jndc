ps -ef | grep jndcccccccccc_client | grep -v grep | cut -c 9-15 | xargs kill -s 9;
echo 'stop jnfc success'
