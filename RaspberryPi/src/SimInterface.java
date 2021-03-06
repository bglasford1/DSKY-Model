/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class provides an interface to the simulators.  Both simulators
            have the same interface for ease of coding.

  Mods:		  07/15/22  Initial Release.
            07/16/22  Performance improvements.
*/
public class SimInterface extends Thread
{
  private final SocketClient socketClient = new SocketClient();
  private final DisplayInterface displayInterface = DisplayInterface.getInstance();
  private final IndicatorInterface indicatorInterface = IndicatorInterface.getInstance();

  private boolean runThread = true;

  /**
   * Initialize the interface to the simulator.
   */
  public void initInterface()
  {
    socketClient.openInterface();
  }

  /**
   * Close the interface to the simulator.
   */
  public void closeInterface()
  {
    runThread = false;
    socketClient.closeInterfaces();
  }

  /**
   * Send a key code for a key that was pressed to the AGC.  There are four packets of data sent.
   * The format for the four packets is 0000pppp 01pppdddd 10dddddd 11dddddd
   * where p = the channel number and d = the 15 bits of channel data.
   * For a keycode, the channel number is always octal 15 for DSKY #1.
   *
   * @param keycode The keycode (as defined by NASA) to send.
   */
  public void sendKeyCode(int keycode)
  {
    byte[] bytesToSend = new byte[4];
    bytesToSend[0] = (byte)0x01;
    bytesToSend[1] = (byte)0x68;
    bytesToSend[2] = (byte)0x80;
    bytesToSend[3] = (byte)(0xC0 | keycode);
    socketClient.sendData(bytesToSend);
    System.out.println("Sent Key Code: " + keycode);
  }

  /**
   * Start a thread that loops waiting on data from the AGC.  Upon data receipt,
   * decode and process the data.
   */
  public void run()
  {
    while(runThread)
    {
      getData();
    }
  }

  /**
   * Method to get data from the AGC, decode it and process it.
   */
  public void getData()
  {
    // Read an array of bytes from the interface.
    byte[] data = socketClient.receiveData();
    if (data == null)
      return;

    int index = 0;
    byte pValue = 0;
    int dValue = 0;
    for (int i = 0; i < data.length; i++)
    {
      if (index == 0)
      {
        byte indexRead = (byte)((data[i] & 0xC0) >> 6);
        if (indexRead == 0)
        {
          pValue = (byte)((data[i] & 0x3F) << 3);
        }
        index++;
      }
      else if (index == 1)
      {
        byte indexRead = (byte)((data[i] & 0xC0) >> 6);
        if (indexRead == 1)
        {
          pValue = (byte)(pValue | (data[i] & 0x38) >> 3);
          dValue = data[i] & 0x0007;
        }
        index++;
      }
      else if (index == 2)
      {
        byte indexRead = (byte)((data[i] & 0xC0) >> 6);
        if (indexRead == 2)
        {
          dValue = (dValue << 6) | (data[i] & 0x3F);
        }
        index++;
      }
      else if (index == 3)
      {
        byte indexRead = (byte)((data[i] & 0xC0) >> 6);
        if (indexRead == 3)
        {
          dValue = (dValue << 6) | (data[i] & 0x3F);
        }

        // Ignore all but channels 10, 11 and 0163 (octal).
        if (pValue == 8 || pValue == 9 || pValue == 115)
        {
          // TODO: temp code, remove...
          if (!(pValue == 8 && dValue == 0))
          {
            System.out.print("Received: Channel = " + Integer.toOctalString(pValue) + ", D = " + dValue);
            System.out.println(", Original Bytes: " + Utils.byteToHex(data[i-3]) + " " + Utils.byteToHex(data[i-2]) +
                               " " + Utils.byteToHex(data[i-1]) + " " + Utils.byteToHex(data[i]));
          }

          // Process Channel 11 - indicator status.
          if (pValue == 9)
          {
            indicatorInterface.setCompActy((dValue & 0x0002) != 0);
            indicatorInterface.setUplinkActy((dValue & 0x0004) != 0);
            indicatorInterface.setTemp((dValue & 0x0008) != 0);
            indicatorInterface.setKeyRel((dValue & 0x0010) != 0);
            indicatorInterface.setOprErr((dValue & 0x0040) != 0);
            indicatorInterface.sendOtherIndidatorsCommand();
          }
          // Process Channel 10 - display data.
          else if (pValue == 8)
          {
            if (dValue != 0)
            {
              displayInterface.setChannel10Register(dValue);
              displayInterface.decodeData();
            }
          }
          // Process pseudo channel 163 - What in the real system is Channel 10, Relay Word 12 data.
          else if (pValue == 115)
          {
            indicatorInterface.setTemp((dValue & 0x0008) != 0);
            indicatorInterface.setKeyRel((dValue & 0x0010) != 0);
            indicatorInterface.setFlashVerbNoun((dValue & 0x0020) != 0);
            indicatorInterface.setOprErr((dValue & 0x0040) != 0);
            indicatorInterface.setRestart((dValue & 0x0080) != 0);
            indicatorInterface.setStandby((dValue & 0x0100) != 0);
            indicatorInterface.sendOtherIndidatorsCommand();
          }
        }
        index = 0;
        pValue = 0;
        dValue = 0;
      }
    }
  }

  /**
   * Used to test the interface separate from everything else.
   *
   * @param args Arguments passed.
   */
  public static void main( String[] args )
  {
    SimInterface simInterface = new SimInterface();
    simInterface.initInterface();
    try
    {
      // ---------- V35E ------------------
      // Perform display test; all 8s in all digits and
      // indicators on with verb, noun, key rel & opr err flashing.
      // Write a Verb key was pressed.
//      simInterface.sendKeyCode(17);
//      System.out.println("Sent verb key...");
//      simInterface.getData();
//
//      // Write a 3 key was pressed.
//      simInterface.sendKeyCode(3);
//      System.out.println("Sent 3 key...");
//      simInterface.getData();
//
//      // Write a 5 key was pressed.
//      simInterface.sendKeyCode(5);
//      System.out.println("Sent 5 key...");
//      simInterface.getData();
//
//      // Write an Enter key was pressed.
//      simInterface.sendKeyCode(28);
//      System.out.println("Sent enter key...");


      // ---------- V36E ------------------
      // Reset display.
      // Write a Verb key was pressed.
      Thread.sleep(1000);
      simInterface.sendKeyCode(17);
      System.out.println("Sent verb key...");
//      simInterface.getData();

      // Write a 3 key was pressed.
      Thread.sleep(1000);
      simInterface.sendKeyCode(3);
      System.out.println("Sent 3 key...");
//      simInterface.getData();

      // Write a 6 key was pressed.
      simInterface.sendKeyCode(6);
      System.out.println("Sent 6 key...");
//      simInterface.getData();

      // Write an Enter key was pressed.
      Thread.sleep(1000);
      simInterface.sendKeyCode(28);
      System.out.println("Sent enter key...");

      // ---------- V16N36E ------------------
      // Count seconds in R3 register.
      // Write a Verb key was pressed.
//      simInterface.sendKeyCode(17);
//      System.out.println("Sent verb key...");
//      simInterface.getData();
//
//      // Write a 1 key was pressed.
//      simInterface.sendKeyCode(1);
//      System.out.println("Sent 1 key...");
//      simInterface.getData();
//
//      // Write a 6 key was pressed.
//      simInterface.sendKeyCode(6);
//      System.out.println("Sent 6 key...");
//      simInterface.getData();
//
//      // Write a Noun key was pressed.
//      simInterface.sendKeyCode(31);
//      System.out.println("Sent noun key...");
//      simInterface.getData();
//
//      // Write a 3 key was pressed.
//      simInterface.sendKeyCode(3);
//      System.out.println("Sent 3 key...");
//      simInterface.getData();
//
//      // Write a 6 key was pressed.
//      simInterface.sendKeyCode(6);
//      System.out.println("Sent 6 key...");
//      simInterface.getData();
//
//      // Write an Enter key was pressed.
//      simInterface.sendKeyCode(28);
//      System.out.println("Sent enter key...");

      while(true)
      {
        simInterface.getData();
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

    simInterface.closeInterface();
  }
}
