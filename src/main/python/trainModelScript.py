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
    csv = pd.read_csv('/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/dataset_TreeEditDistance.csv', sep=",")

    print("csv shape: ",csv.shape)

    datasetX = csv[csv.columns[0:1]].to_numpy()
    datasetY = csv[csv.columns[1]].to_numpy()

    printDatasetShape(datasetX,datasetY)
    #[datasetXPreprocessed] = preProcessingX(datasetX)


    X, Y = shuffle(datasetX, datasetY)
    print("X:\n",X,"\nY:\n",Y)



    startTime = time.time()
    #X_train, X_test, y_train, y_test = train_test_split(datasetX, datasetY , test_size=0.30, random_state=42)
    #printSplittedDatasetShape(X_train,X_test,y_train,y_test)
    #trainModel(X_train, X_test, y_train, y_test)

    #crossValidation(datasetX,datasetY)
    validationCurve(X,Y)
    #learningCurve(X,Y)
    #manualValidationCUrve(X, Y)

    print("Execution time: ",str(datetime.timedelta(seconds=(time.time()-startTime))))

def manualValidationCUrve(X, Y):

    X = X[0:100]
    Y = Y[0:100]

    print("\nStarting validation:")
    print(">X shape: ",X.shape)
    print(">Y shape: ",Y.shape)
    print("\nComputing validation curves...")

    for fixedParam in np.logspace(-2, 7, 10):
        disp = ValidationCurveDisplay.from_estimator(
            svm.SVC(cache_size=1000, gamma=fixedParam),
            X,
            Y,
            param_name="C",
            param_range=np.logspace(-2, 7, 10),
            score_type="both",
            scoring="f1",
            n_jobs=8,
            score_name="f1",
            cv=2,
        )
        disp.ax_.set_title("Validation Curve for SVM with an RBF kernel")
        disp.ax_.set_xlabel(r"C")
        disp.ax_.set_ylim(0.0, 1.1)
        plt.show()

    print("Done!")


def validationCurve(X, Y):

    fitParams={"gamma": [0.01,0.1,10,100,1000,10000,100000,1000000,10000000]}
    gss = GroupShuffleSplit(n_splits=2, train_size=.7, test_size=.3 random_state=42)

    print("\nStarting validation:")
    print(">X shape: ",X.shape)
    print(">Y shape: ",Y.shape)
    print("Computing validation curves...")

    disp = ValidationCurveDisplay.from_estimator(
        svm.SVC(cache_size=1000),
        X,
        Y,
        param_name="C",
        param_range=np.logspace(-2, 7, 10),
        score_type="both",
        scoring="f1",
        n_jobs=-1,
        score_name="f1",
        cv=2,
        groups=gss

    )
    disp.ax_.set_title("Validation Curve for SVM with an RBF kernel")
    disp.ax_.set_xlabel(r"C")
    disp.ax_.set_ylim(0.0, 1.1)
    plt.show()

    print("Done!")

def learningCurve(X,Y):

    print("Computing learning curve...")

    disp = LearningCurveDisplay.from_estimator(svm.SVC(cache_size=1000),
        X, 
        Y, 
        train_sizes=[15000,25000,35000,48670], 
        cv=2, 
        scoring='accuracy', 
        score_name='accuracy',
        score_type='both',
        n_jobs=2,
    )
    disp.ax_.set_title("Learning Curve for SVM with an RBF kernel")
    disp.ax_.set_xlabel(r"Number of Sample")
    disp.ax_.set_ylim(0.0, 1.1)
    plt.show()

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

    for Cparam in np.logspace(-2, 7, 10):
        for gammaParam in np.logspace(-2, 7, 10):


            print("\nTraining model with C: ",Cparam," and gamma: ",gammaParam)
            clf = svm.SVC(cache_size=4000, kernel='rbf', C=Cparam, gamma=gammaParam)
            clf.fit(X_train, y_train)
            print("Complete!")

            print("\nTesting model...")
            y_pred = clf.predict(X_test)
            print("Done!\n\n>Accuracy:",metrics.accuracy_score(y_test, y_pred))

            print(">Precision: ",metrics.precision_score(y_test,y_pred)) #Ci dice l'abilità del classificatore di non etichettare come positiva una etichetta negativa
            print(">Recall: ",metrics.recall_score(y_test,y_pred)) #Ci dice l'abilità del classificatore di trovare tutte le etichette positive

            plot.title("")



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