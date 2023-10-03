import pandas as pd
import numpy as np
import csv
import sys
import os
import time
import datetime
import matplotlib.pyplot as plt
from multiprocessing import Process
from sklearn import svm
from sklearn import metrics
from sklearn.model_selection import ValidationCurveDisplay
from sklearn.utils import resample
from sklearn.model_selection import KFold
from joblib import dump, load

def main():

    #argv[1] = method; argv[2] dataset; argv[3] = C; argv[4] = gamma;
    #print("Path: ", os.getcwd())

    [method, dataset, C, gamma, doResample] = getCommandParams()

    print(">Creating folds based on applications...")
    [foldsX,foldsY] = getFolds(csv, dataset) #array in which each fold is an app
    print(">Resampling folds (stratified)...")
    [foldsXResampled,foldsYResampled] = resampleXY(foldsX,foldsY) #array of resampled folds (1000 sample each stratified)


    if method == "experiment":
        print("Selected experiment -> doResample will be ignored!") # probabilmente si dovrà implementare un do refactor anche per l'esperimento
        experiment(C,gamma, foldsX, foldsY, foldsXResampled, foldsYResampled, dataset)
    if method == "validation":
        print("Selected validation -> C and gamma will be ignored!")
        modelValidation(foldsX, foldsY, foldsXResampled, foldsYResampled, dataset, np.logspace(-2, 5, 8), doResample)


def modelValidation( foldsX, foldsY, foldsXResampled, foldsYResampled, dataset, range, doResample):

    scores = ["f1","precision","recall"]
    params = ["C","gamma"]

    if doResample == 1:
        [X, Y] = concatenateFolds(foldsXResampled, foldsYResampled,-1)
    elif doResample == 0:
        [X, Y] = concatenateFolds(foldsX, foldsY,-1)
    else:
        print("\nERROR: Need to set [doResample] as 0 or 1!Closing script!")
        return

    #Add a control to check if cpu has at least 6 threads?

    #Parallelizing type of validation based on what score we are computing. One type of validation for thread
    processes = []
    for score in scores:
        p = Process(target=validationCurve,kwargs={'X':X,'Y':Y,'dataset':dataset,'range':range,'score':score, 'param':params[0],'fixedParamName':params[1]})
        p.start()
        processes.append(p)
        time.sleep(1)
        validationCurve( X, Y, dataset, range, score, params[0], params[1])

    for score in scores:
        p = Process(target=validationCurve,kwargs={'X':X,'Y':Y,'dataset':dataset,'range':range,'score':score, 'param':params[1],'fixedParamName':params[0]})
        p.start()
        processes.append(p)
        time.sleep(1)
        validationCurve(X, Y, dataset, range, score, params[1], params[0])

    for process in processes:
        process.join()



def validationCurve(X, Y, dataset, range, score, param, fixedParamName):

    timestamp = printValidationInfos(X.shape,Y.shape, dataset, score, param, fixedParamName)

    startTimeTot = time.time()

    if param == "C":
        clf = svm.SVC(cache_size=1000, C=range, class_weight="balanced")
    if param == "gamma":
        clf = svm.SVC(cache_size=1000, gamma=range, class_weight="balanced")


    i = 1
    for fixedParam in range:
        startTime = time.time()
        disp = ValidationCurveDisplay.from_estimator(
            clf,
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

        plt.savefig("/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/plots/Plots_ApplicationSplit/output/ValidationCurve_"+score+"_"+param+"_"+dataset+"_"+fixedParamName+"_"+execTime+"_"+timestamp+".png")
    print("Execution time tot: ",str(datetime.timedelta(seconds=(time.time()-startTimeTot))))

    print("Done!")


def experiment(C, gamma, foldsX, foldsY, foldXResampled, foldYResampled, datasetName):

    date = datetime.datetime.now()
    timestamp = date.strftime('%Y-%m-%d %H:%M:%S.%f')
    timestamp = timestamp[:-7]

    path= "/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/src/main/resources/models/outputModelScores/experiment_"+datasetName+"_"+timestamp+".csv"
    #path= "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/models/outputModelScores/experiment_"+datasetName+"_"+timestamp+".csv"

    fieldnames = ['f1','precision', 'recall', 'accuracy', 'executionTime', 'appTest']
    with open(path, 'a', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()

        file.close()

    for i in range(0,len(foldsX)):
        [X_train, y_train] = concatenateFolds(foldXResampled,foldYResampled,i)
        [f1,precision,recall,accuracy, execTime] = trainModel(X_train, foldXResampled[i], y_train, foldYResampled[i],C, gamma,datasetName, timestamp, i)

        saveScores(f1, precision, recall,accuracy, execTime,timestamp,datasetName, i, path)


def trainModel(X_train, X_test, y_train, y_test,Cparam, gammaParam,datasetName, timestamp,i):

    print("Dataset:"+datasetName+"\nTraining model with C: ",Cparam," and gamma: ",gammaParam)
    startTimeTot = time.time()

    clf = svm.SVC(cache_size=4000, kernel='rbf', C=Cparam, gamma=gammaParam, random_state=42, class_weight="balanced")
    clf.fit(X_train, y_train)
    #print("Complete!")
    #print("\nTesting model...")
    y_pred = clf.predict(X_test)

    execTime = str(datetime.timedelta(seconds=(time.time()-startTimeTot)))

    [accuracy, f1,precision, recall] = getScores(y_test,y_pred)
    print(">f1: ",f1,"\n>Precision: ",precision,"\n>Recall: ",recall,"\n>Accuracy:", accuracy,"\nExecution time: ",execTime)

    path = "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/models/output/model_"+datasetName+"_"+timestamp+"_["+str(i)+"].joblib"
    dump(clf, path)
    print("########################################################################")
    #print("########################################################################")

    return [f1,precision,recall,accuracy, execTime]

def saveScores(f1, precision, recall,accuracy, execTime,timestamp, datasetName, i, path):
    fieldnames = ['f1','precision', 'recall', 'accuracy', 'executionTime', 'appTest']

    with open(path, 'a', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)

        writer.writerow({'f1': f1,'precision' : precision, 'recall':recall, 'accuracy':accuracy,'executionTime':execTime,'appTest':i})

        file.close()

def createCSVToSaveScores():
    path= "/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/models/outputModelScores/experiment_"+datasetName+"_"+timestamp+".csv"
    fieldnames = ['f1','precision', 'recall', 'accuracy', 'executionTime', 'appTest']
    with open(path, 'a', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()

        file.close()

def getScores(y_test, y_pred):

    accuracy = metrics.accuracy_score(y_test, y_pred)
    f1 = metrics.f1_score(y_test,y_pred)
    precision = metrics.precision_score(y_test,y_pred)
    recall = metrics.recall_score(y_test,y_pred)

    return [accuracy, f1,precision, recall]

def getCommandParams():
    method = sys.argv[1]
    dataset = sys.argv[2]
    C = float(sys.argv[3])
    gamma = float(sys.argv[4])
    doResample = int(sys.argv[5])

    return [method, dataset, C, gamma, doResample]
def printValidationInfos(Xshape,Yshape,dataset, score, param, fixedParam):
    date = datetime.datetime.now()
    timestamp = date.strftime('%Y-%m-%d %H:%M:%S.%f')
    timestamp = timestamp[:-7]

    print("\nStarting validation:")
    print("Starting time: ",timestamp)
    print(">Dataset: ",dataset)
    print(">X shape: ",Xshape)
    print(">Y shape: ",Yshape)
    print(">Score name: ",score,"\n>Param name: ",param,"\n>Fixed param: ",fixedParam)
    print("\nComputing validation curves...")
    print(sum)

    return timestamp

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

    #print("Done!\nDataset shapes: ")
    #print(datasetX.shape)
    #print(datasetY.shape)
    #print(datasetX)
    #print(datasetY)

    return [datasetX,datasetY]

def resampleXY(X,Y):

    foldsXResampled = []
    foldsYResampled = []

    for i in range(0,9):
        [foldsXRes, foldsYRes] = resample(X[i],Y[i], n_samples = 1000, stratify=Y[i], random_state = 42)
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

        #/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources
        csv = pd.read_csv('/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/src/main/resources/data/'+dataset, sep=",", skiprows=realStart, nrows=end)
        foldsX.append(csv[csv.columns[0:csv.shape[1]-1]].to_numpy())
        foldsY.append(csv[csv.columns[csv.shape[1]-1]].to_numpy())
        
        start = start + appsIndexes[i]
        realStart = start + 1

    return [foldsX,foldsY]




main()