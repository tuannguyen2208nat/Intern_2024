print("Sensors and Actuators")
import time
import serial.tools.list_ports
import random
import relaystate  

def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]     
        strPort = str(port)   
        if "USB Serial" in strPort:      
            splitPort = strPort.split(" ")    
            commPort = (splitPort[0])
    return commPort

portName = getPort()
print(portName)
if portName != "None":
    ser = serial.Serial(port=portName, baudrate=9600)

relay_ON = relaystate.relay_ON
relay_OFF = relaystate.relay_OFF

def setDevice(state, i):
    if state:
        ser.write(relay_ON[i])
        print(f"CH{i} ON")
    else:
        ser.write(relay_OFF[i])
        print(f"CH{i} OFF")

while True:
    for _ in range(5):
        index = random.randint(0, 32) 
        state = random.choice([True, False])
        setDevice(state, index)
    time.sleep(2)