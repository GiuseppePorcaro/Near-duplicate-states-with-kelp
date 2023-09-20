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
from sklearn.model_selection import ValidationCurveDisplay
from sklearn.utils import shuffle
from sklearn.model_selection import validation_curve
from sklearn.model_selection import LearningCurveDisplay
from sklearn.model_selection import learning_curve
from sklearn import preprocessing
import time
import datetime

def main():
    print("Caricamento dataset...")
    csv = pd.read_csv('/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/dataset_-1_1_targets_attrSim_WebTesting.csv', sep=",")

    print("csv shape: ",csv.shape)

    datasetX = csv[csv.columns[0:5]].to_numpy()
    datasetY = csv[csv.columns[5]].to_numpy()

    printDatasetShape(datasetX,datasetY)
    [datasetXPreprocessed] = preProcessingX(datasetX)

    X_train, X_test, y_train, y_test = train_test_split(datasetX, datasetY , test_size=0.30, random_state=42)
    printSplittedDatasetShape(X_train,X_test,y_train,y_test)


    #X, Y = shuffle(datasetXPreprocessed, datasetY, random_state=0)
    #print("X:\n",X,"Y:\n",Y)



    startTime = time.time()
    trainModel(X_train, X_test, y_train, y_test)
    #crossValidation(datasetX,datasetY)
    #validationCurve(X,Y)

    print("Execution time: ",str(datetime.timedelta(seconds=(time.time()-startTime))))


    #learningCurve(datasetX,datasetY)

def validationCurve(X, Y):

    print("\nStarting validation:")
    print(">X shape: ",X.shape)
    print(">Y shape: ",Y.shape)
    print("Computing validation curves...")
    disp = ValidationCurveDisplay.from_estimator(
        svm.SVC(cache_size=1000),
        X,
        Y,
        param_name="gamma",
        param_range=np.logspace(-4, 5, 10),
        score_type="both",
        scoring="recall",
        n_jobs=2,
        score_name="recall",
        cv=2,
    )
    disp.ax_.set_title("Validation Curve for SVM with an RBF kernel")
    disp.ax_.set_xlabel(r"gamma")
    disp.ax_.set_ylim(0.0, 1.1)
    plt.show()

    print("Done!")

def learningCurve(X,Y):

    print("Computing learning curve...")
    scaler = StandardScaler().fit(X)
    X = scaler.transform(X)
    X, Y = shuffle(X, Y, random_state=0)

    train_sizes, train_scores, valid_scores = learning_curve( svm.SVC(kernel='rbf'), X, Y, train_sizes=[10000, 40000, 70000], cv=5, scoring="accuracy")

    print("Train_scores shape: ",train_scores.shape)
    print("valid_scores shape: ",valid_scores.shape)

    makePlot("Learning curve - accuracy", "Accuracy","Folds",range(1,6),train_scores.mean(0),valid_scores.mean(0),train_sizes)

    print("Done!")


def crossValidation(X,Y):

    print("Starting cross validation...")
    clf = make_pipeline(StandardScaler(), svm.SVC(cache_size=1000,C=1, kernel='rbf'))
    scoring = ['precision_macro', 'recall_macro','accuracy']
    score = cross_validate(clf,X,Y,cv=10, scoring=scoring)
    print("\nDone!")

    print("keys: ",score.keys())
    print("Fit time: ",sum(score['fit_time']))
    print("\nScore time: ",sum(score['score_time']))

    precision = score['test_precision_macro']
    recall = score['test_recall_macro']
    accuracy = ['test_accuracy']
    print("\nPrecision: ",precision," - ",precision.shape)
    print("\nRecall: ",recall," - ",recall.shape)
    print("\nAccuracy: ",accuracy," - ",accuracy.shape)

    makePlot("Cross-Validation - Precision e Recall","Precision - Recall","Fold",range(1,11), precision, recall, accuracy)

def makePlot(title, xlabel,ylabel,range,X1, X2, X3):
    plt.title(title)
    plt.xlabel(ylabel)
    plt.ylabel(xlabel)

    plt.plot(range, X1, color="red", label="train")
    plt.plot(range, X2, color="blue", label="valid")

    plt.show()

def trainModel(X_train, X_test, y_train, y_test):

    print("\nTraining model...")
    clf = svm.SVC(cache_size=1000, kernel='rbf', C=0.1, gamma=0.1)
    clf.fit(X_train, y_train)
    print("Complete!")

    print("\nTesting model...")
    y_pred = clf.predict(X_test)
    print("Done!\n\n>Accuracy:",metrics.accuracy_score(y_test, y_pred))

    print(">Precision: ",metrics.precision_score(y_test,y_pred)) #Ci dice l'abilità del classificatore di non etichettare come positiva una etichetta negativa
    print(">Recall: ",metrics.recall_score(y_test,y_pred)) #Ci dice l'abilità del classificatore di trovare tutte le etichette positive

def preProcessingX(X):
    print("\nPre processing...")
    scaler = preprocessing.MinMaxScaler(feature_range=(-1,1))
    X = scaler.fit_transform(X)
    #print(">X scaled:\n",X)
    print("Done!\n")

    return [X]

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