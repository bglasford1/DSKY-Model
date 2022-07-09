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

  private void resetChannel10()
  {
    // Initialize all channel 10 bits to RLYWD = 12, everything off.
    displayInterface.setChannel10Bit(1, false);
    displayInterface.setChannel10Bit(2, false);
    displayInterface.setChannel10Bit(3, false);
    displayInterface.setChannel10Bit(4, false);
    displayInterface.setChannel10Bit(5, false);
    displayInterface.setChannel10Bit(6, false);
    displayInterface.setChannel10Bit(7, false);
    displayInterface.setChannel10Bit(8, false);
    displayInterface.setChannel10Bit(9, false);
    displayInterface.setChannel10Bit(10, false);
    displayInterface.setChannel10Bit(11, false);
    displayInterface.setChannel10Bit(12, false);
    displayInterface.setChannel10Bit(13, false);
    displayInterface.setChannel10Bit(14, true);
    displayInterface.setChannel10Bit(15, true);
  }

  public void init()
  {
    try
    {
      // ---------------- Set all characters to "8" -------------------
      displayInterface.setChannel10Bit(1, true);
      displayInterface.setChannel10Bit(2, false);
      displayInterface.setChannel10Bit(3, true);
      displayInterface.setChannel10Bit(4, true);
      displayInterface.setChannel10Bit(5, true);
      displayInterface.setChannel10Bit(6, true);
      displayInterface.setChannel10Bit(7, false);
      displayInterface.setChannel10Bit(8, true);
      displayInterface.setChannel10Bit(9, true);
      displayInterface.setChannel10Bit(10, true);
      displayInterface.setChannel10Bit(11, false);

      // MD1/MD2
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, true);
      displayInterface.decodeData();

      // VD1/VD2
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, true);
      displayInterface.decodeData();

      // ND1/ND2
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, true);
      displayInterface.decodeData();

      // R1D1
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, true);
      displayInterface.decodeData();

      // R1D2/R1D3
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R1D4/R1D5
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R2D1/R2D2
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R2D3/R2D4
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R2D5/R3D1
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R3D2/R3D3
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R3D4/R3D5
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // -------------- Put the +/- signs through their paces -----------------
      // R1D2/R1D3/+R1S
      resetChannel10();
      displayInterface.resetSigns();
      displayInterface.setChannel10Bit(11, true);
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R2D1/R2D2/+R2S
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R3D2/R3D3/+R3S
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R1D2/R1D3/+R1S
      displayInterface.setChannel10Bit(11, false);
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R2D1/R2D2/+R2S
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R3D2/R3D3/+R3S
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R1D4/R1D5/-R1S
      displayInterface.setChannel10Bit(11, true);
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R2D3/R2D4/-R2S
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R3D4/R3D5/-R3S
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R1D4/R1D5/-R1S
      displayInterface.setChannel10Bit(11, false);
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R2D3/R2D4/-R2S
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R3D4/R3D5/-R3S
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();


      // ---------------- Make R1 count from 0-9 -------------------
      // Set R1 to all zeros.
      displayInterface.setChannel10Bit(1, true);
      displayInterface.setChannel10Bit(2, false);
      displayInterface.setChannel10Bit(3, true);
      displayInterface.setChannel10Bit(4, false);
      displayInterface.setChannel10Bit(5, true);
      displayInterface.setChannel10Bit(6, true);
      displayInterface.setChannel10Bit(7, false);
      displayInterface.setChannel10Bit(8, true);
      displayInterface.setChannel10Bit(9, false);
      displayInterface.setChannel10Bit(10, true);
      displayInterface.setChannel10Bit(11, false);

      // R1D1
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, false);
      displayInterface.setChannel10Bit(14, false);
      displayInterface.setChannel10Bit(15, true);
      displayInterface.decodeData();

      // R1D2/R1D3
      displayInterface.setChannel10Bit(12, true);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R1D4/R1D5
      displayInterface.setChannel10Bit(12, false);
      displayInterface.setChannel10Bit(13, true);
      displayInterface.setChannel10Bit(14, true);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // Incrementing R1 at one second intervals.
      for (int i = 1; i < 10; i++)
      {
        if (i == 1)
        {
          displayInterface.setChannel10Bit(1, true);
          displayInterface.setChannel10Bit(2, true);
          displayInterface.setChannel10Bit(3, false);
          displayInterface.setChannel10Bit(4, false);
          displayInterface.setChannel10Bit(5, false);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        else if (i == 2)
        {
          displayInterface.setChannel10Bit(1, true);
          displayInterface.setChannel10Bit(2, false);
          displayInterface.setChannel10Bit(3, false);
          displayInterface.setChannel10Bit(4, true);
          displayInterface.setChannel10Bit(5, true);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        else if (i == 3)
        {
          displayInterface.setChannel10Bit(1, true);
          displayInterface.setChannel10Bit(2, true);
          displayInterface.setChannel10Bit(3, false);
          displayInterface.setChannel10Bit(4, true);
          displayInterface.setChannel10Bit(5, true);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        else if (i == 4)
        {
          displayInterface.setChannel10Bit(1, true);
          displayInterface.setChannel10Bit(2, true);
          displayInterface.setChannel10Bit(3, true);
          displayInterface.setChannel10Bit(4, true);
          displayInterface.setChannel10Bit(5, false);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        else if (i == 5)
        {
          displayInterface.setChannel10Bit(1, false);
          displayInterface.setChannel10Bit(2, true);
          displayInterface.setChannel10Bit(3, true);
          displayInterface.setChannel10Bit(4, true);
          displayInterface.setChannel10Bit(5, true);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        else if (i == 6)
        {
          displayInterface.setChannel10Bit(1, false);
          displayInterface.setChannel10Bit(2, false);
          displayInterface.setChannel10Bit(3, true);
          displayInterface.setChannel10Bit(4, true);
          displayInterface.setChannel10Bit(5, true);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        else if (i == 7)
        {
          displayInterface.setChannel10Bit(1, true);
          displayInterface.setChannel10Bit(2, true);
          displayInterface.setChannel10Bit(3, false);
          displayInterface.setChannel10Bit(4, false);
          displayInterface.setChannel10Bit(5, true);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        else if (i == 8)
        {
          displayInterface.setChannel10Bit(1, true);
          displayInterface.setChannel10Bit(2, false);
          displayInterface.setChannel10Bit(3, true);
          displayInterface.setChannel10Bit(4, true);
          displayInterface.setChannel10Bit(5, true);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        else
        {
          displayInterface.setChannel10Bit(1, true);
          displayInterface.setChannel10Bit(2, true);
          displayInterface.setChannel10Bit(3, true);
          displayInterface.setChannel10Bit(4, true);
          displayInterface.setChannel10Bit(5, true);
          displayInterface.setChannel10Bit(6, true);
          displayInterface.setChannel10Bit(7, false);
          displayInterface.setChannel10Bit(8, true);
          displayInterface.setChannel10Bit(9, false);
          displayInterface.setChannel10Bit(10, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          displayInterface.setChannel10Bit(12, false);
          displayInterface.setChannel10Bit(13, true);
          displayInterface.setChannel10Bit(14, true);
          displayInterface.setChannel10Bit(15, false);
          displayInterface.decodeData();
        }
        Thread.sleep(1000);
      }

      // ---------------Toggle all the indicators --------------
      // Toggle UPLINK ACTY
      indicatorInterface.setChannel11Bit(3, true);
      indicatorInterface.setChannel11Bit(3, false);

      // Toggle NO ATT
      resetChannel10();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(4, true);
      displayInterface.decodeData();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(4, false);
      displayInterface.decodeData();

      // Toggle STBY
      indicatorInterface.setStandby(true);
      Thread.sleep(500);
      indicatorInterface.setStandby(false);

      // Toggle KEY REL
      indicatorInterface.setChannel11Bit(5, true);
      Thread.sleep(500);
      indicatorInterface.setChannel11Bit(5, false);

      // Toggle OPR ERR
      indicatorInterface.setChannel11Bit(7, true);
      Thread.sleep(500);
      indicatorInterface.setChannel11Bit(7, false);

      // Toggle TEMP
      indicatorInterface.setChannel11Bit(4, true);
      Thread.sleep(500);
      indicatorInterface.setChannel11Bit(4, false);

      // Toggle GIMBAL LOCK
      resetChannel10();
      displayInterface.setChannel10Bit(6, true);
      displayInterface.decodeData();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(6, false);
      displayInterface.decodeData();

      // Toggle PROG
      resetChannel10();
      displayInterface.setChannel10Bit(9, true);
      displayInterface.decodeData();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(9, false);
      displayInterface.decodeData();

      // Toggle RESTART
      indicatorInterface.setParalm(true);
      Thread.sleep(500);
      indicatorInterface.setParalm(false);

      // Toggle TRACKER
      resetChannel10();
      displayInterface.setChannel10Bit(8, true);
      displayInterface.decodeData();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(8, false);
      displayInterface.decodeData();

      // Toggle ALT
      resetChannel10();
      displayInterface.setChannel10Bit(5, true);
      displayInterface.decodeData();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(5, false);
      displayInterface.decodeData();

      // Toggle VEL
      resetChannel10();
      displayInterface.setChannel10Bit(3, true);
      displayInterface.decodeData();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(3, false);
      displayInterface.decodeData();

      // Toggle COMP ACTY
      indicatorInterface.setChannel11Bit(2, true);
      Thread.sleep(500);
      indicatorInterface.setChannel11Bit(2, false);
      Thread.sleep(500);

      // Reset the displays.
      displayInterface.resetDisplay();
      indicatorInterface.resetDisplay();
      Thread.sleep(500);

      // TODO: go to standby
    }
    catch (InterruptedException e)
    {
      System.out.println(e.getMessage());
    }
  }

  public void assertKbStr()
  {
    System.out.println("Keyboard Strobe asserted.");
    BitSet data = keyboardInterface.getChannnel15Data();
    System.out.println("Key Pressed = " + data.toString());
  }
}
