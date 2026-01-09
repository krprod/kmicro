package com.kmicro.order.config;


/*

@Configuration
public class PersistenceConfig {
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, Flyway flyway, EntityManagerFactoryBuilder builder) {
        // By including Flyway in the parameters, Spring ensures Flyway
        // finishes its migrations before Hibernate starts validation.
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        return em;
        return builder
                .dataSource(dataSource)
                // IMPORTANT: Change this to your actual entity package
                .packages("com.kmicro.order.entities.OutBoxEntity")
                .persistenceUnit("orderUnit")
                .build();
    }
}
*/
