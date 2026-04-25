How to run
Local (safe if you already run local DB/Redis on default ports):
    docker compose --env-file .env.local up -d

EC2: 
    docker compose --env-file .env.ec2 up -d

Fresh database behavior
This stack uses Docker volume postgres_data, so it is isolated from your host DB.

For a fresh DB reset anytime:
docker compose --env-file .env.local down -v
then docker compose --env-file .env.local up -d


loginDB: psql -h localhost -p 55432 -U bloguser -d springmicro
list all DB: \list
switch DB: \c dbname
List of schemas: \dn
update user_schema.users set  roles='{ROLE_USER,ROLE_ORDERS,ROLE_ADMIN}' where id=3;

mvn compile jib:dockerBuild

docker compose --env-file .env.local restart user-service && sleep 5 && docker compose --env-file .env.local logs user-service --tail=120

docker compose up -d --force-recreate
docker compose --env-file .env.local up -d --force-recreate user-service 
docker compose --env-file .env.local up -d --force-recreate user-service order-service  product-service  notification-service apiGateway-service

docker compose --env-file .env.local up -d --build --force-recreate frontend-service
docker exec springmicro-frontend-1 grep -r "api.bloodshot.in" /usr/share/nginx/html/assets/

docker exec -it springmicro-frontend-1 ping apiGateway-service
docker exec -it springmicro-frontend-1 wget -qO- http://apiGateway-service:9096/api/products/paginated?page=1&size=50

docker exec -it springmicro-frontend-1 sh
grep -r "https://api.bloodshot.in" /usr/share/nginx/html/assets/

//------------------------------ AlloyLocalConfig
#discovery.docker "flog_scrape" {
#	host             = "unix:///var/run/docker.sock"
#	refresh_interval = "5s"
#}
#
#discovery.relabel "flog_scrape" {
#	targets = []
#
#	rule {
#		source_labels = ["__meta_docker_container_name"]
#		regex         = "/(.*)"
#		target_label  = "container"
#	}
#}
#
#loki.source.docker "flog_scrape" {
#	host             = "unix:///var/run/docker.sock"
#	targets          = discovery.docker.flog_scrape.targets
#	forward_to       = [loki.write.default.receiver]
#	relabel_rules    = discovery.relabel.flog_scrape.rules
#	refresh_interval = "5s"
#}

loki.source.faro "frontend" {
forward_to = [loki.write.default.receiver]
}

otelcol.receiver.otlp "faro_traces" {
http {}
output {
traces = [otelcol.exporter.otlp.tempo.input]
}
}

otelcol.exporter.otlp "tempo" {
client {
endpoint = "http://tempo:4318"
tls {
insecure = true
}
}
}

loki.write "default" {
endpoint {
url       = "http://gateway:3100/loki/api/v1/push"
tenant_id = "tenant1"
}
external_labels = {}
}
