package com.ping.syncmongo.common.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Data
public class MultiMongoProperties {

    private MongoProperties local;
    private MongoProperties remote;
}
