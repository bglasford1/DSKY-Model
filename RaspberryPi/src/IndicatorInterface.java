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

  private final BitSet displayIndicatorBits = new BitSet(6);
  private final BitSet otherIndicatorBits = new BitSet(6);

  private enum displayIndicators
  {
    NO_ATT,
    GIMBAL_LOCK,
    PROG,
    TRACKER,
    ALT,
    VEL
  }

  private enum otherIndicators
  {
    UPLINK_ACTY,
    KEY_REL,
    OPR_ERR,
    TEMP,
    STBY,
    RESTART
  }

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
    serialInterface.sendDisplayIndicatorsCommand(0);
    serialInterface.sendOtherIndicatorsCommand(0);
    displayIndicatorBits.clear();
    otherIndicatorBits.clear();
  }

  /**
   * Method called to flash the verb/noun by turning the values on/off.
   *
   * @param value Whether to turn them on or off.
   */
  public void setFlashVerbNoun(boolean value)
  {
    serialInterface.flashVerbNoun(value);
  }

  /**
   * Method called to turn on/off the COMP ACTY indicator.
   *
   * @param value Whether to turn it on or off.
   */
  public void setCompActy(boolean value)
  {
    serialInterface.sendDisplayCommand(DisplayCommand.COMP_ACTY, value ? 1 : 0);
  }

  /**
   * Method called to set the parity alarm which lights the RESTART indicator.
   *
   * @param value Whether the parity alarm is set or cleared.
   */
  public void setParalm(boolean value)
  {
    if (value)
    {
      otherIndicatorBits.set(otherIndicators.RESTART.ordinal());
    }
    else
    {
      otherIndicatorBits.clear(otherIndicators.RESTART.ordinal());
    }
  }

  /**
   * Method called to light the RESTART indicator.
   */
  public void setRestart(boolean value)
  {
    if (value)
    {
      otherIndicatorBits.set(otherIndicators.RESTART.ordinal());
    }
    else
    {
      otherIndicatorBits.clear(otherIndicators.RESTART.ordinal());
    }
  }

  /**
   * Method called to light the STBY indicator.
   */
  public void setStandby(boolean value)
  {
    if (value)
    {
      otherIndicatorBits.set(otherIndicators.STBY.ordinal());
    }
    else
    {
      otherIndicatorBits.clear(otherIndicators.STBY.ordinal());
    }
  }

  /**
   * Method called to light the UPLINK ACTY indicator.
   */
  public void setUplinkActy(boolean value)
  {
    if (value)
    {
      otherIndicatorBits.set(otherIndicators.UPLINK_ACTY.ordinal());
    }
    else
    {
      otherIndicatorBits.clear(otherIndicators.UPLINK_ACTY.ordinal());
    }
  }

  /**
   * Method called to light the KEY REL indicator.
   */
  public void setKeyRel(boolean value)
  {
    if (value)
    {
      otherIndicatorBits.set(otherIndicators.KEY_REL.ordinal());
    }
    else
    {
      otherIndicatorBits.clear(otherIndicators.KEY_REL.ordinal());
    }
  }

  /**
   * Method called to light the OPR ERR indicator.
   */
  public void setOprErr(boolean value)
  {
    if (value)
    {
      otherIndicatorBits.set(otherIndicators.OPR_ERR.ordinal());
    }
    else
    {
      otherIndicatorBits.clear(otherIndicators.OPR_ERR.ordinal());
    }
  }

  /**
   * Method called to light the TEMP indicator.
   */
  public void setTemp(boolean value)
  {
    if (value)
    {
      otherIndicatorBits.set(otherIndicators.TEMP.ordinal());
    }
    else
    {
      otherIndicatorBits.clear(otherIndicators.TEMP.ordinal());
    }
  }

  public void sendOtherIndidatorsCommand()
  {
    serialInterface.sendOtherIndicatorsCommand(Utils.toInt(otherIndicatorBits));
  }

  /**
   * Method called to light the VEL indicator.
   */
  public void setVel(boolean value)
  {
    if (value)
    {
      displayIndicatorBits.set(displayIndicators.VEL.ordinal());
    }
    else
    {
      displayIndicatorBits.clear(displayIndicators.VEL.ordinal());
    }
  }

  /**
   * Method called to light the ALT indicator.
   */
  public void setAlt(boolean value)
  {
    if (value)
    {
      displayIndicatorBits.set(displayIndicators.ALT.ordinal());
    }
    else
    {
      displayIndicatorBits.clear(displayIndicators.ALT.ordinal());
    }
  }

  /**
   * Method called to light the NO ATT indicator.
   */
  public void setNoAtt(boolean value)
  {
    if (value)
    {
      displayIndicatorBits.set(displayIndicators.NO_ATT.ordinal());
    }
    else
    {
      displayIndicatorBits.clear(displayIndicators.NO_ATT.ordinal());
    }
  }

  /**
   * Method called to light the GIMBAL LOCK indicator.
   */
  public void setGimbalLock(boolean value)
  {
    if (value)
    {
      displayIndicatorBits.set(displayIndicators.GIMBAL_LOCK.ordinal());
    }
    else
    {
      displayIndicatorBits.clear(displayIndicators.GIMBAL_LOCK.ordinal());
    }
  }

  /**
   * Method called to light the Tracker indicator.
   */
  public void setTracker(boolean value)
  {
    if (value)
    {
      displayIndicatorBits.set(displayIndicators.TRACKER.ordinal());
    }
    else
    {
      displayIndicatorBits.clear(displayIndicators.TRACKER.ordinal());
    }
  }

  /**
   * Method called to light the PROG indicator.
   */
  public void setProg(boolean value)
  {
    if (value)
    {
      displayIndicatorBits.set(displayIndicators.PROG.ordinal());
    }
    else
    {
      displayIndicatorBits.clear(displayIndicators.PROG.ordinal());
    }
  }

  public void sendDisplayIndicatorsCommand()
  {
    serialInterface.sendDisplayIndicatorsCommand(Utils.toInt(displayIndicatorBits));
  }
}
