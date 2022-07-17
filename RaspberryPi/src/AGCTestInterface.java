/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class provides a test version of the AGCInterface so that the rest of the software can be
            tested without the presence of an AGC.

  Mods:		  07/15/22  Initial Release.
*/
import java.util.BitSet;

public class AGCTestInterface
{
  private final DisplayInterface displayInterface = DisplayInterface.getInstance();
  private final IndicatorInterface indicatorInterface = IndicatorInterface.getInstance();
  private final KeyboardInterface keyboardInterface = KeyboardInterface.getInstance();

  /**
   * This method first sets all numbers to "8", then exercises all the sign characters,
   * then sets R1 to 0 and counts up to 9, then toggles on/off all the indicators, then
   * resets the displays and finally sets the indicators and display to the picture I
   * captured off the web.
   */
  public void init()
  {
    long startTime = System.currentTimeMillis();

    // ---------------- Set all characters to "8" -------------------
    // MD1/MD2
    displayInterface.setChannel10Register(0x5BBD); // 101 1011 1011 1101
    displayInterface.decodeData();

    // VD1/VD2
    displayInterface.setChannel10Register(0x53BD); // 101 0011 1011 1101
    displayInterface.decodeData();

    // ND1/ND2
    displayInterface.setChannel10Register(0x4BBD); // 100 1011 1011 1101
    displayInterface.decodeData();

    // R1D1
    displayInterface.setChannel10Register(0x43BD); // 100 0011 1011 1101
    displayInterface.decodeData();

    // R1D2/R1D3/+R1S
    displayInterface.setChannel10Register(0x3BBD); // 011 1011 1011 1101
    displayInterface.decodeData();

    // R1D4/R1D5/-R1S
    displayInterface.setChannel10Register(0x33BD); // 011 0011 1011 1101
    displayInterface.decodeData();

    // R2D1/R2D2/+R2S
    displayInterface.setChannel10Register(0x2BBD); // 010 1011 1011 1101
    displayInterface.decodeData();

    // R2D3/R2D4/-R2S
    displayInterface.setChannel10Register(0x23BD); // 010 0011 1011 1101
    displayInterface.decodeData();

    // R2D5/R3D1
    displayInterface.setChannel10Register(0x1BBD); // 001 1011 1011 1101
    displayInterface.decodeData();

    // R3D2/R3D3/+R3S
    displayInterface.setChannel10Register(0x13BD); // 001 0011 1011 1101
    displayInterface.decodeData();

    // R3D4/R3D5/-R3S
    displayInterface.setChannel10Register(0x0BBD); // 000 1011 1011 1101
    displayInterface.decodeData();

    long endTime = System.currentTimeMillis();
    System.out.println("Time to Draw Display = " + (endTime - startTime));

    // -------------- Put the +/- signs through their paces -----------------
    // R1D2/R1D3/+R1S
    displayInterface.setChannel10Register(0x3FBD); // 011 1111 1011 1101
    displayInterface.decodeData();

    // R2D1/R2D2/+R2S
    displayInterface.setChannel10Register(0x2FBD); // 010 1111 1011 1101
    displayInterface.decodeData();

    // R3D2/R3D3/+R3S
    displayInterface.setChannel10Register(0x17BD); // 001 0111 1011 1101
    displayInterface.decodeData();

    // R1D2/R1D3/+R1S
    displayInterface.setChannel10Register(0x3BBD); // 011 1011 1011 1101
    displayInterface.decodeData();

    // R2D1/R2D2/+R2S
    displayInterface.setChannel10Register(0x2BBD); // 010 1011 1011 1101
    displayInterface.decodeData();

    // R3D2/R3D3/+R3S
    displayInterface.setChannel10Register(0x13BD); // 001 0011 1011 1101
    displayInterface.decodeData();

    // R1D4/R1D5/-R1S
    displayInterface.setChannel10Register(0x37BD); // 011 0111 1011 1101
    displayInterface.decodeData();

    // R2D3/R2D4/-R2S
    displayInterface.setChannel10Register(0x27BD); // 010 0111 1011 1101
    displayInterface.decodeData();

    // R3D4/R3D5/-R3S
    displayInterface.setChannel10Register(0x0FBD); // 000 1111 1011 1101
    displayInterface.decodeData();

    // R1D4/R1D5/-R1S
    displayInterface.setChannel10Register(0x33BD); // 011 0011 1011 1101
    displayInterface.decodeData();

    // R2D3/R2D4/-R2S
    displayInterface.setChannel10Register(0x23BD); // 010 0011 1011 1101
    displayInterface.decodeData();

    // R3D4/R3D5/-R3S
    displayInterface.setChannel10Register(0x0BBD); // 000 1011 1011 1101
    displayInterface.decodeData();

//      // ---------------- Make R1 count from 0-9 -------------------
//      // Set R1 to all zeros.
//      // R1D1
//      displayInterface.setChannel10Register(0x42B5); // 100 0010 1011 0101
//      displayInterface.decodeData();
//
//      // R1D2/R1D3
//      displayInterface.setChannel10Register(0x3AB5); // 011 1010 1011 0101
//      displayInterface.decodeData();
//
//      // R1D4/R1D5
//      displayInterface.setChannel10Register(0x32B5); // 011 0010 1011 0101
//      displayInterface.decodeData();
//
//      // Increment R1R5 at one second intervals.
//      for (int i = 1; i < 10; i++)
//      {
//        if (i == 1)
//        {
//          displayInterface.setChannel10Register(0x32A3); // 011 0010 1010 0011
//          displayInterface.decodeData();
//        }
//        else if (i == 2)
//        {
//          displayInterface.setChannel10Register(0x32B9); // 011 0010 1011 1001
//          displayInterface.decodeData();
//        }
//        else if (i == 3)
//        {
//          displayInterface.setChannel10Register(0x32BB); // 011 0010 1011 1011
//          displayInterface.decodeData();
//        }
//        else if (i == 4)
//        {
//          displayInterface.setChannel10Register(0x32AF); // 011 0010 1010 1111
//          displayInterface.decodeData();
//        }
//        else if (i == 5)
//        {
//          displayInterface.setChannel10Register(0x32BE); // 011 0010 1011 1110
//          displayInterface.decodeData();
//        }
//        else if (i == 6)
//        {
//          displayInterface.setChannel10Register(0x32BC); // 011 0010 1011 1100
//          displayInterface.decodeData();
//        }
//        else if (i == 7)
//        {
//          displayInterface.setChannel10Register(0x32B3); // 011 0010 1011 0011
//          displayInterface.decodeData();
//        }
//        else if (i == 8)
//        {
//          displayInterface.setChannel10Register(0x32BD); // 011 0010 1011 1101
//          displayInterface.decodeData();
//        }
//        else  // i == 9
//        {
//          displayInterface.setChannel10Register(0x32BF); // 011 0010 1011 1111
//          displayInterface.decodeData();
//        }
//        Thread.sleep(1000);
//      }

    // ---------------Toggle all the indicators --------------
    // Set NO ATT, GIMBAL LOCK, PROG, TRACKER, ALT, VEL
    indicatorInterface.setNoAtt(true);
    indicatorInterface.setGimbalLock(true);
    indicatorInterface.setProg(true);
    indicatorInterface.setTracker(true);
    indicatorInterface.setAlt(true);
    indicatorInterface.setVel(true);
    indicatorInterface.sendDisplayIndicatorsCommand();

    // Set UPLINK ACTY, KEY REL, OPR ERR, TEMP, STBY, RESTART
    indicatorInterface.setUplinkActy(true);
    indicatorInterface.setStandby(true);
    indicatorInterface.setKeyRel(true);
    indicatorInterface.setOprErr(true);
    indicatorInterface.setTemp(true);
    indicatorInterface.setParalm(true);
    indicatorInterface.sendOtherIndidatorsCommand();

    // Set COMP ACTY
    indicatorInterface.setCompActy(true);

    // Clear NO ATT, GIMBAL LOCK, PROG, TRACKER, ALT, VEL
    indicatorInterface.setNoAtt(false);
    indicatorInterface.setGimbalLock(false);
    indicatorInterface.setProg(false);
    indicatorInterface.setTracker(false);
    indicatorInterface.setAlt(false);
    indicatorInterface.setVel(false);
    indicatorInterface.sendDisplayIndicatorsCommand();

    // Clear UPLINK ACTY, STBY, KEY REL, OPR ERR, TEMP, RESTART
    indicatorInterface.setUplinkActy(false);
    indicatorInterface.setStandby(false);
    indicatorInterface.setKeyRel(false);
    indicatorInterface.setOprErr(false);
    indicatorInterface.setTemp(false);
    indicatorInterface.setParalm(false);
    indicatorInterface.sendOtherIndidatorsCommand();

    // Clear COMP ACTY
    indicatorInterface.setCompActy(false);

    // -------------- Set indicators and display to the NASA image. -------------------
    // Set KEY REL, TEMP
    indicatorInterface.setKeyRel(true);
    indicatorInterface.setTemp(true);
    indicatorInterface.sendOtherIndidatorsCommand();

    // Set NO ATT, PROG, VEL
    indicatorInterface.setNoAtt(true);
    indicatorInterface.setProg(true);
    indicatorInterface.setVel(true);
    indicatorInterface.sendDisplayIndicatorsCommand();

    // ---------------- Set display characters -------------------
    // MD1/MD2 "00"
    displayInterface.setChannel10Register(0x5AB5); // 101 1010 1011 0101
    displayInterface.decodeData();

    // VD1/VD2 "06"
    displayInterface.setChannel10Register(0x52BC); // 101 0010 1011 1100
    displayInterface.decodeData();

    // ND1/ND2 "36"
    displayInterface.setChannel10Register(0x4B7C); // 100 1011 0111 1100
    displayInterface.decodeData();

    // R1D1 "0"
    displayInterface.setChannel10Register(0x42B5); // 100 0010 1011 0101
    displayInterface.decodeData();

    // R1D2/R1D3 "00"
    displayInterface.setChannel10Register(0x3AB5); // 011 1010 1011 0101
    displayInterface.decodeData();

    // R1D4/R1D5 "-15"
    displayInterface.setChannel10Register(0x347E); // 011 0100 0111 1110
    displayInterface.decodeData();

    // R2D1/R2D2 "00"
    displayInterface.setChannel10Register(0x2AB5); // 010 1010 1011 0101
    displayInterface.decodeData();

    // R2D3/R2D4 "-01"
    displayInterface.setChannel10Register(0x26A3); // 010 0110 1010 0011
    displayInterface.decodeData();

    // R2D5/R3D1 "20"
    displayInterface.setChannel10Register(0x1B35); // 001 1011 0011 0101
    displayInterface.decodeData();

    // R3D2/R3D3 "+05"
    displayInterface.setChannel10Register(0x16BE); // 001 0110 1011 1110
    displayInterface.decodeData();

    // R3D4/R3D5 "68"
    displayInterface.setChannel10Register(0x0B9D); // 000 1011 1001 1101
    displayInterface.decodeData();
  }

  /**
   * Method called to indicate which key was pressed.
   */
  public void assertKbStr()
  {
    System.out.println("Keyboard Strobe asserted.");
    BitSet data = keyboardInterface.getChannnel15Data();
    System.out.println("Key Pressed = " + data.toLongArray()[0]);
  }
}
