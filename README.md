# Near-duplicate-states-with-kelp

Near duplicates with kelp is a master's thesis project at the University of Naples Federico II

## Python scripts tutorial
### trainModelByApplications.py
Used to start an experiment or a validation using sample divided by their original application. <br>
#### Requirements
listed in /src/main/python/requirements.txt file
#### Usage
python3 trainModelByApplications.py [method] [dataset] [C] [gamma]
#### command line arguments
[method] is either <b>experiment</b> or <b>validation</b>. Former to start an experiment, the latter to start a validation.<br><hr>
[dataset] is one of the .csv files present in /src/main/resources/data, and it's the dataset form which samples are loaded. (is possible to create more dataset with createCSVDatasetScript.py script. It needs some changes to create the desired dataset).<br><hr>
[C] and [gamma] are svm classifier parameters used in the experiment and in the validation.Ignored if method is <b>validation</b>.<br><hr>

