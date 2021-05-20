package com.example.poc.kafka.sleuth

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.HashMap

@Configuration
class ProducerConfig {

    @Bean
    fun producerFactory(): ProducerFactory<String, Resource> {
        return DefaultKafkaProducerFactory(producerConfigs(), keySerializer(), valueSerializer())
    }

    private fun valueSerializer(): () -> JsonSerializer<Resource> = { JsonSerializer<Resource>() }

    private fun keySerializer(): () -> StringSerializer = { StringSerializer() }

    @Bean
    fun producerConfigs(): MutableMap<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        props[JsonSerializer.ADD_TYPE_INFO_HEADERS] = "false"
        return props
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Resource> {
        return KafkaTemplate(producerFactory())
    }

}