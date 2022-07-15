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
  public enum Mode
  {
    TEST,
    AGC,
    SWSIM,
    HWSIM
  }

  public static void main(String[] args)
  {
    AGCInterface agcInterface = null;
    AGCTestInterface AGCTestInterface = null;
    SimInterface simInterface = null;

    Mode mode = Mode.HWSIM;

    SerialInterface serialInterface = SerialInterface.getInstance();
    serialInterface.initInterface();

    // Input is based on args.  By default run with the hardware simulator.
    if (args[0].equalsIgnoreCase("--AGC"))
    {
      mode = Mode.AGC;
      agcInterface = new AGCInterface();
      agcInterface.init();
    }
    else if (args[0].equalsIgnoreCase("--AGC-TEST"))
    {
      mode = Mode.TEST;
      AGCTestInterface = new AGCTestInterface();
      AGCTestInterface.init();
    }
    else if (args[0].equalsIgnoreCase("--AGC-SWSIM"))
    {
      mode = Mode.SWSIM;
      simInterface = new SimInterface();
      simInterface.initInterface();
      simInterface.start();
    }
    else // Run with hardware simulator.
    {
      mode = Mode.HWSIM;
      simInterface = new SimInterface();
      simInterface.initInterface();
      simInterface.start();
    }

    KeyboardInterface keyboardInterface = KeyboardInterface.getInstance();

    // This call hangs waiting for keyboard input.
    try
    {
      while (true)
      {
        int keyCode = keyboardInterface.readData();
        if (mode == Mode.TEST)
        {
          AGCTestInterface.assertKbStr();
        }
        else if (mode == Mode.AGC)
        {
          agcInterface.assertKbStr();
        }
        else
        {
          simInterface.sendKeyCode(keyCode);
        }
      }
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }

    if (simInterface != null)
    {
      simInterface.closeInterface();
    }

    serialInterface.closeInterface();
  }
}
