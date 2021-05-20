package com.example.poc.kafka.sleuth

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.web.reactive.function.client.WebClient
import java.util.*


@EnableKafka
@SpringBootApplication
class KafkaIqPocApplication {

	@Bean
	fun webClient() = WebClient.builder()
			.build()


}

fun main(args: Array<String>) {
	runApplication<KafkaIqPocApplication>(*args)
}

