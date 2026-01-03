# Microservice Ecommerce Project
* run any service by cmd: **mvn spring-boot:run**

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

# Services
__Product Service__
- URL: http://localhost:8080/api/products
- DOC: http://localhost:8080/swagger-ui/index.html | http://localhost:8080/v3/api-docs

__Cart Service__
- Cart service is a microservice that handles the cart operations. URL: http://localhost:8090/api/cart

__Order Service__
- Order service is a microservice that handles the order operations. 
  - URLs: 
    - http://localhost:8091/api/checkout
    - http://localhost:8091/api/orders
    - 
__Payment Service__
- Payment service is a microservice that handles the payment operations. URL: http://localhost:8095/api/payment

__Notification Service__
URL: http://localhost:8096/api/notifications

__User Service__
URL: http://localhost:8085/api/

__Commons__