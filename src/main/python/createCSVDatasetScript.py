import sqlite3
import csv

def main():

    path = '/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/output/file__8.csv'
    pathDB = '/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/gs_full_dom_mu01.db'
    pathDBStats  = '/home/giuseppeporcaro/Documenti/GitHub/Near-duplicate-states-with-kelp/src/main/resources/data/gs_stats.db'
    
    print("Creating dataset as csv in:\n",path,"\n\nfrom:\n>",pathDB,"\n>",pathDBStats)
    with open(path, 'w', newline='') as file:
        #fieldnames = ['attribute_sim','numNodes1','height1','degree1','averageBranchingFactor1','numNodes2','height2','degre2','averageBranchingFactor2','human_classification']
        #fieldnames = ['attribute_sim','numNodes1','numNodes2','human_classification']
        fieldnames = ['Attribute_sim','human_classification']

        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()

        conn = sqlite3.connect(pathDB)
        c = conn.cursor()
        c.execute("SELECT ATTRIBUTE_SIM, human_classification from nearduplicates where human_classification <> -1 order by appname, crawl, state1, state2  LIMIT 92490,4851;")

        connStats = sqlite3.connect(pathDBStats)
        cStats = connStats.cursor()

        counter = 0
        for row in c.fetchall():
            [Attribute_sim, humanClassification] = getParameters(row)

            #[numNodes1,height1,degree1,averageBranchingFactor1] = getHtmlTreeStats(appname,crawl,state1,cStats)
            #[numNodes2,height2,degre2,averageBranchingFactor2] = getHtmlTreeStats(appname,crawl,state2,cStats)

            #writer.writerow({'attribute_sim': attributeSim,'numNodes1': numNodes1,'height1' : height1,'degree1' : degree1,'averageBranchingFactor1' : averageBranchingFactor1,'numNodes2' : numNodes2,'height2' : height2,'degre2' : degre2,'averageBranchingFactor2' : averageBranchingFactor2,'human_classification' : humanClassification})
            #writer.writerow({'attribute_sim': attributeSim,'numNodes1': numNodes1,'numNodes2' : numNodes2,'human_classification' : humanClassification})
            writer.writerow({'Attribute_sim': round(float(Attribute_sim),13),'human_classification' : int(humanClassification)})

            counter = counter + 1
            #print(attributeSim," ",numNodes1," ",height1," ",degree1," ",averageBranchingFactor1," ",numNodes2," ",height2," ",degre2," ",averageBranchingFactor2," ",humanClassification,"\t| "+str(counter))
        
        connStats.close()
        conn.close()

        file.close()
        print("Done!")


def getParameters(row):
    Attribute_sim = float(row[0])

    humanClassification = convertHumanClassification(int(row[1]))

    return [Attribute_sim, humanClassification]

def getHtmlTreeStats(appname, crawl, state,cStats):
    cStats.execute("Select numNodes, height, degree, averageBranchingFactor from states where appname=\""+appname+"\" and crawl=\""+crawl+"\" and state = \""+state+"\";")
    rowStats = cStats.fetchone()
    #print(rowStats)
    numNodes = int(rowStats[0])
    height = int(rowStats[1])
    degree = int(rowStats[2])
    averageBranchingFactor = float(rowStats[3])

    return [numNodes,height,degree,averageBranchingFactor]

def convertHumanClassification(humanClassification):

    if humanClassification == 2:
        return -1

    if humanClassification == 1 or humanClassification == 0:
        return 1

main()