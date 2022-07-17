/*
 * Copyright 2022, William Glasford
 *
 *  DSKY:
 *
 *  This is the Arduino code to run the 3.5" LCD that displays the main display of the DSKY.  
 *  This slave processor deals with the display minutia and does not store any state information.
 *  The code waits for a command from the higher level Raspberry Pi and executes the command.
 *  The display is 320 x 480 pixels in size.
 *
 * Mods:     07/04/22  Initial Release.
 *           07/16/22  Performance improvements.
 */
#include <SPI.h>
#include "Adafruit_GFX.h"
#include "Adafruit_HX8357.h"

// These are 'flexible' lines that can be changed
#define TFT_CS 8
#define TFT_DC 9
#define TFT_RST -1 // RST can be set to -1 if you tie it to Arduino's reset

#define BLACK 0x0000
#define GREEN 0x07E0 
#define WHITE 0xFFFF 
#define GREY 0x3186

#define CA_WIDTH 100
#define CA_HEIGHT 100
#define CA_COL 10
#define CA_ROW 0
#define CA_TEXT_X 38
#define CA_TEXT_Y1 30
#define CA_TEXT_Y2 50

#define PG_WIDTH 100
#define PG_HEIGHT 22
#define PG_COL 200
#define PG_ROW 0
#define PG_TEXT_X 225
#define PG_TEXT_Y 5

#define VB_WIDTH 100
#define VB_HEIGHT 22
#define VB_COL 10
#define VB_ROW 110
#define VB_TEXT_X 35
#define VB_TEXT_Y 113

#define NO_WIDTH 100
#define NO_HEIGHT 22
#define NO_COL 200
#define NO_ROW 110
#define NO_TEXT_X 225
#define NO_TEXT_Y 113

#define LINE_WIDTH 290
#define LINE_HEIGHT 7
#define LINE_COL 15
#define LINE_1_ROW 217
#define LINE_2_ROW 307
#define LINE_3_ROW 397

#define SIGN_WIDTH 30
#define SIGN_HEIGHT 10
#define R1S_ROW 261
#define R1S_COL 0
#define R2S_ROW 351
#define R2S_COL 0
#define R3S_ROW 441
#define R3S_COL 0

#define CHAR_WIDTH 45
#define CHAR_HEIGHT 70
#define CHAR_OFFSET 15
#define CHAR_BAR_WIDTH 8
#define CHAR_VOID_WIDTH 2

#define MD1_ROW 30
#define MD1_COL 195
#define MD2_ROW 30
#define MD2_COL 250

#define VD1_ROW 140
#define VD1_COL 0
#define VD2_ROW 140
#define VD2_COL 55

#define ND1_ROW 140
#define ND1_COL 195
#define ND2_ROW 140
#define ND2_COL 250

#define R1D1_ROW 230
#define R1D1_COL 30
#define R1D2_ROW 230
#define R1D2_COL 85
#define R1D3_ROW 230
#define R1D3_COL 140
#define R1D4_ROW 230
#define R1D4_COL 195
#define R1D5_ROW 230
#define R1D5_COL 250

#define R2D1_ROW 320
#define R2D1_COL 30
#define R2D2_ROW 320
#define R2D2_COL 85
#define R2D3_ROW 320
#define R2D3_COL 140
#define R2D4_ROW 320
#define R2D4_COL 195
#define R2D5_ROW 320
#define R2D5_COL 250

#define R3D1_ROW 410
#define R3D1_COL 30
#define R3D2_ROW 410
#define R3D2_COL 85
#define R3D3_ROW 410
#define R3D3_COL 140
#define R3D4_ROW 410
#define R3D4_COL 195
#define R3D5_ROW 410
#define R3D5_COL 250

#define R1S_PLUS  10
#define R1S_MINUS 11
#define R2S_PLUS  20
#define R2S_MINUS 21
#define R3S_PLUS  30
#define R3S_MINUS 31

// Use hardware SPI (on Uno, #13, #12, #11) and the above for CS/DC
Adafruit_HX8357 tft = Adafruit_HX8357(TFT_CS, TFT_DC, TFT_RST);

bool r1sPlus = false;
bool r1sMinus = false;
bool r2sPlus = false;
bool r2sMinus = false;
bool r3sPlus = false;
bool r3sMinus = false;

int vd1 = 0;
int vd2 = 0;
int nd1 = 0;
int nd2 = 0;

void setup() 
{
  String commandString;
  
  Serial.begin(9600);

  // Set up LCD
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
      Serial.println("Display");
      initialized = true;
    }
    commandString = "";
  }
}

/*
 * This is the main Arduino loop that runs continuously.  It waits for a command from the 
 * Raspberry Pi, then executes the command before waiting for the next command.  Each 
 * command is followed by a number representing the 15-bit channel value.  The COMP ACTY 
 * is followed by 0 = off or 1 = on.  
 * The commands are:
 * 
 * 0  = Noop
 * 1  = Reset to power on state
 * 2  = Identify
 * 3  = Display Command
 * 4 = COMP ACTY
 * 5 = Flash Verb/Noun
 */
void loop() 
{
  String commandString;
  
  // Read the next command sequence.
  while (Serial.available()) 
  {
    delay(2);  // Delay 2 milliseconds to allow byte to arrive in input buffer
    char c = Serial.read();  // Gets one byte from serial buffer
    
    if (c != '\n')  
      commandString += c; 
  }

  // If there is a command string to parse...
  if (commandString.length() > 0) 
  {
    String command = commandString;
    String data = "";
    String delim = " ";
    int number;
    byte dspl;
    byte dsph;
    byte rlwd;
    bool signValue = false;

    // Parse the command string.
    if (commandString != "0" && commandString != "1" && commandString != "2")
    {
      int firstDelimIndex = commandString.indexOf(delim);
      command = commandString.substring(0, firstDelimIndex);
      data = commandString.substring(firstDelimIndex + 1);
      int intValue = data.toInt();
      Serial.println(intValue);
      rlwd = (intValue >> 11) & 0x000F;
      dspl = intValue & 0x001F;
      dsph = (intValue >> 5) & 0x001F;
      signValue = (intValue >> 10) & 0x0001;
     }
    
    // Process the Reset command.
    if (command == "1")
    {
      resetDisplay();
    }

    // Process the MD1/MD2 command.
    else if (command == "3")
    {
      if (rlwd == 11)
      {
        displayCharacter(dsph, MD1_COL, MD1_ROW);
        displayCharacter(dspl, MD2_COL, MD2_ROW);
      }
      else if (rlwd == 10)
      {
        vd1 = dspl;
        vd2 = dsph;
        displayCharacter(dsph, VD1_COL, VD1_ROW);
        displayCharacter(dspl, VD2_COL, VD2_ROW);
      }
      else if (rlwd == 9)
      {
        nd1 = dspl;
        nd2 = dsph;
        displayCharacter(dsph, ND1_COL, ND1_ROW);
        displayCharacter(dspl, ND2_COL, ND2_ROW);
      }
      else if (rlwd == 8)
      {
        displayCharacter(dspl, R1D1_COL, R1D1_ROW);
      }
      else if (rlwd == 7)
      {
        if (signValue)
        {
          r1sPlus = true;
          if (!r1sMinus)
          {
            displayPlus(R1S_COL, R1S_ROW);
          }
        }
        else
        {
          r1sPlus = false;
          if (!r1sMinus)
          {
            displayBlank(R1S_COL, R1S_ROW);
          }
        }
        displayCharacter(dsph, R1D2_COL, R1D2_ROW);
        displayCharacter(dspl, R1D3_COL, R1D3_ROW);
      }
      else if (rlwd == 6)
      {
        if (signValue)
        {
          r1sMinus = true;
          if (!r1sPlus)
          {
            displayMinus(R1S_COL, R1S_ROW);
          }
        }
        else
        {
          r1sMinus = false;
          if (!r1sPlus)
          {
            displayBlank(R1S_COL, R1S_ROW);
          }
        }
        displayCharacter(dsph, R1D4_COL, R1D4_ROW);
        displayCharacter(dspl, R1D5_COL, R1D5_ROW);
      }
      else if (rlwd == 5)
      {
        if (signValue)
        {
          r2sPlus = true;
          if (!r2sMinus)
          {
            displayPlus(R2S_COL, R2S_ROW);
          }
        }
        else
        {
          r2sPlus = false;
          if (!r2sMinus)
          {
            displayBlank(R2S_COL, R2S_ROW);
          }
        }
        displayCharacter(dsph, R2D1_COL, R2D1_ROW);
        displayCharacter(dspl, R2D2_COL, R2D2_ROW);
      }
      else if (rlwd == 4)
      {
        if (signValue)
        {
          r2sMinus = true;
          if (!r2sPlus)
          {
            displayMinus(R2S_COL, R2S_ROW);
          }
        }
        else
        {
          r2sMinus = false;
          if (!r2sPlus)
          {
            displayBlank(R2S_COL, R2S_ROW);
          }
        }
        displayCharacter(dsph, R2D3_COL, R2D3_ROW);
        displayCharacter(dspl, R2D4_COL, R2D4_ROW);
      }
      else if (rlwd == 3)
      {
        displayCharacter(dsph, R2D5_COL, R2D5_ROW);
        displayCharacter(dspl, R3D1_COL, R3D1_ROW);
      }
      else if (rlwd == 2)
      {
        if (signValue)
        {
          r3sPlus = true;
          if (!r3sMinus)
          {
            displayPlus(R3S_COL, R3S_ROW);
          }
        }
        else
        {
          r3sPlus = false;
          if (!r3sMinus)
          {
            displayBlank(R3S_COL, R3S_ROW);
          }
        }
        displayCharacter(dsph, R3D2_COL, R3D2_ROW);
        displayCharacter(dspl, R3D3_COL, R3D3_ROW);
      }
      else if (rlwd == 1)
      {
        if (signValue)
        {
          r3sMinus = true;
          if (!r3sPlus)
          {
            displayMinus(R3S_COL, R3S_ROW);
          }
        }
        else
        {
          r3sMinus = false;
          if (!r3sPlus)
          {
            displayBlank(R3S_COL, R3S_ROW);
          }
        }
        displayCharacter(dsph, R3D4_COL, R3D4_ROW);
        displayCharacter(dspl, R3D5_COL, R3D5_ROW);
      }
    }

    // Process the COMP ACTY command.
    else if (command == "4")
    {
      if (data == "0")
      {
        displayCompActy(false);
      }
      else
      {
        displayCompActy(true);
      }
    }

    // Process Flash Verb/Noun command.
    else if (command = "5")
    {
      if (data == "0")
      {
        displayCharacter(0, VD1_COL, VD1_ROW);
        displayCharacter(0, VD2_COL, VD2_ROW);
        displayCharacter(0, ND1_COL, ND1_ROW);
        displayCharacter(0, ND2_COL, ND2_ROW);
      }
      else 
      {
        displayCharacter(vd1, VD1_COL, VD1_ROW);
        displayCharacter(vd2, VD2_COL, VD2_ROW);
        displayCharacter(nd1, ND1_COL, ND1_ROW);
        displayCharacter(nd2, ND2_COL, ND2_ROW);
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
  tft.fillScreen(BLACK);  
  tft.setTextColor(BLACK);
  tft.setTextSize(2);

  // Display PROG 
  tft.fillRect(PG_COL, PG_ROW, PG_WIDTH, PG_HEIGHT, GREEN);
  
  tft.setTextColor(BLACK);
  tft.setTextSize(2);
    
  tft.setCursor(PG_TEXT_X, PG_TEXT_Y);
  tft.println("PROG");

  // Display VERB 
  tft.fillRect(VB_COL, VB_ROW, VB_WIDTH, VB_HEIGHT, GREEN);
  
  tft.setTextColor(BLACK);
  tft.setTextSize(2);
    
  tft.setCursor(VB_TEXT_X, VB_TEXT_Y);
  tft.println("VERB");

  // Display NOUN 
  tft.fillRect(NO_COL, NO_ROW, PG_WIDTH, NO_HEIGHT, GREEN);
  
  tft.setTextColor(BLACK);
  tft.setTextSize(2);
    
  tft.setCursor(NO_TEXT_X, NO_TEXT_Y);
  tft.println("NOUN");

  // Display Lines 
  tft.fillRect(LINE_COL, LINE_1_ROW, LINE_WIDTH, LINE_HEIGHT, GREEN);
  tft.fillRect(LINE_COL, LINE_2_ROW, LINE_WIDTH, LINE_HEIGHT, GREEN);
  tft.fillRect(LINE_COL, LINE_3_ROW, LINE_WIDTH, LINE_HEIGHT, GREEN);

  displayCompActy(false);
}

/*
 * Turn on/off the Computer Activity light.  
 */
void displayCompActy(bool state)
{
  if (state)
  {
    tft.fillRect(CA_COL, CA_ROW, CA_WIDTH, CA_HEIGHT, GREEN);
  }
  else
  {
    tft.fillRect(CA_COL, CA_ROW, CA_WIDTH, CA_HEIGHT, GREY);
  }
    
  tft.setCursor(CA_TEXT_X, CA_TEXT_Y1);
  tft.println("COMP");
 
  tft.setCursor(CA_TEXT_X, CA_TEXT_Y2);
  tft.println("ACTY");
}

/*
 * Display the character based on home position (upper left corner).  There are two triangles used
 * to draw each segment.  The first one draws from UL, LL & LR.  The second one draws from UL, UR & LR.
 */
 void displayCharacter(byte number, int x, int y)
 {
  // Display based on character to display.
  if (number == 0x00) // Blank
  {
    drawSegmentA(x, y, BLACK);
    drawSegmentB(x, y, BLACK);
    drawSegmentC(x, y, BLACK);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, BLACK);
  }
  else if (number == 0x15) // 0
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, GREEN);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, BLACK);
  }
  else if (number == 0x03) // 1
  {
    drawSegmentA(x, y, BLACK);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, BLACK);
  }
  else if (number == 0x19) // 2
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, BLACK);
    drawSegmentE(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, GREEN);
  }
  else if (number == 0x1B) // 3
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, GREEN);
  }
  else if (number == 0x0F) // 4
  {
    drawSegmentA(x, y, BLACK);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else if (number == 0x1E) // 5
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, BLACK);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else if (number == 0x1C) // 6
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, BLACK);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, GREEN);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else if (number == 0x13) // 7
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, BLACK);
  }
  else if (number == 0x1D) // 8
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, GREEN);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else if (number == 0x1F) // 9
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else
  {
    displayCompActy(true);
  }
}

/*
 * Draw the A segment of the 7-segment LED.
 */
void drawSegmentA (int x, int y, int color)
{
  tft.fillTriangle (x + CHAR_OFFSET + 1, y, 
                    x + CHAR_OFFSET + CHAR_BAR_WIDTH, y + CHAR_BAR_WIDTH, 
                    x + CHAR_OFFSET + CHAR_WIDTH - CHAR_BAR_WIDTH - 2, y + CHAR_BAR_WIDTH, color);
 
  tft.fillTriangle (x + CHAR_OFFSET + 1, y,
                    x + CHAR_OFFSET + CHAR_WIDTH - CHAR_BAR_WIDTH - 2, y, 
                    x + CHAR_OFFSET + CHAR_WIDTH - CHAR_BAR_WIDTH - 4, y + CHAR_BAR_WIDTH, color); 
}

/*
 * Draw the B segment of the 7-segment LED.
 */
void drawSegmentB (int x, int y, int color)
{
  tft.fillTriangle (x + CHAR_OFFSET + CHAR_WIDTH - CHAR_BAR_WIDTH, y, 
                    x + CHAR_WIDTH, y + 34, 
                    x + CHAR_WIDTH + CHAR_BAR_WIDTH, y + 34, color);
 
  tft.fillTriangle (x + CHAR_OFFSET + CHAR_WIDTH - CHAR_BAR_WIDTH, y,
                    x + CHAR_OFFSET + CHAR_WIDTH, y, 
                    x + CHAR_WIDTH + CHAR_BAR_WIDTH, y + 34, color);
}

/*
 * Draw the C segment of the 7-segment LED.
 */
void drawSegmentC (int x, int y, int color)
{
  tft.fillTriangle (x + CHAR_OFFSET + CHAR_WIDTH - CHAR_BAR_WIDTH - 8, y + 36, 
                    x + CHAR_WIDTH - CHAR_BAR_WIDTH + 2, y + CHAR_HEIGHT - CHAR_BAR_WIDTH - 2, 
                    x + CHAR_WIDTH, y + CHAR_HEIGHT, color);
 
  tft.fillTriangle (x + CHAR_OFFSET + CHAR_WIDTH - CHAR_BAR_WIDTH - 8, y + 36,
                    x + CHAR_OFFSET + CHAR_WIDTH - 7, y + 36, 
                    x + CHAR_WIDTH, y + CHAR_HEIGHT, color);
}

/*
 * Draw the D segment of the 7-segment LED.
 */
void drawSegmentD (int x, int y, int color)
{
  tft.fillTriangle (x + CHAR_BAR_WIDTH + 4, y + CHAR_HEIGHT - CHAR_BAR_WIDTH, 
                    x + 2, y + CHAR_HEIGHT, 
                    x + CHAR_WIDTH - 2, y + CHAR_HEIGHT, color);
 
  tft.fillTriangle (x + CHAR_BAR_WIDTH + 4, y + CHAR_HEIGHT - CHAR_BAR_WIDTH,
                    x + CHAR_WIDTH - CHAR_BAR_WIDTH, y + CHAR_HEIGHT - CHAR_BAR_WIDTH, 
                    x + CHAR_WIDTH - 2, y + CHAR_HEIGHT, color);
}

/*
 * Draw the E segment of the 7-segment LED.
 */
void drawSegmentE (int x, int y, int color)
{
  tft.fillTriangle (x + 7, y + 36, 
                    x, y + CHAR_HEIGHT - 1, 
                    x + CHAR_BAR_WIDTH + 1, y + CHAR_HEIGHT - CHAR_BAR_WIDTH, color);
 
  tft.fillTriangle (x + 7, y + 36,
                    x + CHAR_BAR_WIDTH + 7, y + 36, 
                    x + CHAR_BAR_WIDTH + 1, y + CHAR_HEIGHT - CHAR_BAR_WIDTH, color);
}

/*
 * Draw the F segment of the 7-segment LED.
 */
void drawSegmentF (int x, int y, int color)
{
  tft.fillTriangle (x + CHAR_OFFSET - 1, y, 
                    x + 7, y + 34, 
                    x + CHAR_BAR_WIDTH + 7, y + 34, color);
 
  tft.fillTriangle (x + CHAR_OFFSET - 1, y,
                    x + CHAR_OFFSET + CHAR_BAR_WIDTH - 2, y + CHAR_BAR_WIDTH + 2, 
                    x + CHAR_BAR_WIDTH + 7, y + 34, color);
}

/*
 * Draw the G segment of the 7-segment LED.
 */
void drawSegmentG (int x, int y, int color)
{
  tft.fillTriangle (x + CHAR_OFFSET + CHAR_BAR_WIDTH - 4, y + 31, 
                    x + CHAR_OFFSET + CHAR_BAR_WIDTH - 6, y + 39, 
                    x + CHAR_WIDTH - CHAR_BAR_WIDTH + 4, y + 39, color);
 
  tft.fillTriangle (x + CHAR_OFFSET + CHAR_BAR_WIDTH - 4, y + 31,
                    x + CHAR_WIDTH - CHAR_BAR_WIDTH + 6, y + 31, 
                    x + CHAR_WIDTH - CHAR_BAR_WIDTH + 4, y + 39, color);
}

/*
 * Display the plus sign.
 */
void displayPlus (int x, int y)
{
  tft.fillRect(x, y, SIGN_WIDTH, SIGN_HEIGHT, GREEN);
  tft.fillRect(x + 11, y - 17, 8, 14, GREEN);
  tft.fillRect(x + 11, y + 13, 8, 14, GREEN);
}

/*
 * Display the minus sign.
 */
void displayMinus (int x, int y)
{
  tft.fillRect(x, y, SIGN_WIDTH, SIGN_HEIGHT, GREEN);
  tft.fillRect(x + 11, y - 17, 8, 14, BLACK);
  tft.fillRect(x + 11, y + 13, 8, 14, BLACK);
}

/*
 * Blank the sign.
 */
void displayBlank (int x, int y)
{
  tft.fillRect(x, y, SIGN_WIDTH, SIGN_HEIGHT, BLACK);
  tft.fillRect(x + 11, y - 17, 8, 14, BLACK);
  tft.fillRect(x + 11, y + 13, 8, 14, BLACK);
}
