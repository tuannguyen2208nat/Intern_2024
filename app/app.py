import relaystate
import sys
import serial.tools.list_ports
import tkinter as tk
from tkinter import scrolledtext

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
    else:
        ser.write(relay_OFF[i])




def on_toggle(button, button_number, log_widget):
    if button.config('text')[-1] == 'OFF':
        button.config(text='ON')
        setDevice(True, button_number)
        log_widget.insert(tk.END, f"Relay {button_number} turned ON\n")
    else:
        button.config(text='OFF')
        setDevice(False, button_number)
        log_widget.insert(tk.END, f"Relay {button_number} turned OFF\n")
    log_widget.see(tk.END)

def create_toggle_buttons(frame, num_buttons, log_widget):
    buttons = []
    for i in range(num_buttons):
        relay_frame = tk.Frame(frame, bg='black')
        relay_frame.pack(pady=5, fill=tk.X)
        
        button = tk.Button(relay_frame, text='OFF', width=10,
                           command=lambda i=i, button=None: on_toggle(buttons[i], i+1, log_widget))
                           
        button.pack(side=tk.RIGHT, padx=(0, 10))
        
        label = tk.Label(relay_frame, text=f"Relay {i+1}", fg='white', bg='black')
        label.pack(side=tk.LEFT)
        
        relay_frame.pack_configure(anchor='center')
        buttons.append(button)
    return buttons

if __name__ == "__main__":
    root = tk.Tk()
    root.title("CaCo IOT Check")
    
    header = tk.Frame(root, bg='#a5efa2', height=50)
    header.pack(side=tk.TOP, fill=tk.X)
    header_label = tk.Label(header, text="CaCo IOT Check", bg='#a5efa2', font=('Arial', 16))
    header_label.pack(pady=10)


    main_frame = tk.Frame(root, bg='black')
    main_frame.pack(side=tk.TOP, fill=tk.BOTH, expand=True)

    left_frame = tk.Frame(main_frame, bg='black')
    left_frame.pack(side=tk.LEFT, fill=tk.Y, padx=10, pady=10)

    canvas = tk.Canvas(left_frame, bg='black')
    canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

    scrollbar = tk.Scrollbar(left_frame, orient=tk.VERTICAL, command=canvas.yview)
    scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

    scrollable_frame = tk.Frame(canvas, bg='black')
    scrollable_frame.bind(
        "<Configure>",
        lambda e: canvas.configure(
            scrollregion=canvas.bbox("all")
        )
    )

    canvas.create_window((0, 0), window=scrollable_frame, anchor="nw")
    canvas.configure(yscrollcommand=scrollbar.set)

    right_frame = tk.Frame(main_frame, bg='black')
    right_frame.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=10, pady=10)

    log_widget = scrolledtext.ScrolledText(right_frame, wrap=tk.WORD, width=50, height=20, bg='black', fg='white')
    log_widget.pack(fill=tk.BOTH, expand=True)

    num_buttons = 32
    buttons = create_toggle_buttons(scrollable_frame, num_buttons, log_widget)

    root.mainloop()
