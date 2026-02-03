# Microservice Ecommerce Project
* run any service by cmd: **mvn spring-boot:run**

# Services
__Product Service__
- URL: http://localhost:8080/
  - Product Serivce Endpoint: /api/products
  - Category Serivce Endpoint: /api/category
- DOC: http://localhost:8080/swagger-ui/index.html | http://localhost:8080/v3/api-docs

__Order Service__
- URL: http://localhost:8091/ 
  - Cart Service Endpoint: /api/carts
  - Order Service Endpoint: /api/orders
- DOC: http://localhost:8091/swagger-ui/index.html | http://localhost:8091/v3/api-docs
 
__User Service__
- URL: http://localhost:8085/api/
  - Auth Service Endpoint: /api/auth
  - Users Service Endpoint: /api/users
- DOC: http://localhost:8085/swagger-ui/index.html | http://localhost:8085/v3/api-docs

__Payment Service__
- URL: http://localhost:8095/api/payment
- DOC: http://localhost:8095/swagger-ui/index.html | http://localhost:8095/v3/api-docs

__Notification Service__
- URL: http://localhost:8096/api/notifications
- DOC: http://localhost:8096/swagger-ui/index.html | http://localhost:8096/v3/api-docs 
- Kafka Doc: http://localhost:8096/springwolf/asyncapi-ui.html#info | http://localhost:8096/springwolf/docs


[//]: # (__Cart Service__)

[//]: # (- Cart service is a microservice that handles the cart operations. URL: http://localhost:8090/api/cart)
__Commons__

# Tools
__Docker__
- Requires Docker compose version 2 or greater `docker compose up`

__Databases__
- Postgress
    - Beekeeper UI tool: [Beekeeper Studio](https://www.beekeeperstudio.io/)
    - Few Useful Commands:
        - psql -U <user> -d <db>	 --Connect to a specific database as a user.
        - psql -h <host> -p <port> -U <user> -d <db>	--Connect to a remote host.
        - psql -U postgres	--Connect as superuser.
        - \c <database>	--Switch connection to a new database.
        - \conninfo	--Show current connection details.
        - \?	Show help for psql commands.
        - \q	--Quit psql.
        - \l / \l+	--List all databases.
        - \dt	--List all tables in current database.
        - \d <table_name>	Describe table structure (columns, types, constraints).
        - \d+ <table_name>	Detailed table info, including storage size.
        - \du / \dg	List users/roles.
        - \x	Toggle expanded display mode (vertical output).
        - \o <file>	Send query results to a file.
        - \copy ...	Copy data between table and CSV file.
        - \timing	Toggle query execution time display.
        - \i <file>	Execute SQL commands from a file.
- Redis
    - redis UI tool: [Another Redis Desktop Manager](https://goanother.com/)

__Kafka__
- Kafka UI tool: [Kafka Manager](https://kafka-manager.io/)

__LocalStack__
- LocalStack UI tool: [Localstack](https://localstack.cloud/)
