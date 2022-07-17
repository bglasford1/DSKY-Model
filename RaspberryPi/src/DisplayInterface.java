/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a singleton that provides the interface to the Display Arduino.  The Display
            Arduino displays the 7-segment LEDs along with the Computer Activity light.  The data
            received from the AGC is persisted in the channel 10register.  This class decodes channel 10
            data and uses this data to update the display when a DISP control pin is asserted.

  Mods:		  07/15/22  Initial Release.
            07/16/22  Improve performance.
*/
import java.util.BitSet;

public class DisplayInterface
{
  private static final DisplayInterface instance = new DisplayInterface();

  private final SerialInterface serialInterface;

  // Index starts with 0.
  private final BitSet channel10Register = new BitSet(15);

  public static DisplayInterface getInstance()
  {
    return instance;
  }

  private DisplayInterface()
  {
    serialInterface = SerialInterface.getInstance();
  }

  /**
   * Method called to set/clear a bit of the Channel 10 register.
   *
   * @param bit The bit to set/clear.
   * @param value The value to use.
   */
  public void setChannel10Bit(int bit, boolean value)
  {
    if (value)
      channel10Register.set(bit);
    else
      channel10Register.clear(bit);
  }

  /**
   * Method called to set the Channel 10 register values.
   *
   * @param value The integer value for the Channel 10 register.
   */
  public void setChannel10Register(int value)
  {
    // Don't process the MSB.
    for (int i = 14; i >= 0; i--)
    {
      boolean nextBit = (value & (1 << i)) != 0;
      setChannel10Bit(i, nextBit);
    }
  }

  /**
   * Method called to reset the display back to a power on state.
   */
  public void resetDisplay()
  {
    serialInterface.sendReset();
    channel10Register.clear();
  }

  /**
   * Method called to send the channel 10 register value to the Display Arduino.
   */
  public void decodeData()
  {
    serialInterface.sendDisplayCommand(Utils.toInt(channel10Register));
  }
}
