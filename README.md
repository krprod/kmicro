# Microservice Ecommerce Project
* run any service by cmd: **mvn spring-boot:run**

# Tools
__Docker__
- Requires Docker compose version 2 or greater `docker compose up`

__Databases__
- Postgress
    - Beekeeper UI tool: [Beekeeper Studio](https://www.beekeeperstudio.io/)
- Redis
  - redis UI tool: [Another Redis Desktop Manager](https://goanother.com/)

__Kafka__
- Kafka UI tool: [Kafka Manager](https://kafka-manager.io/)

__LocalStack__
- LocalStack UI tool: [Localstack](https://localstack.cloud/)

# Services
__Product Service__
URL: http://localhost:8080/api/products

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