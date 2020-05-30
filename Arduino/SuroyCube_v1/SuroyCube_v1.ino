const int buttonPin = 2;    
int buttonState;            
int lastButtonState = LOW;   
int count = 0;


unsigned long lastDebounceTime = 0;  
unsigned long debounceDelay = 50;    

void setup() {
  pinMode(buttonPin, INPUT);
  Serial.begin(9600);
}

void loop() {

  int reading = digitalRead(buttonPin);

  if (reading != lastButtonState) {

    lastDebounceTime = millis();
  }

  if ((millis() - lastDebounceTime) > debounceDelay) {


    if (reading != buttonState) {
      buttonState = reading;


      if (buttonState == HIGH) {
        count++;
        
      }
    }
  }
  Serial.println(count);
  lastButtonState = reading;
}
