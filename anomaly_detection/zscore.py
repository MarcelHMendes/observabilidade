import numpy as np
from scipy import stats

dados = np.array([10, 12, 11, 13, 10, 11, 12, 25, 48, 52])

# Calcula os Z-scores
z_scores = stats.zscore(dados)

print("Z-scores de cada ponto:")
print(z_scores)

# Identificando suspeitos (onde o valor absoluto de Z > 2)
suspeitos = dados[np.abs(z_scores) > 2]
print(f"\nSuspeitos (Z > 2): {suspeitos}")
