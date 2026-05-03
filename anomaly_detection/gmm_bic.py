import numpy as np
from sklearn.mixture import GaussianMixture

def detectar_outliers_gmm_auto(dados, max_components=2, contaminacao=0.2):
    # Garantir que os dados estejam no formato correto (n_samples, n_features)
    X = np.array(dados).reshape(-1, 1)

    # --- 1. Determinar o número ideal de componentes via BIC ---
    bics = []
    modelos = []
    n_range = range(1, max_components + 1)

    for n in n_range:
        gmm = GaussianMixture(n_components=n, random_state=42).fit(X)
        bics.append(gmm.bic(X))
        modelos.append(gmm)

    n_ideal = n_range[np.argmin(bics)]
    melhor_modelo = modelos[np.argmin(bics)]

    print(f"Número ideal de componentes (BIC): {n_ideal}")

    # --- 2. Aplicação do Teste de Densidade para Outliers ---
    # O score_samples calcula o log da densidade de probabilidade
    scores = melhor_modelo.score_samples(X)

    # Definimos o limiar baseado na porcentagem de 'contaminação' esperada
    # (Similar a definir um nível de significância alpha)
    limiar = np.percentile(scores, contaminacao * 100)

    indices_outliers = np.where(scores < limiar)[0]
    valores_outliers = X[indices_outliers].flatten()

    return valores_outliers, n_ideal

# --- Exemplo de uso ---
meus_dados = [10, 11, 12, 11, 10, 12, 50, 51, 52, 50, 100] # Dois grupos e um outlier (100)

outliers, grupos = detectar_outliers_gmm_auto(meus_dados)

print(f"Outliers detectados: {outliers}")
print(f"Número de grupos identificados: {grupos}")
