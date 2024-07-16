#include <AdafruitIO.h>
#include <AdafruitIO_WiFi.h>
#include "RelayStatus.h"

#define IO_USERNAME "tuannguyen2208nat"
#define IO_KEY "aio_Tpqz630LWJqmQ5r5g4Kjzeg4BHHh"

#define WIFI_SSID "ACLAB"
#define WIFI_PASS "ACLAB2023"

#define LED_PIN 2
#define TXD 8
#define RXD 9
#define BAUD_RATE 9600

AdafruitIO_WiFi io(IO_USERNAME, IO_KEY, WIFI_SSID, WIFI_PASS);

AdafruitIO_Feed *status = io.feed("status");
void sendModbusCommand(const uint8_t command[], size_t length)
{
  for (size_t i = 0; i < length; i++)
  {
    Serial2.write(command[i]);
  }
}

void setup()
{
  pinMode(LED_PIN, OUTPUT);
  Serial.begin(115200);
  Serial2.begin(BAUD_RATE, SERIAL_8N1, TXD, RXD);

  sendModbusCommand(relay_OFF[32], 32);

  while (!Serial)
    ;

  Serial.println("Connecting to Adafruit IO");
  io.connect();
  status->onMessage(handleMessage);

  while (io.status() < AIO_CONNECTED)
  {
    Serial.println("Can't connect to Adafruit IO");
    delay(500);
  }

  Serial.println();
  Serial.println(io.statusText());

  status->get();
}

void loop()
{
  io.run();
}

void handleMessage(AdafruitIO_Data *data)
{
  String message = data->value();

  if (message.startsWith("!RELAY") && message.endsWith("#"))
  {
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
    if (statusStr == "ON" && index < sizeof(relay_ON) / sizeof(relay_ON[0]))
    {
      sendModbusCommand(relay_ON[index], sizeof(relay_ON[index]));
      Serial.println("Relay " + String(index) + " turned ON");
    }
    else if (statusStr == "OFF" && index < sizeof(relay_OFF) / sizeof(relay_OFF[0]))
    {
      sendModbusCommand(relay_OFF[index], sizeof(relay_OFF[index]));
      Serial.println("Relay " + String(index) + " turned OFF");
    }
    else
    {
      Serial.println("Invalid command");
    }

    String sendData = String(index) + '-' + statusStr;
    status->save(sendData);
    Serial.println("Data sent to Adafruit IO: " + sendData);
  }
}
