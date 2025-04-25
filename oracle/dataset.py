from torch.utils.data import Dataset
import torch


class ChessEvalDataset(Dataset):
    def __init__(self, features, scores):
        self.X = torch.tensor(features, dtype=torch.float32)
        self.y = torch.tensor(scores, dtype=torch.float32).unsqueeze(1)

    def __len__(self):
        return len(self.X)

    def __getitem__(self, idx):
        return self.X[idx], self.y[idx]
