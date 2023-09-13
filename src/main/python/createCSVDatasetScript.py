import sqlite3
import csv

def main():

    path = '/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/dataset.csv'
    pathDB = '/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/gs_full_dom_mu01.db'
    pathDBStats  = '/run/media/giuseppeporcaro/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/gs_stats.db'
    
    print("Creating dataset as csv in:\n",path,"\n\nfrom:\n>",pathDB,"\n>",pathDBStats)
    with open(path, 'w', newline='') as file:
        fieldnames = ['attribute_sim','numNodes1','height1','degree1','averageBranchingFactor1','numNodes2','height2','degre2','averageBranchingFactor2','human_classification']
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()

        conn = sqlite3.connect(pathDB)
        c = conn.cursor()
        c.execute("SELECT appname, crawl, state1, state2, Attribute_sim, human_classification from nearduplicates where human_classification <> -1 order by appname, crawl, state1, state2;")

        connStats = sqlite3.connect(pathDBStats)
        cStats = connStats.cursor()

        counter = 0
        for table in c.fetchall():
            appname = str(table[0])
            crawl = str(table[1])
            state1 = str(table[2])
            state2 = str(table[3])
            attributeSim = float(table[4])
            humanClassification = int(table[5])

            [numNodes1,height1,degree1,averageBranchingFactor1] = getHtmlTreeStats(appname,crawl,state1,cStats)
            [numNodes2,height2,degre2,averageBranchingFactor2] = getHtmlTreeStats(appname,crawl,state2,cStats)

            writer.writerow({'attribute_sim': attributeSim,'numNodes1': numNodes1,'height1' : height1,'degree1' : degree1,'averageBranchingFactor1' : averageBranchingFactor1,'numNodes2' : numNodes2,'height2' : height2,'degre2' : degre2,'averageBranchingFactor2' : averageBranchingFactor2,'human_classification' : humanClassification})

            counter = counter + 1
            #print(attributeSim," ",numNodes1," ",height1," ",degree1," ",averageBranchingFactor1," ",numNodes2," ",height2," ",degre2," ",averageBranchingFactor2," ",humanClassification,"\t| "+str(counter))
        
        connStats.close()
        conn.close()

        file.close()
        print("Done!")

def getHtmlTreeStats(appname, crawl, state,cStats):
    cStats.execute("Select numNodes, height, degree, averageBranchingFactor from states where appname=\""+appname+"\" and crawl=\""+crawl+"\" and state = \""+state+"\";")
    rowStats = cStats.fetchone()
    #print(rowStats)
    numNodes = int(rowStats[0])
    height = int(rowStats[1])
    degree = int(rowStats[2])
    averageBranchingFactor = float(rowStats[3])

    return [numNodes,height,degree,averageBranchingFactor]

main()