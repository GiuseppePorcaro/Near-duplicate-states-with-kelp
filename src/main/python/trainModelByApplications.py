import pandas as pd
import numpy as np
import csv
import sys
import os
import time
import datetime
import matplotlib.pyplot as plt
from configparser import ConfigParser
from multiprocessing import Process
from sklearn import svm
from sklearn import metrics
from sklearn.model_selection import ValidationCurveDisplay
from sklearn.utils import resample
from sklearn.model_selection import KFold
from joblib import dump, load

def main():

    configFilePath = os.getcwd()+"/config.csv"

    [method, datasetPath, datasetName, outputPath, C, gamma, doResample] = getConfigParams(configFilePath)

    print(">Creating folds based on applications...")
    [foldsX,foldsY] = getFolds(csv, datasetName,datasetPath) #array in which each fold is an app
    print(">Resampling folds (stratified)...")
    [foldsXResampled,foldsYResampled] = resampleXY(foldsX,foldsY) #array of resampled folds (1000 sample each stratified)


    if method == "experiment":
        print("Selected experiment -> doResample will be ignored!") # probabilmente si dovrà implementare un do refactor anche per l'esperimento
        experiment(C,gamma, foldsX, foldsY, foldsXResampled, foldsYResampled, datasetName, outputPath)
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
        #validationCurve( X, Y, dataset, range, score, params[0], params[1])

    for score in scores:
        p = Process(target=validationCurve,kwargs={'X':X,'Y':Y,'dataset':dataset,'range':range,'score':score, 'param':params[1],'fixedParamName':params[0]})
        p.start()
        processes.append(p)
        time.sleep(1)
        #validationCurve(X, Y, dataset, range, score, params[1], params[0])

    for process in processes:
        process.join()



def validationCurve(X, Y, dataset, range, score, param, fixedParamName):

    timestamp = printValidationInfos(X.shape,Y.shape, dataset, score, param, fixedParamName)

    startTimeTot = time.time()

    if param == "C":
        clf = svm.SVC(cache_size=1000, C=range)
    if param == "gamma":
        clf = svm.SVC(cache_size=1000, gamma=range)


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


def experiment(C, gamma, foldsX, foldsY, foldXResampled, foldYResampled, datasetName,outputPath):

    date = datetime.datetime.now()
    timestamp = date.strftime('%Y-%m-%d %H:%M:%S.%f')
    timestamp = timestamp[:-7]

    #path= "/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/src/main/resources/models/outputModelScores/experiment_"+datasetName+"_"+timestamp+".csv"
    path= outputPath+"/experiment_"+datasetName+"_"+timestamp+".csv"

    createCSVToSaveScores(path)

    f1TrainArray = np.empty((len(foldsX),1))
    precisionTrainArray = np.empty((len(foldsX),1))
    recallTrainArray = np.empty((len(foldsX),1))
    f1TestArray = np.empty((len(foldsX),1))
    precisionTestArray = np.empty((len(foldsX),1))
    recallTestArray = np.empty((len(foldsX),1))

    for i in range(0,len(foldsX)):
        [X_train, y_train] = concatenateFolds(foldsX,foldsY,i)
        print("X_train shape: ",X_train.shape)
        print("y_train shape: ",y_train.shape)
        print("foldsX[",i,"] shape: ",foldsX[i].shape)
        print("foldsY[",i,"] shape: ",foldsY[i].shape)

        [f1Train,precisionTrain, recallTrain, f1Test,precisionTest, recallTest, execTime] = trainModel(X_train, foldsX[i], y_train, foldsY[i],C, gamma,datasetName, timestamp, i, outputPath)
        f1TrainArray[i] = f1Train
        precisionTrainArray[i] = precisionTrain
        recallTrainArray[i] = recallTrain
        f1TestArray[i] = f1Test
        precisionTestArray[i] = precisionTest
        recallTestArray[i] = recallTest

        saveScores(f1Train, precisionTrain, recallTrain,f1Test, precisionTest, recallTest, execTime,timestamp,datasetName, i, path)

    pathMean = outputPath+"/experimentMean_"+datasetName+"_"+timestamp+".csv"
    saveScores(np.mean(f1TrainArray),np.mean(precisionTrainArray),np.mean(recallTrainArray),np.mean(f1TestArray),np.mean(precisionTestArray),np.mean(recallTrainArray),execTime,timestamp,datasetName, -1, pathMean)



def trainModel(X_train, X_test, y_train, y_test,Cparam, gammaParam,datasetName, timestamp,i, outputPath):

    print("Dataset:"+datasetName+"\nTraining model with kernel Linear - C: ",Cparam)
    startTimeTot = time.time()

    clf = svm.SVC(cache_size=4000, kernel='linear', C=Cparam, random_state=42, class_weight="balanced")
    clf.fit(X_train, y_train)
    y_pred = clf.predict(X_test)
    y_predTrain = clf.predict(X_train)

    execTime = str(datetime.timedelta(seconds=(time.time()-startTimeTot)))

    [f1Train,precisionTrain, recallTrain] = getScores(y_train,y_predTrain)
    [f1Test,precisionTest, recallTest] = getScores(y_test,y_pred)
    print(">f1Train: ",f1Train," - precisionTrain: ",precisionTrain," - recallTrain: ",recallTrain)
    print(">f1Test: ",f1Test," - precisionTest: ",precisionTest," - recallTest: ",recallTest," - Execution time: ",execTime)

    path = outputPath+"/model_"+datasetName+"_"+timestamp+"_["+str(i)+"].joblib"
    dump(clf, path)
    print("########################################################################")

    return [f1Train,precisionTrain, recallTrain, f1Test,precisionTest, recallTest, execTime]

def saveScores(f1Train, precisionTrain, recallTrain,f1Test, precisionTest, recallTest, execTime,timestamp, datasetName, i, path):
    fieldnames = ['f1Train','precisionTrain', 'recallTrain', 'f1Test','precisionTest','recallTest', 'executionTime', 'appTest']

    with open(path, 'a', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)

        writer.writerow({'f1Train': f1Train,'precisionTrain' : precisionTrain, 'recallTrain':recallTrain, 'f1Test':f1Test,'precisionTest':precisionTest,'recallTest':recallTest,'executionTime':execTime,'appTest':i})

        file.close()

def createCSVToSaveScores(path):
    fieldnames = ['f1Train','precisionTrain', 'recallTrain', 'f1Test','precisionTest','recallTest', 'executionTime', 'appTest']
    with open(path, 'a', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()

        file.close()

def getScores(y_test, y_pred):

    f1 = metrics.f1_score(y_test,y_pred)
    precision = metrics.precision_score(y_test,y_pred)
    recall = metrics.recall_score(y_test,y_pred)

    return [f1,precision, recall]

def getConfigParams(path):

    config = ConfigParser()
    config.read(path)

    method = config['config']['method']
    datasetPath = config['config']['datasetPath']
    outputPath=config['config']['outputPath']
    C=float(config['config']['C'])
    gamma=float(config['config']['gamma'])
    doResample=int(config['config']['resampling'])
    datasetName=config['config']['datasetName']

    return [method, datasetPath, datasetName, outputPath, C, gamma, doResample]
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

def concatenateFolds(foldsX, foldsY, indexNotToConcat):
    start = 0
    if indexNotToConcat == 0:
        start = 1

    datasetX = foldsX[start]
    datasetY = foldsY[start]

    for i in range(start+1, len(foldsX)):
        if indexNotToConcat == i:
            continue
        datasetX = np.concatenate((datasetX, foldsX[i]))
        datasetY = np.concatenate((datasetY, foldsY[i]))

    return [datasetX,datasetY]

def resampleXY(X,Y):

    foldsXResampled = []
    foldsYResampled = []

    for i in range(0,9):
        [foldsXRes, foldsYRes] = resample(X[i],Y[i], n_samples = 1000, stratify=Y[i], random_state = 42)
        foldsXResampled.append(np.array(foldsXRes))
        foldsYResampled.append(np.array(foldsYRes))

    return [foldsXResampled,foldsYResampled]

def getFolds(csv, datasetName, datasetPath):

    foldsX = []
    foldsY = []

    appsIndexes = [8515, 17766, 11628, 11325, 11325, 9730, 11026, 11175, 4851]

    start = 0
    for i in range(0,9):

        if i == 0:
            realStart = 0
        end = appsIndexes[i]

        #/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources
        csv = pd.read_csv(datasetPath+"/"+datasetName, sep=",", skiprows=realStart, nrows=end)
        foldsX.append(csv[csv.columns[0:csv.shape[1]-1]].to_numpy())
        foldsY.append(csv[csv.columns[csv.shape[1]-1]].to_numpy())
        
        start = start + appsIndexes[i]
        realStart = start

    return [foldsX,foldsY]

def createCSVForEachFold(foldsX, foldsY,outputPath):

    print("Start...")
    for i in range(0,len(foldsX)):
        for k in range(0,foldsX[i].shape[0]):

            Attribute_sim = str(foldsX[i][k][0])
            humanClassification = str(foldsY[i][k])
            with open(outputPath+"/file_"+str(i)+".csv", 'a', newline='') as file:
                fieldnames = ['Attribute_sim','human_classification']

                writer = csv.DictWriter(file, fieldnames=fieldnames)

                writer.writerow({'Attribute_sim': round(float(Attribute_sim),13),'human_classification' : humanClassification})



    print("Done!")

main()

