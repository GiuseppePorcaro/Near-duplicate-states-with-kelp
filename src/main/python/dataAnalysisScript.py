import matplotlib.pyplot as plt
import numpy as np
import sqlite3


def main():


    [simOfNearDuplicate,simOfDifferent] = getSimilarities("/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/gs_full_dom_mu01.db")

    print("simOfNearDuplicate shape: ",simOfNearDuplicate.shape)
    print("simOfDifferent shape: ",simOfDifferent.shape)

    print(simOfNearDuplicate)

    plt.title('Histogram')
    plt.xlabel('bins')
    plt.ylabel('Similarities')
    plt.hist(simOfDifferent, bins=100, histtype='barstacked', label="Different pages", color="blue")
    plt.hist(simOfNearDuplicate, bins=100, histtype='barstacked', label="NearDuplicates", color="orange")
    plt.legend(['Different Pages','Near Duplicates'])
    plt.savefig("/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/plots/Histogram_AttributeSImilarity.png")



def getSimilarities(pathDB):

    simOfNearDuplicate = []
    simOfDifferent = []

    conn = sqlite3.connect(pathDB)
    c = conn.cursor()
    c.execute("SELECT Attribute_sim, human_classification from nearduplicates where human_classification <> -1 order by appname, crawl, state1, state2;")

    for row in c.fetchall():

        humanClassification = int(row[1])

        if humanClassification == 0 or humanClassification == 1:
            simOfNearDuplicate.append(float(row[0]))
        if humanClassification == 2:
            simOfDifferent.append(float(row[0]))

    c.close()

    return [np.array(simOfNearDuplicate),np.array(simOfDifferent)]

main()