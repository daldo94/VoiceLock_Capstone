#include <SoftwareSerial.h>

int TxPin = 0;
int RxPin = 1;
int ledPin1 = 13;
SoftwareSerial BTSerial(TxPin, RxPin); 

void setup()  
{
  Serial.begin(9600);
  BTSerial.begin(9600);
  pinMode(ledPin1,OUTPUT);
}

void loop()
{
  if (BTSerial.available())
  {
    char cmd = (char)BTSerial.read();
    if(cmd == '1')
    {
      digitalWrite(ledPin1,HIGH);
    }
    else if(cmd == '2')
    {
      digitalWrite(ledPin1,LOW);
    }

    }
}
