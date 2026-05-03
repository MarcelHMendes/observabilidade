import pandas as pd

dados = [10, 12, 11, 13, 10, 11, 12, 25, 48, 52]
s = pd.Series(dados)

# Calcula os quartis
Q1 = s.quantile(0.25)
Q3 = s.quantile(0.75)
IQR = Q3 - Q1

# Define os limites (o "bigode" do boxplot)
limite_inferior = Q1 - 1.5 * IQR
limite_superior = Q3 + 1.5 * IQR

# Filtra os outliers
outliers = s[(s < limite_inferior) | (s > limite_superior)]

print(f"IQR: {IQR}")
print(f"Limites: {limite_inferior} a {limite_superior}")
print(f"Outliers detectados: {outliers.tolist()}")
