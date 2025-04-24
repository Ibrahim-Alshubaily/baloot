package com.alshubaily.chess.server.data.publisher

import com.alshubaily.chess.server.data.constants.DATASET_PATH
import com.alshubaily.chess.server.data.constants.KAFKA_BOOTSTRAP
import com.alshubaily.chess.server.data.constants.KAFKA_TOPIC
import com.alshubaily.chess.server.util.EvalEncoder
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.luben.zstd.ZstdInputStream
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

object ChessEvaluationsPublisher {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Streaming from $DATASET_PATH to Kafka ($KAFKA_TOPIC)...")

        val producer = createKafkaProducer()
        val mapper = ObjectMapper()
        val fileStream = FileInputStream(File(DATASET_PATH))
        val zis = ZstdInputStream(fileStream)
        val reader = BufferedReader(InputStreamReader(zis))

        var count = 0
        reader.useLines { lines ->
            for (line in lines) {
                val node = mapper.readTree(line)
                val encoded = EvalEncoder.encodeFromJson(node) ?: continue
                val (boardBytes, scoreBytes) = encoded

                producer.send(ProducerRecord(KAFKA_TOPIC, boardBytes, scoreBytes))

                if (++count % 100_000 == 0) println("\uD83D\uDCC4 Sent $count samples...")
            }
        }

        producer.flush()
        producer.close()
        println("Done. Published $count samples.")
    }

    private fun createKafkaProducer(): KafkaProducer<ByteArray, ByteArray> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = KAFKA_BOOTSTRAP
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 64 * 1024
        props[ProducerConfig.LINGER_MS_CONFIG] = 10
        props[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "zstd"
        props[ProducerConfig.ACKS_CONFIG] = "1"
        return KafkaProducer(props)
    }
}
