const int enterB = 2;   //button at 2
const int exitB = 3;    //button at 3
const int buzz = 8;

int buttonState1;             
int lastButtonState1 = LOW;

int buttonState2;             
int lastButtonState2 = LOW;

int buttonState3;             
int lastButtonState3 = LOW;
   
int currentCount;
int longTermCount;

unsigned long lastDebounceTime1 = 0; 
unsigned long lastDebounceTime2 = 0;
unsigned long lastDebounceTime3 = 0;
   
unsigned long debounceDelay = 10;    

void setup() {
  pinMode(exitB, INPUT);
  pinMode(enterB, INPUT);
  pinMode(buzz, OUTPUT);
  Serial.begin(9600);
}

void loop() {
  // read the state of the switch into a local variable:
 currentCount = currentCounter(exitB);
 currentCount = currentCounter(enterB);
 //longTermCount = longTermCounter(enterB);

// Serial.print("Current Count: ");
// Serial.println(currentCount);
// Serial.print(",");
 
// Serial.print("Long Term Count: ");
 //Serial.print(longTermCount);
 //Serial.println();
 //delay(50);
}

int currentCounter(int button)
{
   if(button == enterB)
   {
    int reading = digitalRead(button);
    if (reading != lastButtonState1) {
    lastDebounceTime1 = millis();
  }

  if ((millis() - lastDebounceTime1) > debounceDelay) {
    
    if (reading != buttonState1) {
      buttonState1 = reading;
      
      if (buttonState1 == HIGH) {
          tone1(buzz);
          currentCount++;
          Serial.println(currentCount);
          //Serial.print(",");
      }
    }
  }
  lastButtonState1 = reading;


  return currentCount;
   }

      if(button == exitB)
   {
    int reading = digitalRead(button);
    if (reading != lastButtonState2) {
    lastDebounceTime2 = millis();
  }

  if ((millis() - lastDebounceTime2) > debounceDelay) {
    
    if (reading != buttonState2) {
      buttonState2 = reading;
      
      if (buttonState2 == HIGH) {
          tone2(buzz);
          currentCount--;
          
          if(currentCount < 0)
          {
              currentCount = 0;
              tone3(buzz);
          }
          
          Serial.println(currentCount);
          //Serial.print(",");
      }
    }
  }
  lastButtonState2 = reading;



  return currentCount;
  
   }
  
}

int longTermCounter(int button)
{
  if(button == enterB)
   {
    int reading = digitalRead(button);
    if (reading != lastButtonState3) {
    lastDebounceTime3 = millis();
  }

  if ((millis() - lastDebounceTime3) > debounceDelay) {
    
    if (reading != buttonState3) {
      buttonState3 = reading;
      
      if (buttonState3 == HIGH) {
          tone1(buzz);
          longTermCount++;
          Serial.println(longTermCount);
      }
    }
  }
  lastButtonState3 = reading;

  
  return longTermCount;
   }
}

void tone1(int pin)
{
  digitalWrite(pin,HIGH);
  delay(100);
  digitalWrite(pin,LOW);
  delay(50);
  digitalWrite(pin,HIGH);
  delay(100);
  digitalWrite(pin,LOW);
}

void tone2(int pin)
{
  digitalWrite(pin,HIGH);
  delay(250);
  digitalWrite(pin,LOW);
  delay(100);
}

void tone3(int pin)
{
  digitalWrite(pin,HIGH);
  delay(250);
  digitalWrite(pin,LOW);
  delay(100);
  digitalWrite(pin,HIGH);
  delay(250);
  digitalWrite(pin,LOW);
  delay(100);
  digitalWrite(pin,HIGH);
  delay(250);
  digitalWrite(pin,LOW);
  delay(100);
}
