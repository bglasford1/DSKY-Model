/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a utility class that contains common methods.

  Mods:		  07/15/22  Initial Release.
*/
import java.math.BigInteger;
import java.util.BitSet;

public class Utils
{
  /**
   * Method to convert a bitset to an integer value.
   *
   * @param bitSet The bitset to convert.
   * @return The integer value.
   */
  public static int toInt(BitSet bitSet)
  {
    int intValue = 0;
    for (int bit = 0; bit < bitSet.length(); bit++)
    {
      if (bitSet.get(bit))
      {
        intValue |= (1 << bit);
      }
    }
    return intValue;
  }

  // TODO: temp for debug purposes <-- REMOVE
  public static String byteToHex(byte num)
  {
    char[] hexDigits = new char[2];
    hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
    hexDigits[1] = Character.forDigit((num & 0xF), 16);
    return new String(hexDigits);
  }

  public static byte[] intToByteArray( final int i )
  {
    BigInteger bigInt = BigInteger.valueOf(i);
    return bigInt.toByteArray();
  }
}
