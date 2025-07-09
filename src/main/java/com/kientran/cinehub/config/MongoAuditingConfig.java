package com.kientran.cinehub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoAuditingConfig {
    // @EnableMongoAuditing tự động xử lý @CreatedDate và @LastModifiedDate trong base entity.
}
