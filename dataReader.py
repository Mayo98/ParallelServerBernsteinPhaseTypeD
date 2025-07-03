#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Tue Oct  1 12:07:30 2024

@author: giacomomagistrato
"""

import matplotlib.pyplot as plt
import matplotlib.ticker as ticker

# Nome del file contenente i dati

#MODE = 1 AVAILABILITY ---- MODE = 2 PERFORMABILITY
mode = 2
filename = 'SteadyStateTests'
#filenamesSources = 'SteadyStateResults/Performability/ + filename +'.txt'
#filenamesSources = 'SteadyStateResults/TokensDistribution/P1 Tests/' + filename + '.txt'
#filenamesSources = 'SteadyStateResults/TokensDistribution/P2 Tests/' + filename + '.txt'
#filenamesSources = 'SteadyStateResults/TokensDistribution/P3 Tests/' + filename + '.txt'
#filenamesSources = 'SteadyStateResults/TokensDistribution/Pool Tests/' + filename + '.txt'


#filenamesSources = 'SteadyStateResults/PoolCopies/ + filename +'.txt'
#filenamesSources = 'SteadyStateResults/PoolCopies/P1 Tests/' + filename + '.txt'
#filenamesSources = 'SteadyStateResults/PoolCopies/P2 Tests/' + filename + '.txt'
#filenamesSources = 'SteadyStateResults/PoolCopies/P3 Tests/' + filename + '.txt'
filenamesSources = 'SteadyStateResults/PoolCopies/Pool Tests/' + filename + '.txt'

# Elaborazione dei dati



if mode == 1:

    data_dict = { 'Ph1': [], 'Ph2': [], 'Ph3': [], 'Ph4': [], 'Pool': [] }
    with open(filenamesSources, 'r') as file:
        lines = file.readlines()

        for line in lines:
            line2 = line.strip()
            if line2 != '':
                parts = line.split(':')

                key = parts[0].strip()
                value, x = map(float, parts[1].strip().split())
                data_dict[key].append((value, int(x)))

    # Creazione del grafico
    plt.figure(figsize=(12, 5))

    for key in data_dict:
        #if key != 'Pool':  # Escludi "Pool"
        values, xs = zip(*data_dict[key])
        plt.plot(xs, values , marker='', label=key)


    plt.gca().xaxis.set_major_formatter(ticker.FormatStrFormatter('%.2f'))  # 2 decimali per l'asse x
    plt.gca().yaxis.set_major_formatter(ticker.FormatStrFormatter('%.2f'))  # 2 decimali per l'asse y

    # Imposta i limiti degli assi
    plt.xlim(0, 11)  # Imposta i limiti minimi e massimi per l'asse x
    plt.ylim(0 ,4)  # Imposta i limiti per l'asse y max(max(y) for y in y_values)

    plt.gca().xaxis.set_major_locator(ticker.MultipleLocator(1))  # Passo di 0.5 per l'asse x
    plt.gca().yaxis.set_major_locator(ticker.MultipleLocator(0.2))  # Pa
    # Aggiunta di etichette e titolo
    plt.xlabel('Valori')
    plt.ylabel('Probabilità')
    plt.title('Grafico delle Curve - Variazione di Pool')
    plt.legend()
    plt.grid(True)

    #plt.tight_layout()
    plt.savefig('plots/SteadyState/' + filename +' -_AvailabilityPool' +  '.png')
    plt.show()




if mode ==2:
    data_dict = {}

    with open(filenamesSources, 'r') as file:
        lines = file.readlines()

        for line in lines:
            line2 = line.strip()
            if line2 != '':
                parts = line.split(':')

                # Recupera la chiave e i valori
                key = parts[0].strip()
                value, x = map(float, parts[1].strip().split())

                # Usa setdefault per aggiungere la chiave se non esiste
                data_dict.setdefault(key, []).append((value, int(x)))

    # Creazione del grafico
    plt.figure(figsize=(13, 5))

    for key in data_dict:
        #if key != 'Pool':  # Escludi "Pool"
        values, xs = zip(*data_dict[key])
        plt.plot(xs, values , marker='', label=key)

    # Imposta i limiti degli assi
    plt.xlim(0, 13)  # Imposta i limiti minimi e massimi per l'asse x
    plt.ylim(0 ,0.70)  # Imposta i limiti per l'asse y max(max(y) for y in y_values)

    plt.gca().xaxis.set_major_locator(ticker.MultipleLocator(1))  # Passo di 0.5 per l'asse x
    plt.gca().yaxis.set_major_locator(ticker.MultipleLocator(0.05))  # Pa
    # Aggiunta di etichette e titolo
    plt.xlabel('Valori')
    plt.ylabel('Probabilità')
    plt.title('Variazione di Pool')
    plt.legend()
    plt.grid(True)


    #plt.tight_layout()

    plt.savefig('plots/SteadyState/' + filename +' -_PerformabilityPool' + '.png')
    plt.show()