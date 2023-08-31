import sqlite3
import subprocess
import time
import threading
from subprocess import STDOUT,PIPE
import multiprocessing

counter = 0
lock = threading.Lock()

def main():
    
    threads = []
    
    numCPU = multiprocessing.cpu_count()
    slice = round(97490/numCPU) +1 
    print(slice)
    start = 0
    for i in range(0,numCPU):
        start = i*slice
        t = threading.Thread(target=importdb, args=[str(start),str(slice),"Thread "+str(i)])
        t.start()
        threads.append(t)
    for x in threads:
     x.join()
    

def importdb(start,slice,t):
    global counter
    global lock
    
    pathDB = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/gs.db"
    

    conn = sqlite3.connect(pathDB)
    c = conn.cursor()
    c.execute("SELECT appname, crawl,state1, state2 from nearduplicates limit "+start+","+slice+";")

    startTime = time.time()
    for table in c.fetchall():
        counterStr = str(counter)
        subprocess.run(["java","-jar","/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/target/kelp_test-1.0-SNAPSHOT-jar-with-dependencies.jar",table[0],table[1],table[2],table[3],counterStr])
        
        lock.acquire()
        counter = counter+1
        lock.release()
        
        if(counter % 10 == 0):
            endTime = time.time()
            elapsedTime = (endTime-startTime)
            print("\nt: "+t+" "+str(elapsedTime)+"\n")


main()

