# Cyrulik
Simple website for posting utility notes.

It uses session based authentication and was generated using Luminus version "3.54"

```
podman compose up

mysql -h 127.0.0.1 -P 3306 -D db -u cyrulik -p < migrations/init.sql
```
