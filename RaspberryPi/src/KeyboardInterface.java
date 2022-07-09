/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a singleton that provides the interface to the Keyboard Arduino.  The Keyboard
            Arduino checks for key presses, saves the key that was pressed and returns the key that was
            pressed when requested.  The key press value returned by the Arduino is the internal value
            used by NASA.  The value is saved in the Channel 15 register.  Notice that the PRO key is
            different in that it is saved in a Channnel 32 register.  The PRO key is returned when the
            RPRO control line is asserted.  The remainder of the key code values are returned when the
            KEY1 control line is asserted.  KEY1 is named because it returns key presses from DSKY1.
            There is a second DSKY on the Command Module that uses a KEY2 control line.  This code is
            simulating DSKY1.

  Mods:		  07/15/22  Initial Release.
*/
import java.io.IOException;
import java.math.BigInteger;
import java.util.BitSet;

public class KeyboardInterface
{
  private static final KeyboardInterface instance = new KeyboardInterface();

  private final SerialInterface serialInterface;

  // Channel 15 contains the Keyboard #1 value of the last key pressed.
  private BitSet channel15Register = new BitSet(5);
  private boolean proKeyPressed = false;

  public static KeyboardInterface getInstance()
  {
    return instance;
  }

  private KeyboardInterface()
  {
    serialInterface = SerialInterface.getInstance();
  }

  /**
   * Method to get the value in the Channel 15 register.
   *
   * @return The bits of channel 15.
   */
  public BitSet getChannnel15Data()
  {
    return channel15Register;
  }

  /**
   * Method to determine if the Proceed key has been pressed.
   *
   * @return Whether or not the PRO key has been pressed.
   */
  public boolean isProKeyPressed()
  {
    return proKeyPressed;
  }

  /**
   * Method to read data from the serial interface and if a key has been pressed, then
   * place the keycode value into the Channel 15 register.  The keycode values are
   * cryptic NASA defined values.
   *
   * @return The keycode of the last key pressed.
   * @throws IOException A serial interface error occurred.
   */
  public int readData() throws IOException
  {
    int keyCode = -1;
    boolean dataFound = false;
    while (!dataFound)
    {
      byte[] bytesRead = serialInterface.readData();
      if (bytesRead.length > 0)
      {
        keyCode = new BigInteger(bytesRead).intValue();
        if (keyCode == 0)
        {
          proKeyPressed = true;
        }
        else
        {
          channel15Register = BitSet.valueOf(new long[]{keyCode});
        }
        dataFound = true;
      }
    }
    return keyCode;
  }
}
