package com.alshubaily.chess.server.data.loader

import com.alshubaily.chess.server.data.constants.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.luben.zstd.ZstdInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.sql.DriverManager

object LichessEvalImporter {


    @JvmStatic
    fun main(args: Array<String>) {
        streamAndInsert()
    }

    fun streamAndInsert(batchSize: Int = 1_000) {
        println("Loading $REMOTE_URL into $JDBC_URL...")

        DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS).use { conn ->
            conn.autoCommit = false
            conn.prepareStatement(
                "INSERT INTO $TABLE_NAME (fen, score) VALUES (?, ?) ON CONFLICT (fen) DO NOTHING"
            ).use { stmt ->

                val mapper = ObjectMapper()
                val remoteStream = URI(REMOTE_URL).toURL().openStream()
                val zis = ZstdInputStream(remoteStream)
                val reader = BufferedReader(InputStreamReader(zis))

                var rowCount = 0
                reader.useLines { lines ->
                    for (line in lines) {
                        val node = mapper.readTree(line)
                        val fen = node["fen"].asText()

                        val bestEval = node["evals"].maxByOrNull { it["depth"].asInt() }
                        val pvs = bestEval?.get("pvs") ?: continue
                        if (!pvs.elements().hasNext()) continue

                        val bestPv = pvs[0]

                        val score = when {
                            bestPv.has("cp") -> bestPv["cp"].asInt()
                            bestPv.has("mate") -> if (bestPv["mate"].asInt() > 0) 100000 else -100000
                            else -> continue
                        }

                        stmt.setString(1, fen)
                        stmt.setInt(2, score)
                        stmt.addBatch()

                        if (++rowCount % batchSize == 0) {
                            stmt.executeBatch()
                            conn.commit()
                            println("Processed %,d lines...".format(rowCount))
                        }
                    }
                }

                stmt.executeBatch()
                stmt.close()
                conn.commit()

                conn.createStatement().use { stmt ->
                    val countRs = stmt.executeQuery("SELECT COUNT(*) FROM $TABLE_NAME")
                    if (countRs.next()) {
                        val total = countRs.getInt(1)
                        println("📊 Total rows in lichess_evaluations: $total")
                    }

                    val sampleRs = stmt.executeQuery("SELECT fen, score FROM $TABLE_NAME ORDER BY RANDOM() LIMIT 1")
                    if (sampleRs.next()) {
                        val sampleFen = sampleRs.getString("fen")
                        val sampleScore = sampleRs.getInt("score")
                        println("🔍 Sample → FEN: \"$sampleFen\", Score: $sampleScore")
                    }
                }

            }
        }
        println("✅ Completed...")
    }
}
