import torch
import numpy as np
from oracle.model import ChessNet
import os


class Trainer:
    def __init__(self, config):
        self.model = ChessNet()
        self.model.train()
        self.optimizer = torch.optim.Adam(
            self.model.parameters(), lr=3e-4)
        self.loss_fn = torch.nn.MSELoss()
        self.counter = 0
        self.checkpoint_dir = config.get(
            "checkpoint_dir", "oracle/checkpoints")
        os.makedirs(self.checkpoint_dir, exist_ok=True)

    def train_batch(self, X: np.ndarray, y: np.ndarray):
        self.counter += 1
        X_tensor = torch.tensor(X, dtype=torch.float32)
        y_tensor = torch.tensor(y, dtype=torch.float32).unsqueeze(1)

        self.optimizer.zero_grad()
        predictions = self.model(X_tensor)
        loss = self.loss_fn(predictions, y_tensor)
        loss.backward()
        self.optimizer.step()

        print(f"✅ Batch #{self.counter} — loss: {loss.item():.4f}")

        if self.counter % 1000 == 0:  # Save every 10 batches
            self.save_checkpoint()

    def save_checkpoint(self):
        path = os.path.join(self.checkpoint_dir, "latest_model.pth")
        torch.save({
            "model_state_dict": self.model.state_dict(),
            "optimizer_state_dict": self.optimizer.state_dict(),
            "counter": self.counter
        }, path)
        print(f"💾 Saved checkpoint at batch #{self.counter} ➔ {path}")

    def predict(self, X: np.ndarray) -> np.ndarray:
        self.model.eval()
        with torch.no_grad():
            X_tensor = torch.tensor(X, dtype=torch.float32)
            predictions = self.model(X_tensor)
        return predictions.numpy()
