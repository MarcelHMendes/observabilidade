import scikit_posthocs as sp
import numpy as np

dados = np.array([10, 12, 11, 13, 10, 11, 12, 25, 48, 52])

# outliers_gesd(dados, k_outliers, alpha)
# O parâmetro 'hypo=True' retorna um array booleano indicando quem é outlier
outliers = sp.outliers_gesd(dados, outliers=3, hypo=True)

print(f"Indices dos outliers: {np.where(outliers)[0]}")
print(f"Valores detectados: {dados[outliers]}")
