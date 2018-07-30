import RPi.GPIO as GPIO
import sys
import time
import Adafruit_DHT
import pymysql
import json

with open('secrets.json') as f:
	secrets = json.load(f)

sensor = Adafruit_DHT.DHT22
conn = pymysql.connect(host=secrets['host'],
                    user=secrets['user'],
                    passwd=secrets['passwd'],
                    db=secrets['db'])

pin = 20
try:
    with conn.cursor() as cur:
        sql="insert into collect_data values(%s, %s, %s, %s)"
        while True:
            humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)
            if humidity is not None and temperature is not None:
                print("Temp=%0.1f°C Humidity=%0.1f%" % (temperature, humidity))
                cur.execute(sql, ('DHT22', time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()),
                            temperature, humidity))
                conn.commit()
            else:
                print("Failed to get temp and humidity")
            time.sleep(1)
except KeyboardInterrupt :
    exit()
finally:
    conn.close()