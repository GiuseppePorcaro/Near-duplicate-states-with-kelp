import pandas as pd
import numpy as np
import csv
import sys
import os
from sklearn import svm
from sklearn import metrics
import matplotlib.pyplot as plt
from sklearn.model_selection import ValidationCurveDisplay
from sklearn.utils import resample
import time
import datetime
from sklearn.model_selection import KFold
from joblib import dump, load

def main():

    #argv[1] = method; argv[2] dataset; argv[3] = C; argv[4] = gamma;
    print("Path: ", os.getcwd())
    #resource
    method = sys.argv[1]
    dataset = sys.argv[2]
    C = float(sys.argv[3])
    gamma = float(sys.argv[4])


    print(">Creating folds based on applications...")
    [foldsX,foldsY] = getFolds(csv, dataset) #array in which each fold is an app
    print(">Resampling folds (stratified)...")
    [foldsXResampled,foldsYResampled] = resampleXY(foldsX,foldsY) #array of resampled folds (1000 sample each stratified)


    if method == "experiment":
        experiment(C,gamma, foldsX, foldsY, foldsXResampled, foldsYResampled, dataset)
    if method == "validaiton":
        [datasetX,datasetY] = concatenateFolds(foldsXResampled,foldsYResampled, -1)
        validationCurve(datasetX,datasetY,"dataset_TreeEditDistance", np.logspace(-2, 5, 8))


def validationCurve(X, Y, dataset, range):

    score = "recall"
    param = "gamma"
    fixedParamName = "C"

    printValidationInfos(X.shape,Y.shape, dataset, score, param, fixedParamName)

    startTimeTot = time.time()

    i = 1
    for fixedParam in range:
        startTime = time.time()
        disp = ValidationCurveDisplay.from_estimator(
            svm.SVC(cache_size=1000, C=fixedParam),
            X,
            Y,
            param_name=param,
            param_range=range,
            score_type="both",
            scoring=score,
            n_jobs=2,
            score_name=score,
            cv=KFold(n_splits=9, random_state=None, shuffle=False),

        )

        execTime = str(datetime.timedelta(seconds=(time.time()-startTime)))
        disp.ax_.set_title("Validation Curve (SVM, RBF) - CV split - "+fixedParamName+": "+str(fixedParam)+" - "+dataset)
        disp.ax_.set_xlabel(param)
        disp.ax_.set_ylim(0.0, 1.1)
        plt.figure(i)
        i = i+1
        print("Execution time figure("+str(i)+"): ",execTime)

        plt.savefig("src/main/resources/plots/Plots_ApplicationSplit/output/ValidationCurve_"+score+"_"+param+"_"+dataset+"_"+fixedParamName+"_"+execTime+".png")
    print("Execution time tot: ",str(datetime.timedelta(seconds=(time.time()-startTimeTot))))

    print("Done!")


def experiment(C, gamma, foldsX, foldsY, foldXResampled, foldYResampled, datasetName):

    date = datetime.datetime.now()
    timestamp = date.strftime('%Y-%m-%d %H:%M:%S.%f')
    timestamp = timestamp[:-7]


    #for i in range(0,len(foldsX)):
        #[X_train, y_train] = concatenateFolds(foldXResampled,foldYResampled,i)
        #[f1,precision,recall,accuracy, execTime] = trainModel(X_train, foldXResampled[i], y_train, foldYResampled[i],C, gamma,datasetName, timestamp)

        #print(foldXResampled[i])
        #print(foldYResampled[i])
        #print(foldsX[i])
        #print(foldsY[i])

        #saveScores(f1, precision, recall,accuracy, execTime,timestamp,datasetName, i)


    np.set_printoptions(threshold=sys.maxsize)

    debug(C, gamma, foldsX, foldsY, foldXResampled, foldYResampled, datasetName, timestamp)

def debug(C, gamma, foldsX, foldsY, foldXResampled, foldYResampled, datasetName, timestamp):

    [X_train, y_train] = concatenateFolds(foldXResampled,foldYResampled,-1)

    print(foldsX[1])
    print(foldsY[1])

    X_test = X_train[5001:6000]
    y_test = y_train[5001:6000]

    X_train = np.concatenate((X_train[0:5000],X_train[6001:]))
    y_train = np.concatenate((y_train[0:5000],y_train[6001:]))
    trainModel(X_train, foldsX[5], y_train, foldsY[5],C, gamma,datasetName, timestamp)

def trainModel(X_train, X_test, y_train, y_test,Cparam, gammaParam,datasetName, timestamp):

    print("Dataset:"+datasetName+"\nTraining model with C: ",Cparam," and gamma: ",gammaParam)
    startTimeTot = time.time()

    clf = svm.SVC(cache_size=4000, kernel='rbf', C=Cparam, gamma=gammaParam, random_state=42)
    clf.fit(X_train, y_train)
    #print("Complete!")
    #print("\nTesting model...")
    y_pred = clf.predict(X_test)
    #print(y_pred.shape)
    #print(y_test.shape)
    #print(y_pred)
    #print(y_test)

    execTime = str(datetime.timedelta(seconds=(time.time()-startTimeTot)))

    [accuracy, f1,precision, recall] = getScores(y_test,y_pred)
    print(">f1: ",f1,"\n>Precision: ",precision,"\n>Recall: ",recall,"\n>Accuracy:", accuracy, " - ",set(y_test) - set(y_pred))

    path = "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/models/output/model_"+datasetName+"_"+timestamp+".joblib"
    dump(clf, path)
    print("########################################################################")
    #print("########################################################################")

    return [f1,precision,recall,accuracy, execTime]

def saveScores(f1, precision, recall,accuracy, execTime,timestamp, datasetName, i):
    path= "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/models/outputModelScores/experiment_"+datasetName+"_"+timestamp+".csv"
    with open(path, 'w', newline='') as file:
        writer = csv.writer(file)
        fieldnames = ["f1","precision", "recall", "accuracy", "executionTime", "appTest"]
        writer.writerow([f1, precision, recall,execTime])
        writer.writerow({'f1': f1,'precision' : precision, 'recall':recall, 'accuracy':accuracy,'executionTime':execTime,"appTest":i})

    file.close()


def getScores(y_test, y_pred):

    accuracy = metrics.accuracy_score(y_test, y_pred)
    f1 = metrics.f1_score(y_test,y_pred)
    precision = metrics.precision_score(y_test,y_pred)
    recall = metrics.recall_score(y_test,y_pred)

    return [accuracy, f1,precision, recall]
def printValidationInfos(Xshape,Yshape,dataset, score, param, fixedParam):

    print("\nStarting validation:")
    print(">Dataset: ",dataset)
    print(">X shape: ",Xshape)
    print(">Y shape: ",Yshape)
    print(">Score name: ",score,"\n>Param name: ",param,"\n>Fixed param: ",fixedParam)
    print("\nComputing validation curves...")
    print(sum)

def concatenateFolds(foldsXResampled,foldsYResampled, indexNotToConcat):
    print(">Concatenating folds to create dataset...")
    start = 0
    if indexNotToConcat == 0:
        start = 1

    datasetX = foldsXResampled[start]
    datasetY = foldsYResampled[start]

    for i in range(start+1,len(foldsXResampled)):
        if indexNotToConcat == i:
            continue
        datasetX = np.concatenate((datasetX,foldsXResampled[i]))
        datasetY = np.concatenate((datasetY, foldsYResampled[i]))

    print("Done!\nDataset shapes: ")
    #print(datasetX.shape)
    #print(datasetY.shape)
    #print(datasetX)
    #print(datasetY)

    return [datasetX,datasetY]

def resampleXY(X,Y):

    foldsXResampled = []
    foldsYResampled = []

    for i in range(0,9):
        [foldsXRes, foldsYRes] = resample(X[i],Y[i], n_samples = 1000, stratify=Y[i])
        foldsXResampled.append(np.array(foldsXRes))
        foldsYResampled.append(np.array(foldsYRes))

    return [foldsXResampled,foldsYResampled]

def getFolds(csv, dataset):

    foldsX = []
    foldsY = []

    appsIndexes = [8515, 17766, 11628, 11325, 11325, 9730, 11026, 11175, 4851]

    start = 0
    for i in range(0,9):

        if i == 0:
            realStart = 0
        end = appsIndexes[i]

        csv = pd.read_csv('/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/'+dataset, sep=",", skiprows=realStart, nrows=end)
        foldsX.append(csv[csv.columns[0:csv.shape[1]-1]].to_numpy())
        foldsY.append(csv[csv.columns[csv.shape[1]-1]].to_numpy())
        
        start = start + appsIndexes[i]
        realStart = start + 1

    return [foldsX,foldsY]




main()