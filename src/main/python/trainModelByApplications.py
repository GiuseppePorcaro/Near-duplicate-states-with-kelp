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
from sklearn.model_selection import GroupShuffleSplit
import time
import datetime

def main():


    [foldsX,foldsY] = getFolds(csv)




    print("FoldsX: ",foldsX[0])
    print("FoldsY: ",foldsY[0])

    print(sum)
def getFolds(csv):

    foldsX = []
    foldsY = []

    appsIndexes = [8515, 17766, 11628, 11325, 11325, 9730, 11026, 11175, 4851]

    print("Caricamento dataset...")

    start = 0
    for i in range(0,9):

        if i == 0:
            realStart = 0
        end = appsIndexes[i]

        print("start: ",start+1," - ",end)

        csv = pd.read_csv('/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/dataset_-1_1_targets.csv', sep=",", skiprows=realStart, nrows=end)
        foldsX.append(csv[csv.columns[0:csv.shape[1]-1]].to_numpy())
        foldsY.append(csv[csv.columns[csv.shape[1]-1]].to_numpy())
        
        start = start + appsIndexes[i]
        realStart = start + 1

    return [foldsX,foldsY]


def setGroups():


    group = np.empty((0,97341))

    start = 0
    for i in range(0,len(appsIndexes)):
        group[(start+i):appsIndexes[i]]

    '''
    
    

    group[0:appsIndexes[0]] = 1
    group[appsIndexes[0]+1:appsIndexes[1]] = 2
    group[appsIndexes[1]+1:appsIndexes[2]] = 3
    group[appsIndexes[2]+1:appsIndexes[3]] = 4
    group[appsIndexes[3]+1:appsIndexes[4]] = 5
    group[appsIndexes[4]+1:appsIndexes[5]] = 6
    group[appsIndexes[5]+1:appsIndexes[6]] = 7
    group[appsIndexes[6]+1:appsIndexes[7]] = 8
    group[appsIndexes[7]+1:appsIndexes[8]] = 9

    '''

    return group



main()