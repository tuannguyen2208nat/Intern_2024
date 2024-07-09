#include <AdafruitIO.h>
#include <AdafruitIO_WiFi.h>
#include <SoftwareSerial.h>
#include "RelayStatus.h"

#define IO_USERNAME  "tuannguyen2208nat"
#define IO_KEY       "aio_ZMAD70yk9WBlfvJMFK1MgWjUCj4K"

#define WIFI_SSID "ACLAB"
#define WIFI_PASS "ACLAB2023"

AdafruitIO_WiFi io(IO_USERNAME, IO_KEY, WIFI_SSID, WIFI_PASS);

#define LED_PIN 2
#define RS485_RX_PIN 21
#define RS485_TX_PIN 18

AdafruitIO_Feed *status = io.feed("status");
SoftwareSerial rs485(RS485_RX_PIN, RS485_TX_PIN);

// Function to send Modbus command via RS485
void sendModbusCommand(const uint8_t command[], size_t length) {
  for (size_t i = 0; i < length; i++) {
    rs485.write(command[i]);
  }
}

void setup() {
  pinMode(LED_PIN, OUTPUT);
  Serial.begin(115200);
  rs485.begin(9600);

  // Initialize all relays to OFF
  // sendModbusCommand(relay_OFF[relay_OFF.size() - 1], sizeof(relay_OFF[0]));

  while (!Serial);

  Serial.println("Connecting to Adafruit IO");
  io.connect();
  status->onMessage(handleMessage);

  while (io.status() < AIO_CONNECTED) {
    Serial.println("Can't connect to Adafruit IO");
    delay(500);
  }

  Serial.println();
  Serial.println(io.statusText());

  status->get();
}

void loop() {
  io.run();
}

void handleMessage(AdafruitIO_Data *data) {
  String message = data->value();

  if (message.startsWith("!RELAY") && message.endsWith("#")) {
    int indexStart = message.indexOf('!') + 6;
    int indexEnd = message.indexOf(':');
    String indexStr = message.substring(indexStart, indexEnd);
    int index = indexStr.toInt();

    int statusStart = indexEnd + 1;
    int statusEnd = message.indexOf('#');
    String statusStr = message.substring(statusStart, statusEnd);

    // Debug prints
    Serial.print("Raw message: ");
    Serial.println(message);
    Serial.print("Index string: ");
    Serial.println(indexStr);
    Serial.print("Index: ");
    Serial.println(index);
    Serial.print("Status string: ");
    Serial.println(statusStr);

    // Send the Modbus command for the specific relay
    if (statusStr == "ON" && index < sizeof(relay_ON) / sizeof(relay_ON[0])) {
      sendModbusCommand(relay_ON[index], sizeof(relay_ON[index]));
      Serial.println("Relay " + String(index) + " turned ON");
    } else if (statusStr == "OFF" && index < sizeof(relay_OFF) / sizeof(relay_OFF[0])) {
      sendModbusCommand(relay_OFF[index], sizeof(relay_OFF[index]));
      Serial.println("Relay " + String(index) + " turned OFF");
    } else {
      Serial.println("Invalid command");
    }

    String sendData = String(index) + '-' + statusStr;
    status->save(sendData);
    Serial.println("Data sent to Adafruit IO: " + sendData);
  }
}
