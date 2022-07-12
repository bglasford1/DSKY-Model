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
 *  
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

// Use hardware SPI (on Uno, #13, #12, #11) and the above for CS/DC
Adafruit_HX8357 tft = Adafruit_HX8357(TFT_CS, TFT_DC, TFT_RST);

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
 * Raspberry Pi, then executes the command before waiting for the next command.  Each of the
 * commands are followed by a single digit numeric value except for the three sign commands.  
 * The sign command values are 0 = blank, 1 = minus and 2 = plus.  The COMP ACTY is followed 
 * by 0 = off or 1 = on.  The remaining commands are followed by 0-9 or B for blank.  
 * The commands are:
 * 
 * 0  = Noop
 * 1  = Reset to power on state
 * 2  = Identify
 * 3  = MD1 value
 * 4  = MD2 value
 * 5  = VD1 value
 * 6  = VD2 value
 * 7  = ND1 value
 * 8  = ND2 value
 * 9  = R1S
 * 10 = R1D1
 * 11 = R1D2
 * 12 = R1D3
 * 13 = R1D4
 * 14 = R1D5
 * 15 = R2S
 * 16 = R2D1
 * 17 = R2D2
 * 18 = R2D3
 * 19 = R2D4
 * 20 = R2D5
 * 21 = R3S
 * 22 = R3D1
 * 23 = R3D2
 * 24 = R3D3
 * 25 = R3D4
 * 26 = R3D5
 * 27 = COMP ACTY
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

    // Parse the command string.
    if (commandString != "0" && commandString != "1" && commandString != "2")
    {
      int firstDelimIndex = commandString.indexOf(delim);
      command = commandString.substring(0, firstDelimIndex);
      data = commandString.substring(firstDelimIndex + 1);
     }
    
    // Process the Reset command.
    if (command == "1")
    {
      resetDisplay();
    }

    // Process the MD1 command.
    else if (command == "3")
    {
      displayCharacter(data, MD1_COL, MD1_ROW);
    }

    // Process the MD2 command.
    else if (command == "4")
    {
      displayCharacter(data, MD2_COL, MD2_ROW);
    }

    // Process the VD1 command.
    else if (command == "5")
    {
      displayCharacter(data, VD1_COL, VD1_ROW);
    }

    // Process the VD2 command.
    else if (command == "6")
    {
      displayCharacter(data, VD2_COL, VD2_ROW);
    }

    // Process the ND1 command.
    else if (command == "7")
    {
      displayCharacter(data, ND1_COL, ND1_ROW);
    }

    // Process the ND2 command.
    else if (command == "8")
    {
      displayCharacter(data, ND2_COL, ND2_ROW);
    }

    // Process the R1S command.
    else if (command == "9")
    {
      displaySign(data, R1S_COL, R1S_ROW);
    }

    // Process the R1D1 command.
    else if (command == "10")
    {
      displayCharacter(data, R1D1_COL, R1D1_ROW);
    }

    // Process the R1D2 command.
    else if (command == "11")
    {
      displayCharacter(data, R1D2_COL, R1D2_ROW);
    }

    // Process the R1D3 command.
    else if (command == "12")
    {
      displayCharacter(data, R1D3_COL, R1D3_ROW);
    }

    // Process the R1D4 command.
    else if (command == "13")
    {
      displayCharacter(data, R1D4_COL, R1D4_ROW);
    }

    // Process the R1D5 command.
    else if (command == "14")
    {
      displayCharacter(data, R1D5_COL, R1D5_ROW);
    }

    // Process the R2S command.
    else if (command == "15")
    {
     displaySign(data, R2S_COL, R2S_ROW);
     }

    // Process the R2D1 command.
    else if (command == "16")
    {
      displayCharacter(data, R2D1_COL, R2D1_ROW);
    }

    // Process the R2D2 command.
    else if (command == "17")
    {
      displayCharacter(data, R2D2_COL, R2D2_ROW);
    }

    // Process the R2D3 command.
    else if (command == "18")
    {
      displayCharacter(data, R2D3_COL, R2D3_ROW);
    }

    // Process the R2D4 command.
    else if (command == "19")
    {
      displayCharacter(data, R2D4_COL, R2D4_ROW);
    }

    // Process the R2D5 command.
    else if (command == "20")
    {
      displayCharacter(data, R2D5_COL, R2D5_ROW);
    }

    // Process the R3S command.
    else if (command == "21")
    {
     displaySign(data, R3S_COL, R3S_ROW);
     }

    // Process the R3D1 command.
    else if (command == "22")
    {
      displayCharacter(data, R3D1_COL, R3D1_ROW);
    }

    // Process the R3D2 command.
    else if (command == "23")
    {
      displayCharacter(data, R3D2_COL, R3D2_ROW);
    }

    // Process the R3D3 command.
    else if (command == "24")
    {
      displayCharacter(data, R3D3_COL, R3D3_ROW);
    }

    // Process the R3D4 command.
    else if (command == "25")
    {
      displayCharacter(data, R3D4_COL, R3D4_ROW);
    }

    // Process the R3D5 command.
    else if (command == "26")
    {
      displayCharacter(data, R3D5_COL, R3D5_ROW);
    }

    // Process the COMP ACTY command.
    else if (command == "27")
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
 void displayCharacter(String number, int x, int y)
 {
  // Display based on character to display.
  if (number.equals("B"))
  {
    drawSegmentA(x, y, BLACK);
    drawSegmentB(x, y, BLACK);
    drawSegmentC(x, y, BLACK);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, BLACK);
  }
  else if (number.equals("0"))
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, GREEN);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, BLACK);
  }
  else if (number.equals("1"))
  {
    drawSegmentA(x, y, BLACK);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, BLACK);
  }
  else if (number.equals("2"))
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, BLACK);
    drawSegmentE(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, GREEN);
  }
  else if (number.equals("3"))
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, GREEN);
  }
  else if (number.equals("4"))
  {
    drawSegmentA(x, y, BLACK);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else if (number.equals("5"))
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, BLACK);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else if (number.equals("6"))
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, BLACK);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, GREEN);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else if (number.equals("7"))
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, BLACK);
    drawSegmentE(x, y, BLACK);
    drawSegmentF(x, y, BLACK);
    drawSegmentG(x, y, BLACK);
  }
  else if (number.equals("8"))
  {
    drawSegmentA(x, y, GREEN);
    drawSegmentB(x, y, GREEN);
    drawSegmentC(x, y, GREEN);
    drawSegmentD(x, y, GREEN);
    drawSegmentE(x, y, GREEN);
    drawSegmentF(x, y, GREEN);
    drawSegmentG(x, y, GREEN);
  }
  else if (number.equals("9"))
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
 * Display the sign based on home position (upper left corner).  Sign values: 0 = blank,
 * 1 = minus, 2 = plus.
 */
void displaySign (String sign, int x, int y)
{
  if (sign.equals("2"))
  {
    displayPlus(x, y);
  }
  else if (sign.equals("1"))
  {
    displayMinus(x, y);
  }
  else if (sign.equals("0"))
  {
    displayBlank(x, y);
  }
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
