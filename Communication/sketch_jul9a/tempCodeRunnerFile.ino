void sendModbusCommand(const uint8_t command[], size_t length)
{
  for (size_t i = 0; i < length; i++)
  {
    Serial2.write(command[i]);
  }
}