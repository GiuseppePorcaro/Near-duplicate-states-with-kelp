import pandas as pd
import numpy as np
import sqlite3
import csv
from sklearn.model_selection import train_test_split
from sklearn import svm
from sklearn.pipeline import make_pipeline
from sklearn.preprocessing import StandardScaler
from sklearn import metrics
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import cross_validate
import matplotlib.pyplot as plt

def main():
    print("Caricamento dataset...")
    csv = pd.read_csv('/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/dataset_-1_1_targets.csv', sep=",")

    print("csv shape: ",csv.shape)

    datasetX = csv[csv.columns[0:9]].to_numpy()
    datasetY = csv[csv.columns[9]].to_numpy()

    printDatasetShape(datasetX,datasetY)
    
    X_train, X_test, y_train, y_test = train_test_split(datasetX, datasetY , test_size=0.30, random_state=42)
    printSplittedDatasetShape(X_train,X_test,y_train,y_test)

    [X_trainScaled, X_testSCaled] = preProcessingX(X_train,X_test)

    #trainModel(X_trainScaled, X_testSCaled, y_train, y_test)
    crossValidation(datasetX,datasetY)

def crossValidation(X,Y):

    print("Starting cross validation...")
    clf = make_pipeline(StandardScaler(), svm.SVC(cache_size=1000,C=1, kernel='rbf'))
    scoring = ['precision_macro', 'recall_macro']
    score = cross_validate(clf,X,Y,cv=10, scoring=scoring, return_train_score=True)
    print("\nDone!")

    print("keys: ",score.keys())
    print("Fit time: ",sum(score['fit_time']))
    print("\nScore time: ",sum(score['score_time']))

    precision = score['test_precision_macro']
    recall = score['test_recall_macro']
    print("\nPrecision: ",precision)
    print("\nRecall: ",recall)

    makePlot("Cross-Validation - Precision","Precision","Fold",range(1,11), precision)
    makePlot("Cross-Validation - Recall", "Recall","Fold", range(1,11), recall)


def makePlot(title, xlabel,ylabel,range,X):
    plt.title(title)
    plt.xlabel(ylabel)
    plt.ylabel(xlabel)
    plt.plot(range, X, color="red")

    plt.show()

def trainModel(X_train, X_test, y_train, y_test):

    print("\nTraining model...")
    clf = svm.SVC(cache_size=1000, kernel='rbf')
    clf.fit(X_train, y_train)
    print("Complete!")

    print("\nTesting model...")
    y_pred = clf.predict(X_test)
    print("Done!\n\n>Accuracy:",metrics.accuracy_score(y_test, y_pred))

    print(">Precision: ",metrics.precision_score(y_test,y_pred)) #Ci dice l'abilità del classificatore di non etichettare come positiva una etichetta negativa
    print(">Recall: ",metrics.recall_score(y_test,y_pred)) #Ci dice l'abilità del classificatore di trovare tutte le etichette positive

def preProcessingX(X_train, X_test):
    print("\nPre processing...\n>   X scaled:")
    scaler = StandardScaler().fit(X_train)
    X_trainScaled = scaler.transform(X_train)
    X_testSCaled = scaler.transform(X_test)
    print("\n",X_trainScaled)
    print("Done!\n")

    return [X_trainScaled, X_testSCaled]

def printSplittedDatasetShape(X_train,X_test,y_train,y_test):
    print("\nX_train shape: ",X_train.shape)
    print("X_test shape: ",X_test.shape)
    print("y_train shape: ",y_train.shape)
    print("y_test shape: ",y_test.shape)
def printDatasetShape(datasetX,datasetY):

    print("Sample shape: ",datasetX.shape)
    print("Target shape: ",datasetY.shape)
    print("\n")
    print("Sample:\n",datasetX)
    print("Target:\n",datasetY)

 

    


main()