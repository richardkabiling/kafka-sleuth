package com.example.poc.kafka.sleuth

import kotlinx.coroutines.reactive.awaitFirst
import mu.KotlinLogging
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import java.lang.RuntimeException
import java.net.URI

private val logger = KotlinLogging.logger {  }

@RestController
class ResourcesController(private val kafkaTemplate: KafkaTemplate<String, Resource>,
                          private val webClient: WebClient,
                          private val queryService: InteractiveQueryService) {

    @PutMapping("/namespaces/{namespace}/resources/{resourceId}")
    suspend fun mapResource(@RequestBody request: MapResourceRequest,
                            @PathVariable("namespace") namespace: String,
                            @PathVariable("resourceId") resourceId: String): ResponseEntity<Resource> {
        logger.info { "Received request to map resource. namespace=$namespace, resourceId=$resourceId, request=$request" }

        val resource = Resource(namespace = namespace, id = resourceId, name = request.name)
        kafkaTemplate.send("resources", "${namespace}:${resourceId}", resource).get()

        return ResponseEntity.ok(resource)
    }

    @GetMapping("/namespaces/{namespace}/resources/{resourceId}")
    suspend fun getResource(@PathVariable("namespace") namespace: String,
                          @PathVariable("resourceId") resourceId: String): ResponseEntity<Resource> {
        logger.info { "Received request to get resource. namespace=$namespace, resourceId=$resourceId" }

        val key = "${namespace}:${resourceId}"
        val targetHostInfo = queryService.getHostInfo("resources-store", key, StringSerializer())

        logger.info { "currentHostInfo=${queryService.currentHostInfo}" }
        logger.info { "targetHostInfo=${targetHostInfo}" }

        return if(targetHostInfo == queryService.currentHostInfo) {
            logger.info { "Target host is local. targetHost=local" }
            val resourcesStore = queryService.getQueryableStore("resources-store",
                    QueryableStoreTypes.keyValueStore<String, Resource>())
            val resource = resourcesStore.get(key)
            logger.info { resource }

            when(resource) {
                null -> throw RuntimeException()
                else -> ResponseEntity.ok(resource)
            }
        } else {
            logger.info { "Target host is remote. targetHost=remote" }
            val resource = webClient.get()
                    .uri(URI.create("http://${targetHostInfo.host()}:${targetHostInfo.port()}/namespaces/${namespace}/resources/${resourceId}"))
                    .exchangeToMono { it.bodyToMono(Resource::class.java) }
                    .awaitFirst()

            ResponseEntity.ok(resource)
        }
    }

}

data class MapResourceRequest(val name: String)

data class Resource(val namespace: String, val id: String, val name: String)

