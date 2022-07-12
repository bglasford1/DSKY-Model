/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a singleton that provides the interface to the Indicators Arduino.  The Indicators
            Arduino displays the various indicator lights to the operator.  The data received from the AGC is
            persisted in the Channel 11 register.  The indicator light on the LCD panel is only updated if the
            state of the given indicator changes.

  Mods:		  07/15/22  Initial Release.
*/
import java.util.BitSet;

public class IndicatorInterface
{
  private static final IndicatorInterface instance = new IndicatorInterface();

  private final SerialInterface serialInterface;

  private final BitSet channel11Register = new BitSet(15);
  private boolean paralm = false;

  public static IndicatorInterface getInstance()
  {
    return instance;
  }

  private IndicatorInterface()
  {
    serialInterface = SerialInterface.getInstance();
  }

  /**
   * Method used to reset the display back to power on conditions.
   */
  public void resetDisplay()
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.RESET, false);
    channel11Register.clear();
    paralm = false;
  }

  /**
   * Method called to set the parity alarm which lights the RESTART indicator.
   *
   * @param value Whether the parity alarm is set or cleared.
   */
  public void setParalm(boolean value)
  {
    if (value != paralm)
    {
      paralm = value;
      serialInterface.sendIndicatorCommand(IndicatorCommand.RESTART, value);
    }
  }

  /**
   * Method called to light the STBY indicator.
   */
  public void setStandby(boolean value)
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.STBY, value);
  }

  /**
   * Method called to light the VEL indicator.
   */
  public void setVel(boolean value)
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.VEL, value);
  }

  /**
   * Method called to light the ALT indicator.
   */
  public void setAlt(boolean value)
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.ALT, value);
  }

  /**
   * Method called to light the NO ATT indicator.
   */
  public void setNoAtt(boolean value)
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.NO_ATT, value);
  }

  /**
   * Method called to light the GIMBAL LOCK indicator.
   */
  public void setGimbalLock(boolean value)
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.GIMBAL_LOCK, value);
  }

  /**
   * Method called to light the Tracker indicator.
   */
  public void setTracker(boolean value)
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.TRACKER, value);
  }

  /**
   * Method called to light the PROG indicator.
   */
  public void setProg(boolean value)
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.PROG, value);
  }

  /**
   * Method called to light the RESTART indicator.
   */
  public void setRestart(boolean value)
  {
    serialInterface.sendIndicatorCommand(IndicatorCommand.RESTART, value);
  }

  /**
   * Method called to set/clear a bit of the channel 11 register.
   *
   * @param bit The bit to set/clear.
   * @param value The value of the bit.
   */
  public void setChannel11Bit(int bit, boolean value)
  {
    if (bit == 2)
    {
      if (value != channel11Register.get(bit))
      {
        if (value)
        {
          channel11Register.set(bit);
          serialInterface.sendDisplayCommand(DisplayCommand.COMP_ACTY, 1);
        }
        else
        {
          channel11Register.clear(bit);
          serialInterface.sendDisplayCommand(DisplayCommand.COMP_ACTY, 0);
        }
      }
    }
    else if (bit == 3)
    {
      if (value != channel11Register.get(bit))
      {
        if (value)
        {
          channel11Register.set(bit);
          serialInterface.sendIndicatorCommand(IndicatorCommand.UPLINK_ACTY, true);
        }
        else
        {
          channel11Register.clear(bit);
          serialInterface.sendIndicatorCommand(IndicatorCommand.UPLINK_ACTY, false);
        }
      }
    }
    else if (bit == 4)
    {
      if (value != channel11Register.get(bit))
      {
        if (value)
        {
          channel11Register.set(bit);
          serialInterface.sendIndicatorCommand(IndicatorCommand.TEMP, true);
        }
        else
        {
          channel11Register.clear(bit);
          serialInterface.sendIndicatorCommand(IndicatorCommand.TEMP, false);
        }
      }
    }
    else if (bit == 5)
    {
      if (value != channel11Register.get(bit))
      {
        if (value)
        {
          channel11Register.set(bit);
          serialInterface.sendIndicatorCommand(IndicatorCommand.KEY_REL, true);
        }
        else
        {
          channel11Register.clear(bit);
          serialInterface.sendIndicatorCommand(IndicatorCommand.KEY_REL, false);
        }
      }
    }
    else if (bit == 6)
    {
      if (value != channel11Register.get(bit))
      {
        if (value)
        {
          channel11Register.set(bit);
          serialInterface.flashVerbNoun(true);
        }
        else
        {
          channel11Register.clear(bit);
          serialInterface.flashVerbNoun(false);
        }
      }
    }
    else if (bit == 7)
    {
      if (value != channel11Register.get(bit))
      {
        if (value)
        {
          channel11Register.set(bit);
          serialInterface.sendIndicatorCommand(IndicatorCommand.OPR_ERR, true);
        }
        else
        {
          channel11Register.clear(bit);
          serialInterface.sendIndicatorCommand(IndicatorCommand.OPR_ERR, false);
        }
      }
    }
  }
}
