package com.example.poc.kafka.sleuth

import mu.KotlinLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonSerde
import java.util.function.Consumer

private val logger = KotlinLogging.logger {  }

@Configuration
class ProcessingConfig {

    @Bean
    fun materializeResources(resourceSerde: JsonSerde<Resource>): Consumer<KStream<String, Resource>> {
        return Consumer {
            it.peek { key, value -> logger.info { "Received to materialize resource. $key=$value" } }
                .toTable(Materialized.`as`<String, Resource, KeyValueStore<Bytes, ByteArray>>("resources-store")
                    .withKeySerde(Serdes.StringSerde())
                    .withValueSerde(resourceSerde))
        }
    }

}