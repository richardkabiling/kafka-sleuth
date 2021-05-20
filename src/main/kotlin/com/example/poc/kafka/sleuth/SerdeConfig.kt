package com.example.poc.kafka.sleuth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerde

@Configuration
class SerdeConfig {

    @Bean
    fun resourceSerde(): JsonSerde<Resource> {
        return JsonSerde<Resource>().apply {
            val props = mutableMapOf(
                JsonDeserializer.TRUSTED_PACKAGES to "*",
                JsonDeserializer.USE_TYPE_INFO_HEADERS to "false",
                JsonDeserializer.VALUE_DEFAULT_TYPE to Resource::class.java
            )
            configure(props, false)
        }
    }

}