/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class provides the display commands in a human readable enumeration type.
            Note that the number sent to the display Arduino is the ordinal.

  Mods:		  07/15/22  Initial Release.
*/

public enum DisplayCommand
{
  NOOP,
  RESET,
  IDENTIFY,
  MD1,
  MD2,
  VD1,
  VD2,
  ND1,
  ND2,
  R1S,
  R1D1,
  R1D2,
  R1D3,
  R1D4,
  R1D5,
  R2S,
  R2D1,
  R2D2,
  R2D3,
  R2D4,
  R2D5,
  R3S,
  R3D1,
  R3D2,
  R3D3,
  R3D4,
  R3D5,
  COMP_ACTY
  }
