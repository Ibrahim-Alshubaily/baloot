package com.alshubaily.chess.server.data.publisher

import com.alshubaily.chess.server.data.constants.*
import com.alshubaily.chess.server.util.FenEncoder
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import java.sql.DriverManager
import java.util.Properties

object ChessEvaluationsPublisher {

    @JvmStatic
    fun main(args: Array<String>) {
        truncateKafkaTopic()

        val producer = createKafkaProducer()
        val conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)
        val stmt = conn.prepareStatement("SELECT fen, score FROM $TABLE_NAME")
        val rs = stmt.executeQuery()

        var count = 0
        while (rs.next()) {
            val fen = rs.getString("fen")
            val score = rs.getInt("score")
            val boardBytes = FenEncoder.encodeBoardFeatures(fen)
            val scoreBytes = encodeScore(score)
            val record = ProducerRecord(KAFKA_TOPIC, boardBytes, scoreBytes)
            producer.send(record)

            if (++count % 10000 == 0) println("🔍 Published $count samples...")
        }

        println("✅Published $count samples to Kafka.")
        producer.flush()
        producer.close()
        stmt.close()
        conn.close()
    }

    fun createKafkaProducer(): KafkaProducer<ByteArray, ByteArray> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = KAFKA_BOOTSTRAP
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 32 * 1024
        props[ProducerConfig.LINGER_MS_CONFIG] = 20
        props[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "zstd"
        return KafkaProducer(props)
    }

    fun encodeScore(score: Int): ByteArray {
        val clamped = score.coerceIn(-1000, 1000)
        return byteArrayOf((clamped shr 8).toByte(), (clamped and 0xFF).toByte())
    }

    fun truncateKafkaTopic() {
        val props = Properties()
        props["bootstrap.servers"] = KAFKA_BOOTSTRAP
        AdminClient.create(props).use { admin ->
            admin.deleteTopics(listOf(KAFKA_TOPIC)).all().get()
            admin.createTopics(listOf(NewTopic(KAFKA_TOPIC, 1, 1))).all().get()
            println("🗑️  Kafka topic '$KAFKA_TOPIC' truncated and recreated.")
        }
    }
}
