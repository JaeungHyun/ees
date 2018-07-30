import pymysql
import json

with open('secrets.json') as f:
	secrets = json.load(f)

conn=pymysql.connect(host="192.168.219.21",
                    user=secrets['user'],
                    passwd=secrets['passwd'],
                    db=secrets['db'])

try:
    with conn.cursor() as cur:
        sql="select * from collect_data"
        cur.execute(sql)
        for row in cur.fetchall() :
            print(row[0], row[1], row[2])
finally:
    conn.close()