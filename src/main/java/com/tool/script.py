import sqlite3
import subprocess


def main():
    #pathDB = "F:\\Università\\Libri_università\\Magistrale\\Tesi_magistrale\\Web_Test_Generation\\Crawls_complete\\GroundTruthModels\\gs.db"
    #importdb(pathDB)
    subprocess.run(["cd", "E:\\GitHub\\Near-duplicate-states-with-kelp\\src\\main\\java\\com\\tool"])



def importdb(db):
    conn = sqlite3.connect(db)
    c = conn.cursor()
    c.execute("SELECT appname, crawl,state1, state2 from nearduplicates limit 10;")
    for table in c.fetchall():
        print(table[0])


main()

