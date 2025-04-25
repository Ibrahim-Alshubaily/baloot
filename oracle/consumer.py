from kafka import KafkaConsumer
import numpy as np
import struct


def decode_score(score_bytes):
    return struct.unpack(">h", score_bytes)[0] / 32767.0


def unpack_board_features(bitpacked):
    bits = np.unpackbits(np.frombuffer(bitpacked, dtype=np.uint8))[:781]
    return bits.astype(np.float32)


def stream_evaluations(topic, bootstrap_servers):
    consumer = KafkaConsumer(
        topic,
        bootstrap_servers=bootstrap_servers,
        key_deserializer=lambda k: k,
        value_deserializer=lambda v: v,
        auto_offset_reset='earliest',
        enable_auto_commit=True,
        group_id='oracle-group'
    )

    for msg in consumer:
        board = unpack_board_features(msg.key)
        score = float(decode_score(msg.value))
        full = np.append(board, score)
        yield full
