/*
 * Copyright 2022, William Glasford
 *
 *  DSKY:
 *
 *  This is the Arduino code to run the 3.5" LCD that displays the indicators of the DSKY.  
 *  This slave processor deals with the display minutia and does not store any state information.
 *  The code waits for a command from the higher level Raspberry Pi and executes the command.
 *
 * Mods:     07/04/22  Initial Release.
 *  
 */
#include <SPI.h>
#include "Adafruit_GFX.h"
#include "Adafruit_HX8357.h"

// These are 'flexible' lines that can be changed
#define TFT_CS 8
#define TFT_DC 9
#define TFT_RST -1 // RST can be set to -1 if you tie it to Arduino's reset

// LCD colors: RGB: 16-bit value from MSB to LSB: RRRRRGGGGGGBBBBB
#define BLACK 0x0000
#define WHITE 0xFFFF 
#define GREY 0x3186
#define YELLOW 0xFFEB

#define BOX_WIDTH 156
#define BOX_HEIGHT 60
#define START_COL1 0
#define START_COL2 164
#define START_ROW1 0
#define START_ROW2 68
#define START_ROW3 136
#define START_ROW4 204
#define START_ROW5 272
#define START_ROW6 340
#define START_ROW7 408
#define TEXT_UPPER_ROW 15
#define TEXT_LOWER_ROW 35
#define TEXT_CENTERED 25
#define TEXT_SHORT 60
#define TEXT_MEDIUM 55
#define TEXT_LONG 45
#define TEXT_XLONG 35

// Use hardware SPI (on Uno, #13, #12, #11) and the above for CS/DC
Adafruit_HX8357 tft = Adafruit_HX8357(TFT_CS, TFT_DC, TFT_RST);

// SoftSPI - note that on some processors this might be *faster* than hardware SPI!
//Adafruit_HX8357 tft = Adafruit_HX8357(TFT_CS, TFT_DC, MOSI, SCK, TFT_RST, MISO);

void setup() 
{
  String commandString;
  
  Serial.begin(9600);

  // TODO: Set up LCD
  tft.begin();
  tft.setRotation(2);

  resetDisplay();
  delay(500);

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
      Serial.println("Indicators");
      initialized = true;
    }
    commandString = "";
  }
}

/*
 * This is the main Arduino loop that runs continuously.  It waits for a command from the 
 * Raspberry Pi, then executes the command before waiting for the next command.  Each of the
 * indicator commands are followed by a 0 = off or 1 = on.  The commands are:
 * 0  = Noop
 * 1  = Reset to power on state
 * 2  = Identify
 * 3  = UPLINK ACTY
 * 4  = NO ATT
 * 5  = STBY
 * 6  = KEY REL
 * 7  = OPR ERR
 * 8  = TEMP
 * 9  = GIMBAL LOCK
 * 10 = PROG
 * 11 = RESTART
 * 12 = TRACKER
 * 13 = ALT
 * 14 = VEL
 */
void loop() 
{
  String commandString;

  // Read the next command sequence.
  while (Serial.available() > 0) 
  {
    delay(2);  // Delay 2 milliseconds to allow byte to arrive in input buffer
    char c = Serial.read();  // Gets one byte from serial buffer
    
    if (c != '\n' && c != '\r')  
      commandString += c; 
  }

  // If there is a command string to parse...
  if (commandString.length() > 0) 
  {
    String command = commandString;
    String data = "";
    String delim = " ";

    // Parse the command string.
    if (commandString != "0" && commandString != "1" && commandString != "2")
    {
      int firstDelimIndex = commandString.indexOf(delim);
      command = commandString.substring(0, firstDelimIndex);
      data = commandString.substring(firstDelimIndex + 1, commandString.length());
    }
    
    // Process the Reset command.
    if (command == "1")
    {
     resetDisplay();
    }
    
    // Process the UPLINK ACTY command.
    else if (command == "3")
    {
     if (data == "0")
      {
        tft.fillRect(START_COL1, START_ROW1, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL1 + TEXT_LONG, START_ROW1 + TEXT_UPPER_ROW);
        tft.println("UPLINK");
 
        tft.setCursor(START_COL1 + TEXT_MEDIUM, START_ROW1 + TEXT_LOWER_ROW);
        tft.println("ACTY");
      }
      else
      {
        tft.fillRect(START_COL1, START_ROW1, BOX_WIDTH, BOX_HEIGHT, WHITE);
    
        tft.setCursor(START_COL1 + TEXT_LONG, START_ROW1 + TEXT_UPPER_ROW);
        tft.println("UPLINK");
 
        tft.setCursor(START_COL1 + TEXT_MEDIUM, START_ROW1 + TEXT_LOWER_ROW);
        tft.println("ACTY");
      }
    }

    // Process the NO ATT command.
    else if (command == "4")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL1, START_ROW2, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL1 + TEXT_LONG, START_ROW2 + TEXT_CENTERED);
        tft.println("NO ATT");
      }
      else
      {
        tft.fillRect(START_COL1, START_ROW2, BOX_WIDTH, BOX_HEIGHT, WHITE);
    
        tft.setCursor(START_COL1 + TEXT_LONG, START_ROW2 + TEXT_CENTERED);
        tft.println("NO ATT");
      }
    }

    // Process the STBY command.
    else if (command == "5")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL1, START_ROW3, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL1 + TEXT_MEDIUM, START_ROW3 + TEXT_CENTERED);
        tft.println("STBY");
      }
      else
      {
        tft.fillRect(START_COL1, START_ROW3, BOX_WIDTH, BOX_HEIGHT, WHITE);
    
        tft.setCursor(START_COL1 + TEXT_MEDIUM, START_ROW3 + TEXT_CENTERED);
        tft.println("STBY");
      }
    }

    // Process the KEY REL command.
    else if (command == "6")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL1, START_ROW4, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL1 + TEXT_LONG, START_ROW4 + TEXT_CENTERED);
        tft.println("KEY REL");
      }
      else
      {
        tft.fillRect(START_COL1, START_ROW4, BOX_WIDTH, BOX_HEIGHT, WHITE);
    
        tft.setCursor(START_COL1 + TEXT_LONG, START_ROW4 + TEXT_CENTERED);
        tft.println("KEY REL");
      }
    }

    // Process the OPR ERR command.
    else if (command == "7")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL1, START_ROW5, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL1 + TEXT_LONG, START_ROW5 + TEXT_CENTERED);
        tft.println("OPR ERR");
      }
      else
      {
        tft.fillRect(START_COL1, START_ROW5, BOX_WIDTH, BOX_HEIGHT, WHITE);
    
        tft.setCursor(START_COL1 + TEXT_LONG, START_ROW5 + TEXT_CENTERED);
        tft.println("OPR ERR");
      }
    }

    // Process the TEMP command.
    else if (command == "8")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL2, START_ROW1, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setTextColor(BLACK);
        tft.setTextSize(2);
        tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW1 + TEXT_CENTERED);
        tft.println("TEMP");
      }
      else
      {
        tft.fillRect(START_COL2, START_ROW1, BOX_WIDTH, BOX_HEIGHT, YELLOW);
    
        tft.setTextColor(BLACK);
        tft.setTextSize(2);
        tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW1 + TEXT_CENTERED);
        tft.println("TEMP");
      }
    }

    // Process the GIMBAL LOCK command.
    else if (command == "9")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL2, START_ROW2, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL2 + TEXT_LONG, START_ROW2 + TEXT_UPPER_ROW);
        tft.println("GIMBAL");
    
        tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW2 + TEXT_LOWER_ROW);
        tft.println("LOCK");
      }
      else
      {
        tft.fillRect(START_COL2, START_ROW2, BOX_WIDTH, BOX_HEIGHT, YELLOW);
    
        tft.setCursor(START_COL2 + TEXT_LONG, START_ROW2 + TEXT_UPPER_ROW);
        tft.println("GIMBAL");
    
        tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW2 + TEXT_LOWER_ROW);
        tft.println("LOCK");
      }
    }

    // Process the PROG command.
    else if (command == "10")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL2, START_ROW3, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW3 + TEXT_CENTERED);
        tft.println("PROG");
      }
      else
      {
        tft.fillRect(START_COL2, START_ROW3, BOX_WIDTH, BOX_HEIGHT, YELLOW);
    
        tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW3 + TEXT_CENTERED);
        tft.println("PROG");
      }
    }

    // Process the RESTART command.
    else if (command == "11")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL2, START_ROW4, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL2 + TEXT_XLONG, START_ROW4 + TEXT_CENTERED);
        tft.println("RESTART");
      }
      else
      {
        tft.fillRect(START_COL2, START_ROW4, BOX_WIDTH, BOX_HEIGHT, YELLOW);
    
        tft.setCursor(START_COL2 + TEXT_XLONG, START_ROW4 + TEXT_CENTERED);
        tft.println("RESTART");
      }
    }

    // Process the TRACKER command.
    else if (command == "12")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL2, START_ROW5, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL2 + TEXT_XLONG, START_ROW5 + TEXT_CENTERED);
        tft.println("TRACKER");
      }
      else
      {
        tft.fillRect(START_COL2, START_ROW5, BOX_WIDTH, BOX_HEIGHT, YELLOW);
    
        tft.setCursor(START_COL2 + TEXT_XLONG, START_ROW5 + TEXT_CENTERED);
        tft.println("TRACKER");
      }
    }

    // Process the ALT command.
    else if (command == "13")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL2, START_ROW6, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL2 + TEXT_SHORT, START_ROW6 + TEXT_CENTERED);
        tft.println("ALT");
      }
      else
      {
        tft.fillRect(START_COL2, START_ROW6, BOX_WIDTH, BOX_HEIGHT, YELLOW);
    
        tft.setCursor(START_COL2 + TEXT_SHORT, START_ROW6 + TEXT_CENTERED);
        tft.println("ALT");
      }
    }

    // Process the VEL command.
    else if (command == "14")
    {
      if (data == "0")
      {
        tft.fillRect(START_COL2, START_ROW7, BOX_WIDTH, BOX_HEIGHT, GREY);
    
        tft.setCursor(START_COL2 + TEXT_SHORT, START_ROW7 + TEXT_CENTERED);
        tft.println("VEL");
      }
      else
      {
        tft.fillRect(START_COL2, START_ROW7, BOX_WIDTH, BOX_HEIGHT, YELLOW);
    
        tft.setCursor(START_COL2 + TEXT_SHORT, START_ROW7 + TEXT_CENTERED);
        tft.println("VEL");
      }
    }
    commandString = "";
  }
}

  /*
   * Reset the display to a power on state.
   */
  void resetDisplay()
  {
    // First draw the rectangles.
    tft.fillScreen(HX8357_BLACK);
    tft.fillRect(START_COL1, START_ROW1, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL1, START_ROW2, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL1, START_ROW3, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL1, START_ROW4, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL1, START_ROW5, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL1, START_ROW6, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL1, START_ROW7, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL2, START_ROW1, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL2, START_ROW2, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL2, START_ROW3, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL2, START_ROW4, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL2, START_ROW5, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL2, START_ROW6, BOX_WIDTH, BOX_HEIGHT, GREY);
    tft.fillRect(START_COL2, START_ROW7, BOX_WIDTH, BOX_HEIGHT, GREY);

    // Now set the text on top of the rectangles.
    tft.setTextColor(BLACK);
    tft.setTextSize(2);
    
    tft.setCursor(START_COL1 + TEXT_LONG, START_ROW1 + TEXT_UPPER_ROW);
    tft.println("UPLINK");
 
    tft.setCursor(START_COL1 + TEXT_MEDIUM, START_ROW1 + TEXT_LOWER_ROW);
    tft.println("ACTY");
    
    tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW1 + TEXT_CENTERED);
    tft.println("TEMP");
    
    tft.setCursor(START_COL1 + TEXT_LONG, START_ROW2 + TEXT_CENTERED);
    tft.println("NO ATT");
    
    tft.setCursor(START_COL2 + TEXT_LONG, START_ROW2 + TEXT_UPPER_ROW);
    tft.println("GIMBAL");
    
    tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW2 + TEXT_LOWER_ROW);
    tft.println("LOCK");
    
    tft.setCursor(START_COL1 + TEXT_MEDIUM, START_ROW3 + TEXT_CENTERED);
    tft.println("STBY");
    
    tft.setCursor(START_COL2 + TEXT_MEDIUM, START_ROW3 + TEXT_CENTERED);
    tft.println("PROG");
    
    tft.setCursor(START_COL1 + TEXT_LONG, START_ROW4 + TEXT_CENTERED);
    tft.println("KEY REL");
    
    tft.setCursor(START_COL2 + TEXT_XLONG, START_ROW4 + TEXT_CENTERED);
    tft.println("RESTART");
    
    tft.setCursor(START_COL1 + TEXT_LONG, START_ROW5 + TEXT_CENTERED);
    tft.println("OPR ERR");
    
    tft.setCursor(START_COL2 + TEXT_XLONG, START_ROW5 + TEXT_CENTERED);
    tft.println("TRACKER");
    
    tft.setCursor(START_COL2 + TEXT_SHORT, START_ROW6 + TEXT_CENTERED);
    tft.println("ALT");
    
    tft.setCursor(START_COL2 + TEXT_SHORT, START_ROW7 + TEXT_CENTERED);
    tft.println("VEL");
  }
