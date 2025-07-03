#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Feb 10 17:45:23 2025

@author: giacomomagistrato
"""
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker

def read_data(file_path):
    x_values = []
    y_values = []

    with open(file_path, 'r') as file:
        for line in file:
            parts = line.split()
            if len(parts) == 3:  # Assumiamo che ogni riga abbia 3 valori
                y_values.append(float(parts[0]))  # Prima colonna -> Asse Y
                x_values.append(int(parts[1]))   # Seconda colonna -> Asse X

    return x_values, y_values

def plot_data(x_values, y_values, filename):
    plt.figure(figsize=(8, 5))
    plt.plot(x_values, y_values, marker='o', linestyle='-', color='b', label='Rejection rate')
    plt.xlabel('Pool size value')
    plt.ylabel('Rejection Rate')
    plt.title('Rejection Rate Pool dimensioning - '+ filename)
    plt.gca().xaxis.set_major_formatter(ticker.FormatStrFormatter('%.0f'))  # 2 decimali per l'asse x
    plt.gca().yaxis.set_major_formatter(ticker.FormatStrFormatter('%.2f'))  # 2 decimali per l'asse y

    # Imposta i limiti degli assi
    plt.xlim(min(x_values), 15)  # Imposta i limiti minimi e massimi per l'asse x
    plt.ylim(0, max(y_values))  # Imposta i limiti per l'asse y

    #plt.gca().set_yticks(sorted(set(y_values)))

    plt.gca().xaxis.set_major_locator(ticker.MultipleLocator(1))  # Passo di 0.5 per l'asse x
    plt.gca().yaxis.set_major_locator(ticker.MultipleLocator(0.5))  # Pa
    plt.legend()
    plt.grid()
    plt.savefig('plots/RejectionAnalysis/' + filename + '.png')
    plt.show()

# Esegui lo script
filename = '3P1 1P2 5P3 8Pool'
file_path = 'RejectionRatesAnalysis/PoolCopies/RejectionPoolDimentioning/' + filename + '.txt'
x, y = read_data(file_path)
plot_data(x, y, filename)
