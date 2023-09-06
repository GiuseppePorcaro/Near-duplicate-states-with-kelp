import sqlite3
import subprocess
import time
import threading
from subprocess import STDOUT,PIPE
import multiprocessing

counter = 0
lock = threading.Lock()

def main():
    #num tot row nearduplicates 97490
    
    threads = []
    
    numCPU = multiprocessing.cpu_count()
    numRows = 8515
    slice = round(numRows/numCPU) + 1 
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
    
    pathDB = "/Volumes/SDDPeppe/Università/Libri_università/Magistrale/Tesi_magistrale/Web_Test_Generation/Crawls_complete/GroundTruthModels/gs_dom_senza_tag_tabelle.db"
    
    lock.acquire()
    conn = sqlite3.connect(pathDB)
    c = conn.cursor()
    c.execute("SELECT appname, crawl,state1, state2 from nearduplicates where appname=\"addressbook\" order by appname, crawl, state1, state2 limit "+start+","+slice+";")
    rows = c.fetchall()
    conn.close()
    lock.release()

    startTime = time.time()
    for table in rows:
        counterStr = str(counter)
        out = subprocess.Popen(["java","-jar","/Users/giuseppeporcaro/Desktop/Libri_università/Magistrale/Tesi magistrale/Web Test Generation/Tool Web Testing/Near-duplicate-states-with-kelp/target/kelp_test-1.0-SNAPSHOT-jar-with-dependencies.jar",table[0],table[1],table[2],table[3],counterStr],stdout=subprocess.PIPE)
        token = str(out.stdout.read()).split("|")

        appname = str(table[0])
        crawl = str(table[1])
        state1 = str(table[2])
        state2 = str(table[3])
        kernel = token[1]

        print(appname+" "+ crawl+" "+ state1+" "+state2+ " -- "+kernel + "| ",counter)
        lock.acquire()

        counter = counter+1
        conn = sqlite3.connect(pathDB,timeout=10)
        c = conn.cursor()
        c.execute('''update nearduplicates set ATTRIBUTE_SIM = ? where appname= ? and crawl = ? and state1 = ? and state2 = ?;''',(kernel,appname,crawl,state1,state2))
        conn.commit()
        conn.close()

        lock.release()
        
        if(counter % 10 == 0):
            endTime = time.time()
            elapsedTime = (endTime-startTime)
            print("\nt: "+t+" "+str(elapsedTime)+"\n")

        
main()

