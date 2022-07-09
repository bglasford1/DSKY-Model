/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class provides the indicator commands in a human readable enumeration type.
            Note that the number sent to the indicator Arduino is the ordinal.

  Mods:		  07/15/22  Initial Release.
*/
public enum IndicatorCommand
{
  NOOP,
  RESET,
  IDENTIFY,
  UPLINK_ACTY,
  NO_ATT,
  STBY,
  KEY_REL,
  OPR_ERR,
  TEMP,
  GIMBAL_LOCK,
  PROG,
  RESTART,
  TRACKER,
  ALT,
  VEL
}
