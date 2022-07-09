/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This is the main class that runs the DSKY model.  There are three subordinate Arduino Nanos that
            interface to the two LCD screens and the keyboard.  A simple serial protocol drives the Arduinos.
            This code can get its control from either the AGC hardware or the internal AGC simulator.  If the
            AGC hardware CLK1 pin has a clock pulse present then the external AGC hardware provides control.
            If not then the control is obtained from the AGC simulator running on this Raspberry Pi.

  Mods:		  07/15/22  Initial Release.
*/
public class DSKY
{
  public static void main(String[] args)
  {
    boolean testMode = true;
    AGCInterface agcInterface = null;
    AGCTestInterface AGCTestInterface = null;

    SerialInterface serialInterface = SerialInterface.getInstance();
    serialInterface.initInterface();

    // In test mode the interface to the AGC is via a test interface and not the real AGC.
    if (testMode)
    {
      AGCTestInterface = new AGCTestInterface();
      AGCTestInterface.init();
    }
    else
    {
      agcInterface = new AGCInterface();
      agcInterface.init();
    }

    KeyboardInterface keyboardInterface = KeyboardInterface.getInstance();

    // TODO: Add code here to check for pulse on CLK1 pin, if not then start simulator.

    // This call hangs waiting for keyboard input.
    try
    {
      while (true)
      {
        int keyCode = keyboardInterface.readData();
        System.out.println("Received Key Code: " + keyCode);
        if (testMode)
        {
          AGCTestInterface.assertKbStr();
        }
        else
        {
          agcInterface.assertKbStr();
        }
      }
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }
}
