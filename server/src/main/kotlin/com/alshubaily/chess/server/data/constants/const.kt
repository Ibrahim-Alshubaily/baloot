package com.alshubaily.chess.server.data.constants

const val DATASET_PATH = "datasets/lichess_db_eval.jsonl.zst"
//const val REMOTE_URL = "https://database.lichess.org/lichess_db_eval.jsonl.zst"

const val JDBC_URL = "jdbc:postgresql://localhost:5432/chess_evaluations"
const val TABLE_NAME = "lichess_evaluations"
const val DB_USER = "chess_evaluations_user"
const val DB_PASS = "chess_evaluations_password"

const val KAFKA_TOPIC = "chess_evaluation_samples"
const val KAFKA_BOOTSTRAP = "localhost:9092"
