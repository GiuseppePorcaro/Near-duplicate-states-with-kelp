import pandas as pd
import numpy as np
import sqlite3
import csv

def main():
    print("Caricamento dataset...")
    csv = pd.read_csv('/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/dataset.csv', sep=",")

    datasetX = csv[csv.columns[0:9]].to_numpy()
    datasetY = csv[csv.columns[9]].to_numpy()

    print("Sample shape: ",datasetX.shape)
    print("Target shape: ",datasetY.shape)
    print("\n\n")
    print("Sample:\n",datasetX)
    print("Target:\n",datasetY)





main()