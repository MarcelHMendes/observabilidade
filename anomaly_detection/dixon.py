import numpy as np

def teste_q_dixon(dados, alpha=0.05):
    """
    Executa o Teste Q de Dixon para detectar um único outlier.
    Recomendado para amostras pequenas (3 <= n <= 10).
    """
    n = len(dados)
    # Tabela Q Crítica para Alpha=0.05 (Confiança 95%)
    tabela_q = {
        3: 0.941, 4: 0.765, 5: 0.642, 6: 0.560,
        7: 0.507, 8: 0.468, 9: 0.437, 10: 0.412
    }

    if n < 3 or n > 10:
        return "O Teste de Dixon é ideal para amostras entre 3 e 10 elementos."

    # 1. Ordenar os dados
    ordenados = np.sort(dados)
    amplitude = ordenados[-1] - ordenados[0]

    if amplitude == 0:
        return "Sem variação nos dados."

    # 2. Calcular o Q para os dois extremos
    q_menor = (ordenados[1] - ordenados[0]) / amplitude
    q_maior = (ordenados[-1] - ordenados[-2]) / amplitude

    # 3. Identificar o candidato a outlier
    q_calc = max(q_menor, q_maior)
    q_critico = tabela_q[n]
    suspeito = ordenados[0] if q_menor > q_maior else ordenados[-1]

    # 4. Resultado
    rejeitar = q_calc > q_critico

    return {
        "N": n,
        "Q_Calculado": round(q_calc, 3),
        "Q_Critico": q_critico,
        "Suspeito": suspeito,
        "É outlier?": "Sim" if rejeitar else "Não"
    }

# Exemplo prático
meus_dados = [10.2, 10.1, 10.3, 10.2, 12.5]
resultado = teste_q_dixon(meus_dados)

print(f"Resultado do Teste de Dixon: {resultado}")
