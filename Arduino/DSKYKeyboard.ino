/*
 * Copyright 2022, William Glasford
 *
 *  DSKY:
 *
 *  This is the Arduino code to capture input from the keyboard of the DSKY.  
 *  This slave processor deals with the display minutia and does not store any 
 *  state information.  The code waits for a key press and then sends it to 
 *  the Raspberry Pi.
 *
 * Mods:     07/04/22  Initial Release.
 *  
 */
 
#include <limits.h>

#define BITMASK(b) (1 << ((b) % CHAR_BIT))
#define BITSLOT(b) ((b) / CHAR_BIT)
#define BITSET(a, b) ((a)[BITSLOT(b)] |= BITMASK(b))
#define BITCLEAR(a, b) ((a)[BITSLOT(b)] &= ~BITMASK(b))
#define BITTEST(a, b) ((a)[BITSLOT(b)] & BITMASK(b))

const byte ROWS = 3;
const byte COLS = 7;
const byte TOTAL_KEYS = ROWS * COLS;

char lastKeySetting [(TOTAL_KEYS + CHAR_BIT - 1) / CHAR_BIT];  // one bit each, 0 = up, 1 = down
unsigned long lastKeyTime[TOTAL_KEYS];
const unsigned long DEBOUNCE_TIME = 10; // In milliseconds
const bool DEBUGGING = true;

// The row and column pin assignments.
const byte rowPins [ROWS] = {9, 10, 11};
const byte colPins [COLS] = {2, 3, 4, 5, 6, 7, 8};

// Two dimensional array that maps each key based on row/col to the return value.
int returnValue[ROWS][COLS] = {
  { 17, 26, 7, 8, 9, 30, 28 },
  { 31, 27, 4, 5, 6, 0, 18 },
  { -1, 16, 1, 2, 3, 25, -1 }
};

/*
 * The setup initializes what is needed to be initialized and then does not continue until
 * an identify command is received.  The identify command is a character "2".
 */
void setup() 
{
  String commandString;
  
  Serial.begin (9600);
  // Wait for Serial to activate.
  while (!Serial) { }  

  // Set each column's input-pullup resistor.
  for (byte i = 0; i < COLS; i++)
  {
    pinMode (colPins [i], INPUT_PULLUP);
  }


  if (DEBUGGING)
  {
    Serial.println("Keyboard Initialized for debugging...");
  }
  else
  {
    // Wait for an initialize command.
    bool initialized = false;
    while (!initialized)
    {
      while (Serial.available()) 
      {
        delay(2);  // Delay 2 milliseconds to allow byte to arrive in input buffer
        char c = Serial.read();  // Gets one byte from serial buffer
    
        if (c != '\n' && c != '\r')  
          commandString += c; 
      }

      if (commandString.length() > 0 && commandString.equals("2"))
      {
        Serial.println("Keyboard");
        initialized = true;
      }
    }
  }
}

/*
 * This is the main Arduino loop that runs continuously.  It waits for a key press and then 
 * sends the value to the Raspberry Pi.  Each key is assigned a row/column value and a code.  
 * 
 * Key    Col Row Key #
 * -----  --- --- -----
 * VERB   0   0   17
 * NOUN   0   1   31
 * "+"    1   0   26
 * "-"    1   1   27
 * 0      1   2   16
 * 7      2   0   7
 * 4      2   1   4
 * 1      2   2   1
 * 8      3   0   8
 * 5      3   1   5
 * 2      3   2   2
 * 9      4   0   9
 * 6      4   1   6
 * 3      4   2   3
 * CLR    5   0   30
 * PRO    5   1   0
 * KEYREL 5   2   25
 * ENTER  6   0   28
 * RESET  6   1   18
 */
void loop() 
{
  byte keyNumber = 0;
  unsigned long now = millis();

  for (byte row = 0; row < ROWS; row++)
  {
    // Set this row to OUTPUT and LOW
    pinMode (rowPins [row], OUTPUT);
    digitalWrite (rowPins [row], LOW);

    // Check each column to see if the switch has driven the column LOW.
    for (byte col = 0; col < COLS; col++)
    {
      // Ignore key bounces.
      if (now - lastKeyTime[keyNumber] >= DEBOUNCE_TIME)
      {
        // Check to see if key is in pressed state.
        bool keyState = digitalRead (colPins [col]) == LOW;
        if (keyState != (BITTEST (lastKeySetting, keyNumber) != 0))
        {
          lastKeyTime[keyNumber] = now;
          // Save the new state.
          if (keyState)
            BITSET (lastKeySetting, keyNumber);
          else
            BITCLEAR (lastKeySetting, keyNumber);

          if (keyState)
          {
            if (DEBUGGING)
            {
              Serial.print (F("Key: "));
              Serial.print (row);
              Serial.print (F(", "));
              Serial.println (col);
            }
            else
            {
              Serial.write (returnValue[row][col]);
            }
          }
        }
      }
      keyNumber++;
    }
    // Set row pin back to not reading.
    pinMode (rowPins [row], INPUT);
  }
}
