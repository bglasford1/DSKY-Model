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
    setDSPL(false, false, false, false, false);
    setDSPH(false, false, false, false, false);
    displayInterface.setChannel10Bit(11, false);
    setRLWD(false, false, true, true);
  }

  /**
   * This method first sets all numbers to "8", then exercises all the sign characters,
   * then sets R1 to 0 and counts up to 9, then toggles on/off all the indicators, then
   * resets the displays and finally sets the indicators and display to the picture I
   * captured off the web.
   */
  public void init()
  {
    try
    {
      // ---------------- Set all characters to "8" -------------------
      setDSPL(true, false, true, true, true);
      setDSPH(true, false, true, true, true);
      displayInterface.setChannel10Bit(11, false);

      // MD1/MD2
      setRLWD(true, true, false, true);
      displayInterface.decodeData();

      // VD1/VD2
      setRLWD(false, true, false, true);
      displayInterface.decodeData();

      // ND1/ND2
      setRLWD(true, false, false, true);
      displayInterface.decodeData();

      // R1D1
      setRLWD(false, false, false, true);
      displayInterface.decodeData();

      // R1D2/R1D3
      setRLWD(true, true, true, false);
      displayInterface.decodeData();

      // R1D4/R1D5
      setRLWD(false, true, true, false);
      displayInterface.decodeData();

      // R2D1/R2D2
      setRLWD(true, false, true, false);
      displayInterface.decodeData();

      // R2D3/R2D4
      setRLWD(false, false, true, false);
      displayInterface.decodeData();

      // R2D5/R3D1
      setRLWD(true, true, false, false);
      displayInterface.decodeData();

      // R3D2/R3D3
      setRLWD(false, true, false, false);
      displayInterface.decodeData();

      // R3D4/R3D5
      setRLWD(true, false, false, false);
      displayInterface.decodeData();

      // -------------- Put the +/- signs through their paces -----------------
      // R1D2/R1D3/+R1S
      resetChannel10();
      displayInterface.resetSigns();
      displayInterface.setChannel10Bit(11, true);
      setRLWD(true, true, true, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R2D1/R2D2/+R2S
      setRLWD(true, false, true, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R3D2/R3D3/+R3S
      setRLWD(false, true, false, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R1D2/R1D3/+R1S
      displayInterface.setChannel10Bit(11, false);
      setRLWD(true, true, true, false);
      displayInterface.decodeData();

      // R2D1/R2D2/+R2S
      setRLWD(true, false, true, false);
      displayInterface.decodeData();

      // R3D2/R3D3/+R3S
      setRLWD(false, true, false, false);
      displayInterface.decodeData();

      // R1D4/R1D5/-R1S
      displayInterface.setChannel10Bit(11, true);
      setRLWD(false, true, true, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R2D3/R2D4/-R2S
      setRLWD(false, false, true, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R3D4/R3D5/-R3S
      setRLWD(true, false, false, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R1D4/R1D5/-R1S
      displayInterface.setChannel10Bit(11, false);
      setRLWD(false, true, true, false);
      displayInterface.decodeData();

      // R2D3/R2D4/-R2S
      setRLWD(false, false, true, false);
      displayInterface.decodeData();

      // R3D4/R3D5/-R3S
      setRLWD(true, false, false, false);
      displayInterface.decodeData();


      // ---------------- Make R1 count from 0-9 -------------------
      // Set R1 to all zeros.
      setDSPL(true, false, true, false, true);
      setDSPH(true, false, true, false, true);
      displayInterface.setChannel10Bit(11, false);

      // R1D1
      setRLWD(false, false, false, true);
      displayInterface.decodeData();

      // R1D2/R1D3
      setRLWD(true, true, true, false);
      displayInterface.setChannel10Bit(15, false);
      displayInterface.decodeData();

      // R1D4/R1D5
      setRLWD(false, true, true, false);
      displayInterface.decodeData();

      // Incrementing R1 at one second intervals.
      for (int i = 1; i < 10; i++)
      {
        if (i == 1)
        {
          setDSPL(true, true, false, false, false);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
          displayInterface.decodeData();
        }
        else if (i == 2)
        {
          setDSPL(true, false, false, true, true);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
          displayInterface.decodeData();
        }
        else if (i == 3)
        {
          setDSPL(true, true, false, true, true);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
          displayInterface.decodeData();
        }
        else if (i == 4)
        {
          setDSPL(true, true, true, true, false);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
          displayInterface.decodeData();
        }
        else if (i == 5)
        {
          setDSPL(false, true, true, true, true);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
          displayInterface.decodeData();
        }
        else if (i == 6)
        {
          setDSPL(false, false, true, true, true);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
          displayInterface.decodeData();
        }
        else if (i == 7)
        {
          setDSPL(true, true, false, false, true);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
          displayInterface.decodeData();
        }
        else if (i == 8)
        {
          setDSPL(true, false, true, true, true);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
          displayInterface.decodeData();
        }
        else
        {
          setDSPL(true, true, true, true, true);
          setDSPH(true, false, true, false, true);
          displayInterface.setChannel10Bit(11, false);

          // R1D4/R1D5
          setRLWD(false, true, true, false);
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

      // -------------- Set indicators and display to the NASA image. -------------------
      resetChannel10();
      // Set NO ATT, PROG, VEL
      displayInterface.setChannel10Bit(4, true);
      displayInterface.decodeData();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(9, true);
      displayInterface.decodeData();
      Thread.sleep(500);
      displayInterface.setChannel10Bit(3, true);
      displayInterface.decodeData();
      Thread.sleep(500);

      // Set KEY REL, TEMP
      indicatorInterface.setChannel11Bit(4, true);
      Thread.sleep(500);
      indicatorInterface.setChannel11Bit(5, true);
      Thread.sleep(500);

      // ---------------- Set display characters -------------------

      // MD1/MD2 "00"
      setDSPL(true, false, true, false, true);
      setDSPH(true, false, true, false, true);
      displayInterface.setChannel10Bit(11, false);
      setRLWD(true, true, false, true);
      displayInterface.decodeData();
      Thread.sleep(500);

      // VD1/VD2 "06"
      setDSPL(false, false, true, true, true);
      setDSPH(true, false, true, false, true);
      displayInterface.setChannel10Bit(11, false);
      setRLWD(false, true, false, true);
      displayInterface.decodeData();
      Thread.sleep(500);

      // ND1/ND2 "36"
      setDSPL(false, false, true, true, true);
      setDSPH(true, true, false, true, true);
      displayInterface.setChannel10Bit(11, false);
      setRLWD(true, false, false, true);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R1D1 "0"
      setDSPL(true, false, true, false, true);
      setDSPH(true, false, true, false, true);
      displayInterface.setChannel10Bit(11, false);
      setRLWD(false, false, false, true);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R1D2/R1D3 "00"
      setDSPL(true, false, true, false, true);
      setDSPH(true, false, true, false, true);
      displayInterface.setChannel10Bit(11, false);
      setRLWD(true, true, true, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R1D4/R1D5 "-15"
      setDSPL(false, true, true, true, true);
      setDSPH(true, true, false, false, false);
      displayInterface.setChannel10Bit(11, true);
      setRLWD(false, true, true, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R2D1/R2D2 "00"
      setDSPL(true, false, true, false, true);
      setDSPH(true, false, true, false, true);
      displayInterface.setChannel10Bit(11, false);
      setRLWD(true, false, true, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R2D3/R2D4 "-01"
      setDSPL(true, true, false, false, false);
      setDSPH(true, false, true, false, true);
      displayInterface.setChannel10Bit(11, true);
      setRLWD(false, false, true, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R2D5/R3D1 "20"
      setDSPL(true, false, true, false, true);
      setDSPH(true, false, false, true, true);
      displayInterface.setChannel10Bit(11, false);
      setRLWD(true, true, false, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R3D2/R3D3 "+05"
      setDSPL(false, true, true, true, true);
      setDSPH(true, false, true, false, true);
      displayInterface.setChannel10Bit(11, true);
      setRLWD(false, true, false, false);
      displayInterface.decodeData();
      Thread.sleep(500);

      // R3D4/R3D5 "68"
      setDSPL(true, false, true, true, true);
      setDSPH(false, false, true, true, true);
      displayInterface.setChannel10Bit(11, false);
      setRLWD(true, false, false, false);
      displayInterface.decodeData();
    }
    catch (InterruptedException e)
    {
      System.out.println(e.getMessage());
    }
  }

  private void setDSPL(boolean bit1, boolean bit2, boolean bit3, boolean bit4, boolean bit5)
  {
    displayInterface.setChannel10Bit(1, bit1);
    displayInterface.setChannel10Bit(2, bit2);
    displayInterface.setChannel10Bit(3, bit3);
    displayInterface.setChannel10Bit(4, bit4);
    displayInterface.setChannel10Bit(5, bit5);
  }

  private void setDSPH(boolean bit6, boolean bit7, boolean bit8, boolean bit9, boolean bit10)
  {
    displayInterface.setChannel10Bit(6, bit6);
    displayInterface.setChannel10Bit(7, bit7);
    displayInterface.setChannel10Bit(8, bit8);
    displayInterface.setChannel10Bit(9, bit9);
    displayInterface.setChannel10Bit(10, bit10);
  }

  private void setRLWD(boolean bit12, boolean bit13, boolean bit14, boolean bit15)
  {
    displayInterface.setChannel10Bit(12, bit12);
    displayInterface.setChannel10Bit(13, bit13);
    displayInterface.setChannel10Bit(14, bit14);
    displayInterface.setChannel10Bit(15, bit15);
  }

  /**
   * Method called to indicate which key was pressed.
   */
  public void assertKbStr()
  {
    System.out.println("Keyboard Strobe asserted.");
    BitSet data = keyboardInterface.getChannnel15Data();
    System.out.println("Key Pressed = " + data.toString());
  }
}
