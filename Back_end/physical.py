import time
import serial.tools.list_ports
import relaystate
import sys
import tkinter as tk
from tkinter import messagebox

def getPort():
    ports = serial.tools.list_ports.comports()
    commPort = "None"
    
    for port in ports:
        strPort = str(port)
        if "FT232R USB UART" in strPort or "USB Serial" in strPort:
            print("Found USB Serial Device: " + strPort)
            splitPort = strPort.split(" ")
            commPort = splitPort[0]
            break
    
    return commPort

portName = getPort()
if portName != "None":
    ser = serial.Serial(port=portName, baudrate=9600)
else:
    print("No USB Serial device found")
    sys.exit()

relay_ON = relaystate.relay_ON
relay_OFF = relaystate.relay_OFF

def setDevice(state, i):
    if state:
        ser.write(relay_ON[i])
        print(f"CH{i} ON")
    else:
        ser.write(relay_OFF[i])
        print(f"CH{i} OFF")

def submit():
    try:
        i = int(channel_entry.get())
        state_input = state_var.get().lower()
        
        if state_input == 'on':
            state = True
        elif state_input == 'off':
            state = False
        else:
            messagebox.showerror("Input Error", "Invalid state input. Please enter 'ON' or 'OFF'.")
            return
        
        if 0 <= i < len(relay_ON):
            setDevice(state, i)
            status_label.config(text=f"Channel {i} {'ON' if state else 'OFF'}")
        else:
            messagebox.showerror("Input Error", "Invalid channel number. Please enter a valid channel number.")
    
    except ValueError:
        messagebox.showerror("Input Error", "Invalid input. Ensure the channel number is an integer.")

app = tk.Tk()
app.title("Relay Control")

tk.Label(app, text="Enter Relay Channel Number:").pack()
channel_entry = tk.Entry(app)
channel_entry.pack()

tk.Label(app, text="Enter State (ON/OFF):").pack()
state_var = tk.StringVar()
tk.Entry(app, textvariable=state_var).pack()

tk.Button(app, text="Submit", command=submit).pack()
status_label = tk.Label(app, text="")
status_label.pack()

app.mainloop()
