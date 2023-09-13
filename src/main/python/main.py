import pandas as pd
import numpy as np
import sqlite3
import csv
from sklearn.model_selection import train_test_split
from sklearn import svm
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
from sklearn import metrics

def main():
    print("Caricamento dataset...")
    csv = pd.read_csv('/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/dataset3_preCheck_9Param.csv', sep=",")

    datasetX = csv[csv.columns[0]].to_numpy()
    datasetY = csv[csv.columns[9]].to_numpy()

    print("Sample shape: ",datasetX.shape)
    print("Target shape: ",datasetY.shape)
    print("\n\n")
    print("Sample:\n",datasetX)
    print("Target:\n",datasetY)
    
    X_train, X_test, y_train, y_test = train_test_split(datasetX, datasetY , test_size=0.30, random_state=42)

    print("X_train shape: ",X_train.shape)
    print("X_test shape: ",X_test.shape)
    print("y_train shape: ",y_train.shape)
    print("y_test shape: ",y_test.shape)

    print("Training model...")
    clf = make_pipeline(StandardScaler(), svm.SVC(cache_size=1000, kernel='rbf'))
    clf.fit(X_train.reshape(-1, 1), y_train)
    print("Complete!")

    print("\nTesting model...")
    y_pred = clf.predict(X_test.reshape(-1, 1))
    print("Done!\nAccuracy:",metrics.accuracy_score(y_test, y_pred))




 

    


main()