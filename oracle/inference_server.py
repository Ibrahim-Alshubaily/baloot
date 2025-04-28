from flask import Flask, request, jsonify
import yaml
import numpy as np
import torch
from oracle.train import Trainer

app = Flask(__name__)

with open("oracle/config.yaml") as f:
    config = yaml.safe_load(f)

trainer = Trainer(config["training"])

checkpoint_path = f"{config['training'].get('checkpoint_dir')}/latest_model.pth"
checkpoint = torch.load(checkpoint_path, map_location=torch.device('cpu'))
trainer.model.load_state_dict(checkpoint["model_state_dict"])
trainer.model.eval()

print(f"✅ Loaded model from {checkpoint_path}")


@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json()
    features = np.array(data["features"])
    predictions = trainer.predict(features)
    values = predictions.flatten().tolist()
    return jsonify(values)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5005)
