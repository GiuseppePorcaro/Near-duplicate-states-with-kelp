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

    print("Caricamento dataset...")
    csv = pd.read_csv('/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/dataset_TreeEditDistance.csv', sep=",")

    print("csv shape: ",csv.shape)

    group = np.ones(csv.shape[0])
    group[86166:] = 2

    gss = GroupShuffleSplit(n_splits=2, train_size=.7, test_size=.3, random_state=42)
    datasetX = csv[csv.columns[0:1]].to_numpy()
    datasetY = csv[csv.columns[1]].to_numpy()

    for i, (train_index, test_index) in enumerate(gss.split(datasetX, datasetY, group)):
        print(f"Fold {i}:")
        print(f"  Train: index={train_index}, group={group[train_index]}")
        print(f"  Test:  index={test_index}, group={group[test_index]}")

main()