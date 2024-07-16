import random
import time
import sys
from Adafruit_IO import MQTTClient
import serial.tools.list_ports
import pymongo
import datetime
from dotenv import dotenv_values

# Load configuration from .env file
config = dotenv_values(".env")
AIO_FEED_IDS = ["status"]
AIO_USERNAME = config.get("AIO_USERNAME")
AIO_KEY = config.get("AIO_KEY")

def connected(client):
    print("Connected successfully")
    for feed in AIO_FEED_IDS:
        client.subscribe(feed)

def subscribe(client, userdata, mid, granted_qos):
    print("Subscription successful")

def disconnected(client):
    print("Disconnected")
    sys.exit(1)

def message(client, feed_id, payload):
    print("Received data: " + payload + " on feed " + feed_id)

client = MQTTClient(AIO_USERNAME, AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe

try:
    client.connect()
    client.loop_background()
except Exception as e:
    print("Error connecting to MQTT: ", e)
    sys.exit(1)

def getPort():
    ports = serial.tools.list_ports.comports()
    for port in ports:
        if "Hello" in str(port):
            return port.device
    return None

port = getPort()
if port:
    try:
        ser = serial.Serial(port=port, baudrate=9600)
    except Exception as e:
        print("Error opening serial port: ", e)
        sys.exit(1)
else:
    print("No compatible serial port found")
    sys.exit(1)

while True:
    try:
        time.sleep(2)
        # Additional serial port or MQTT handling code can be added here
    except KeyboardInterrupt:
        print("Interrupted by user")
        break
    except Exception as e:
        print("An error occurred: ", e)
        break

# Ensure the client disconnects properly on exit
client.disconnect()
