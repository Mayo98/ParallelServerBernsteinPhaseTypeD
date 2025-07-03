#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Oct 15 17:45:25 2024

@author: giacomomagistrato
"""
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker

## POOL COPIES REWARDS GRAPH

def load_data_from_file(path):


    with open(path, 'r') as file:
        lines = file.readlines()
        labels = []
        # Leggi la prima riga per le etichette, se necessario
        labels = lines[0].strip().split()  # Puoi modificare questo se non hai bisogno di etichette
        c = len(labels)
        x_values = []
        a = 0
        y_values = [[] for _ in range(c-1)]  # 5 liste per le 5 curve

        # Leggi i dati delle righe successive
        for line in lines[1:]:  # Inizia dalla seconda riga
            values = list(map(float, line.strip().split()))
            if len(values) < 6:
                continue  # Salta righe che non hanno abbastanza valori

            x_values.append(round(values[0], 4))  # Primo valore come x

            for i in range(1,c):
                y_values[i-1].append(values[i])  # Aggiungi i valori y


    return x_values, y_values, labels

# Nome del file contenente i dati
#filename = 'TransientResults/PoolCopies/IncreasedRate/5P1 2P2 6P3 10Pool -- 3P1.txt'
#filename = '2P1 1P2 3P3 8Pool'
filename = '2P1 2P2 1P3 8Pool -- 3P1 1P2 5P3'

#path = 'TransientResults/PoolCopies/'+ filename+'.txt'

path = 'TransientResults/PoolCopies/IncreasedRate/' + filename+ '.txt'

# Carica i dati dal file
x_values, y_values, labels = load_data_from_file(path)

# Creazione del grafico
plt.figure(figsize=(15, 6))
for i in range(1,len(labels)):
    plt.plot(x_values, y_values[i-1], label=labels[i])  # Usa le etichette lette dal file

# Imposta il formato dei decimali sugli assi
plt.gca().xaxis.set_major_formatter(ticker.FormatStrFormatter('%.2f'))  # 2 decimali per l'asse x
plt.gca().yaxis.set_major_formatter(ticker.FormatStrFormatter('%.2f'))  # 2 decimali per l'asse y

# Imposta i limiti degli assi
plt.xlim(min(x_values), 4.50)  # Imposta i limiti minimi e massimi per l'asse x
plt.ylim(min(min(y) for y in y_values),0.40)  # Imposta i limiti per l'asse y max(max(y) for y in y_values)

plt.gca().xaxis.set_major_locator(ticker.MultipleLocator(0.5))  # Passo di 0.5 per l'asse x
plt.gca().yaxis.set_major_locator(ticker.MultipleLocator(0.02))  # Pa

plt.title('Transient Analysis PoolCopies Rewards')

plt.xlabel('Time')
plt.ylabel('Values')
plt.legend()
plt.grid(True)
plt.savefig('plots/TransientAnalysis/' + filename + 'Perf.png')
plt.show()