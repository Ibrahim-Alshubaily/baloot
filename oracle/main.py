import yaml
import numpy as np
from oracle.consumer import stream_evaluations
from oracle.train import Trainer


def main():
    with open("oracle/config.yaml") as f:
        config = yaml.safe_load(f)

    print("📥 Streaming from Kafka...")

    trainer = Trainer(config["training"])
    topic = config["kafka"]["topic"]
    servers = config["kafka"]["bootstrap_servers"]
    batch_size = config["training"]["batch_size"]

    features, scores = [], []

    for i, encoded in enumerate(stream_evaluations(topic, servers), 1):
        features.append(encoded[:-1])
        scores.append(encoded[-1])

        if i % batch_size == 0:
            trainer.train_batch(np.array(features), np.array(scores))
            features.clear()
            scores.clear()


if __name__ == "__main__":
    main()
