package com.openclassrooms.starterjwt;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;

public class TestDataSourceConfig {
    @Bean
    public DataSource dataSource() {
        // Configuration de la source de données pour les tests
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2) // Utilisation d'une base de données embarquée (H2)
                .addScript("classpath:schema.sql") // Script SQL pour créer le schéma de base de données
                .build();
    }
}
