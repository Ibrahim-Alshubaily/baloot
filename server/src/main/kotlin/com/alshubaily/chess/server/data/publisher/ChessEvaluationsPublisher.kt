package com.alshubaily.chess.server.data.publisher

import com.alshubaily.chess.server.data.constants.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.sql.DriverManager
import java.util.Properties

object ChessEvaluationsPublisher {



    @JvmStatic
    fun main(args: Array<String>) {
        val producer = createKafkaProducer()
        val conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)
        val stmt = conn.prepareStatement("SELECT fen, score FROM $TABLE_NAME")
        val rs = stmt.executeQuery()

        var count = 0
        while (rs.next()) {
            val fen = rs.getString("fen")
            val score = rs.getInt("score")
            val sampleJson = convertToSampleJson(fen, score)
            val record = ProducerRecord(KAFKA_TOPIC, fen, sampleJson)
            producer.send(record)

            if (++count % 10000 == 0) println("🔍 Published $count samples...")
        }

        println("🔍 Published $count samples to Kafka.")
        producer.flush()
        producer.close()
        stmt.close()
        conn.close()
    }

    fun createKafkaProducer(): KafkaProducer<String, String> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = KAFKA_BOOTSTRAP
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 32 * 1024
        props[ProducerConfig.LINGER_MS_CONFIG] = 20
        props[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "lz4"
        return KafkaProducer(props)
    }

    fun convertToSampleJson(fen: String, score: Int): String {
        val features = encodeFenToFeatureVector(fen)
        val normalizedScore = score.coerceIn(-1000, 1000) / 1000.0
        val featuresStr = features.joinToString(prefix = "[", postfix = "]")
        return "{\"features\":$featuresStr,\"label\":$normalizedScore}"
    }

    fun encodeFenToFeatureVector(fen: String): List<Int> {
        val parts = fen.split(" ")
        val piecePlacement = parts[0]
        val activeColor = parts[1]
        val castling = parts[2]
        val enPassant = parts[3]

        val boardPlanes = Array(12) { Array(8) { IntArray(8) } }
        val pieceToPlane = mapOf(
            'P' to 0, 'N' to 1, 'B' to 2, 'R' to 3, 'Q' to 4, 'K' to 5,
            'p' to 6, 'n' to 7, 'b' to 8, 'r' to 9, 'q' to 10, 'k' to 11
        )

        piecePlacement.split("/").forEachIndexed { rank, row ->
            var file = 0
            for (char in row) {
                if (char.isDigit()) {
                    file += char.digitToInt()
                } else {
                    pieceToPlane[char]?.let { plane ->
                        boardPlanes[plane][rank][file] = 1
                    }
                    file++
                }
            }
        }

        val features = mutableListOf<Int>()
        for (plane in boardPlanes) {
            for (row in plane) {
                for (square in row) {
                    features.add(square)
                }
            }
        }

        // Side to move
        features.add(if (activeColor == "w") 1 else 0)

        // Castling rights
        features.add(if ('K' in castling) 1 else 0)
        features.add(if ('Q' in castling) 1 else 0)
        features.add(if ('k' in castling) 1 else 0)
        features.add(if ('q' in castling) 1 else 0)

        // En passant
        val enPassantFile = if (enPassant != "-") enPassant[0] - 'a' else -1
        for (i in 0..7) {
            features.add(if (i == enPassantFile) 1 else 0)
        }

        return features
    }
}
