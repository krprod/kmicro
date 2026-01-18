package com.kmicro.product.config;

/*@Configuration
public class MultiTenantFlywayConfig {
    @Bean
    public Boolean migrateCustomSchemas(DataSource dataSource) {
        List<String> tenants = List.of("client_a", "client_b", "client_c");

        for (String tenant : tenants) {
            Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(tenant) // Each tenant gets its own history table in its own schema
                    .locations("db/migration/tenants")
                    .load()
                    .migrate();
        }
        return true;
    }
}*/
