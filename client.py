import datetime
import time
import pymysql
import matplotlib.pyplot as plt
import matplotlib.legend as legend

conn=pymysql.connect(host="192.168.219.21",
					user="admin",
					passwd="wowodnd1",
					db="raspi_db")

current=datetime.datetime.now()
last_time=current-datetime.timedelta(minutes=1)
try:
	lstx=[]
	lst_tmp=[]
	lst_hum=[]
	fig=plt.gcf()
	fig.canvas.draw()
	with conn.cursor() as cur:
		sql="select *. rom collect_data where collect_time ? %s"
		while True:
			i=0

			for row in cur.fetchall():
				lstx.append(row[1])
				lst_tmp.append(row[2])
				lst_hum.append(row[3])
				last_time=row[1]
				i+=1
				conn.commit()

			if i == 0:
				plt.pause(0.01)
				fig.canvas.draw()
				time.sleep(0.5)
				continue
			plt.ylim(0,100)
			tmp=plt.plot(lstx,lst_tmp,'r.-')
			hum=plt.plot(lstx,lst_hum,'g.-')
			plt.legend([tmp,hum], ['Temperature', 'Humidity'],loc=3)
			plt.pause(0.01)
			fig.canvas.draw()
			time.sleep(0,5)
except KeyboardInterrupt:
	exit()
finally:
	conn.close()
