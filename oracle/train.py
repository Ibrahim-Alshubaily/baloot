import torch
import numpy as np
from oracle.model import ChessNet


class Trainer:
    def __init__(self, config):
        self.model = ChessNet()
        self.model.train()
        self.optimizer = torch.optim.Adam(
            self.model.parameters(), lr=3e-4)
        self.loss_fn = torch.nn.MSELoss()
        self.counter = 0

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
