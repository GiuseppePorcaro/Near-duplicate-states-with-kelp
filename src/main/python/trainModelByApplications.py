import pandas as pd
import numpy as np
import csv
from sklearn import svm
from sklearn import metrics
import matplotlib.pyplot as plt
from sklearn.model_selection import ValidationCurveDisplay
from sklearn.model_selection import validation_curve
from sklearn.model_selection import LearningCurveDisplay
from sklearn.model_selection import GroupShuffleSplit
from sklearn.utils import resample
import time
import datetime
from sklearn.model_selection import KFold

def main():


    print(">Creating folds based on applications...")
    [foldsX,foldsY] = getFolds(csv) #array in which each fold is an app
    print(">Resampling folds (stratified)...")
    [foldsXResampled,foldsYResampled] = resampleXY(foldsX,foldsY) #array of resampled folds (1000 sample each stratified)
    print(">Concatenating folds to create dataset...")
    [datasetX,datasetY] = concatenateFolds(foldsXResampled,foldsYResampled)
    print("Done!\nDataset shapes: ")
    print(datasetX.shape)
    print(datasetY.shape)
    print(datasetX)
    print(datasetY)

    validationCurve(datasetX,datasetY,"dataset_-1_1_targets")

def validationCurve(X, Y, dataset):


    score = "recall"
    param = "gamma"
    fixedParamName = "C"

    printValidationInfos(X.shape,Y.shape, dataset, score, param, fixedParamName)

    startTimeTot = time.time()

    i = 1
    for fixedParam in np.logspace(-2, 6, 9):
        startTime = time.time()
        disp = ValidationCurveDisplay.from_estimator(
            svm.SVC(cache_size=1000, C=fixedParam),
            X,
            Y,
            param_name=param,
            param_range=np.logspace(-2, 6, 9),
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

        plt.savefig("/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/plots/Plots_ApplicationSplit/AttrSimOnly/ValidationCurve_"+score+"_"+param+"_"+dataset+"_"+fixedParamName+"_"+execTime+".png")
    print("Execution time tot: ",str(datetime.timedelta(seconds=(time.time()-startTimeTot))))

    print("Done!")

def printValidationInfos(Xshape,Yshape,dataset, score, param, fixedParam):


    print("\nStarting validation:")
    print(">Dataset: ",dataset)
    print(">X shape: ",Xshape)
    print(">Y shape: ",Yshape)
    print(">Score name: ",score,"\n>Param name: ",param,"\n>Fixed param: ",fixedParam)
    print("\nComputing validation curves...")
    print(sum)

def concatenateFolds(foldsXResampled,foldsYResampled):
    datasetX = foldsXResampled[0]
    datasetY = foldsYResampled[0]

    for i in range(1,9):
        datasetX = np.concatenate((datasetX,foldsXResampled[i]))
        datasetY = np.concatenate((datasetY, foldsYResampled[i]))

    return [datasetX,datasetY]

def resampleXY(X,Y):

    foldsXResampled = []
    foldsYResampled = []

    for i in range(0,9):
        [foldsXRes, foldsYRes] = resample(X[0],Y[0], n_samples = 1000)
        foldsXResampled.append(np.array(foldsXRes))
        foldsYResampled.append(np.array(foldsYRes))

    return [foldsXResampled,foldsYResampled]

def getFolds(csv):

    foldsX = []
    foldsY = []

    appsIndexes = [8515, 17766, 11628, 11325, 11325, 9730, 11026, 11175, 4851]

    start = 0
    for i in range(0,9):

        if i == 0:
            realStart = 0
        end = appsIndexes[i]

        csv = pd.read_csv('/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/dataset_-1_1_targets.csv', sep=",", skiprows=realStart, nrows=end)
        foldsX.append(csv[csv.columns[0:1]].to_numpy())
        foldsY.append(csv[csv.columns[csv.shape[1]-1]].to_numpy())
        
        start = start + appsIndexes[i]
        realStart = start + 1

    return [foldsX,foldsY]




main()