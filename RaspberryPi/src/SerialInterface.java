/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class is a singleton that provides the serial interface to the three Arduinos.  Each Arduino,
            as it connects is assigned the tty device name starting with zero.  Each possible device is opened
            and if a device is connected, an identify command is sent.  This is used to map each arduino to its
            port.  All direct interfacing to the Arduinos is encapsulated within this class.

  Mods:		  07/15/22  Initial Release.
            07/16/22  Performance improvements.
*/
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SerialInterface
{
  private static final SerialInterface instance = new SerialInterface();

  private static final String PORT0_NAME = "/dev/ttyACM0";
  private static final String PORT1_NAME = "/dev/ttyACM1";
  private static final String PORT2_NAME = "/dev/ttyACM2";
  private SerialPort keyboardPort = null;
  private SerialPort indicatorsPort = null;
  private SerialPort displayPort = null;

  private SerialInterface() { }

  public static SerialInterface getInstance()
  {
    return instance;
  }

  /**
   * Method called to initialize the interfaces to the display, indicators and keyboard
   * Arduinos.  If something is connected to a port, then a call is made to identify
   * the device.
   */
  public void initInterface()
  {
    try
    {
      SerialPort port = openPort(PORT0_NAME);
      identifyPort(port);
    }
    catch (SerialPortInvalidPortException e)
    {
      System.out.println("Port " + PORT0_NAME + " has no device connected.");
    }

    try
    {
      SerialPort port = openPort(PORT1_NAME);
      identifyPort(port);
    }
    catch (SerialPortInvalidPortException e)
    {
      System.out.println("Port " + PORT1_NAME + " has no device connected.");
    }

    try
    {
      SerialPort port = openPort(PORT2_NAME);
      identifyPort(port);
    }
    catch (SerialPortInvalidPortException e)
    {
      System.out.println("Port " + PORT2_NAME + " has no device connected.");
    }
  }

  /**
   * Internal method called to open the port specified.
   *
   * @param portName  The name of the port.
   * @return If opened, the port object.
   * @throws SerialPortInvalidPortException  An error occurred opening the port.
   */
  private SerialPort openPort(String portName) throws SerialPortInvalidPortException
  {
    SerialPort port = SerialPort.getCommPort(portName);
    port.setComPortParameters(9600, 8, 1, 0);

    if (port.openPort())
    {
      System.out.println(portName + " is open.");
    }
    else
    {
      System.out.println("Failed to open " + portName);
    }

    try
    {
      //Delay to give Arduinos time to reset.
      Thread.sleep(2000);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }

    return port;
  }

  /**
   * Internal method used to identify which Arduino is connected to the port.  Each Arduino
   * responds to a "2" command with their name as a string.
   *
   * @param port The port object to query.
   */
  private void identifyPort(SerialPort port)
  {
    // Send the identify command.
    byte[] identifyCommand = new byte[1];
    identifyCommand[0] = 50; //send identify command "2"
    port.writeBytes(identifyCommand, 1);

    // Read from the port to see which board it is.
    byte[] readBuffer = new byte[20];
    byte[] assembledInput = new byte[20];
    int nextByte = 0;
    int tries = 100;
    while (tries > 0)
    {
      try
      {
        //Delay to give Arduinos time to reset.
        Thread.sleep(100);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      int numRead = port.readBytes(readBuffer, readBuffer.length);

      if (numRead > 0)
      {
        // Assemble string until you get a CR or LF.
        for (int i = 0; i < numRead; i++)
        {
          if (readBuffer[i] == '\n' || readBuffer[i] == '\r')
          {
            tries = 0;
            break;
          }
          else
          {
            assembledInput[nextByte] = readBuffer[i];
            nextByte++;
          }
        }
      }
      tries--;
    }

    byte[] readBytes = Arrays.copyOfRange(assembledInput, 0, nextByte);
    String stringRead = new String(readBytes, StandardCharsets.UTF_8);

    if (stringRead.equalsIgnoreCase("Keyboard"))
    {
      keyboardPort = port;
      System.out.println("Keyboard Port is " + port.getSystemPortName());
    }
    else if (stringRead.equalsIgnoreCase("Indicators"))
    {
      indicatorsPort = port;
      System.out.println("Indicators Port is " + port.getSystemPortName());
    }
    else if (stringRead.equalsIgnoreCase("Display"))
    {
      displayPort = port;
      System.out.println("Display Port is " + port.getSystemPortName());
    }
  }

  /**
   * Method to send a RESET command.  There are no associated values.
   */
  public void sendReset()
  {
    try
    {
      byte[] commandToSend = new byte[] { 49 };
      displayPort.writeBytes(commandToSend, commandToSend.length);
      Thread.sleep(100);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to set/clear the COMP ACTY indicator. 0 = off, 1 = on
   *
   * @param value The value of the indicator.
   */
  public void sendCompActy(boolean value)
  {
    try
    {
      byte[] commandToSend;
      if (value)
        commandToSend = new byte[] { 52, 32, 49 };
      else
        commandToSend = new byte[] { 52, 32, 48 };
      displayPort.writeBytes(commandToSend, commandToSend.length);
      Thread.sleep(100);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method called to send a display command to the display Arduino.  The
   * entire 15 bit channel value is sent.  The format for this value is
   * xRRR RSHH HHHL LLLL where L = DSPL, H = DSPH, S = sign bit, R = relay
   * word and x = don't care.
   *
   * @param value The integer value to send.
   */
  public void sendDisplayCommand(int value)
  {
    byte[] commandToSend = new byte[] { 51, 32, 32, 32, 32, 32, 32 };
    String valueString = String.valueOf(value);
    byte[] valueBytes = valueString.getBytes(StandardCharsets.UTF_8);
    System.arraycopy(valueBytes, 0, commandToSend, 2, valueBytes.length);

    try
    {
      displayPort.writeBytes(commandToSend, commandToSend.length);
      Thread.sleep(100);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to set/clear the other indicators that originate from the channel 11 register.
   *
   * @param value The value to write.
   */
  public void sendOtherIndicatorsCommand(int value)
  {
    byte[] commandToSend = new byte[] { 52, 32, 32, 32 };
    String stringValue = String.valueOf(value);
    byte[] valueBytes = stringValue.getBytes(StandardCharsets.US_ASCII);
    for (int i = 0; i < valueBytes.length; i++)
    {
      commandToSend[2+i] = valueBytes[i];
    }
    sendCommand(commandToSend);
    try
    {
      Thread.sleep(100);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method to set/clear the display indicators that originate from the channel 10 register,
   * relay word 12.
   *
   * @param value The value to write.
   */
  public void sendDisplayIndicatorsCommand(int value)
  {
    byte[] commandToSend = new byte[] { 51, 32, 32, 32 };
    String stringValue = String.valueOf(value);
    byte[] valueBytes = stringValue.getBytes(StandardCharsets.US_ASCII);
    for (int i = 0; i < valueBytes.length; i++)
    {
      commandToSend[2+i] = valueBytes[i];
    }
    sendCommand(commandToSend);

    try
    {
      Thread.sleep(100);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method called to flash the verb and noun values.
   *
   * @param state Whether to turn the flashing on or off.
   */
  public void flashVerbNoun(boolean state)
  {
    byte[] commandToSend;
    if (state)
    {
      commandToSend = new byte[] { 53, 32, 49 };
    }
    else
    {
      commandToSend = new byte[] { 53, 32, 48 };
    }
    try
    {
      displayPort.writeBytes(commandToSend, commandToSend.length);
      Thread.sleep(100);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }

  }

  /**
   * Internal method used to send a command to the indicators Arduino.
   *
   * @param command The command to send.
   */
  private void sendCommand(byte[] command)
  {
    try
    {
      if (command != null)
      {
        indicatorsPort.writeBytes(command, command.length);
        Thread.sleep(100);
      }
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method called to read data from the keyboard Arduino.
   *
   * @return The bytes read from the interface.
   * @throws IOException An error occured reading data.
   */
  public byte[] readData() throws IOException
  {
    if (keyboardPort == null)
    {
      throw new IOException("No Keyboard found.");
    }

    byte[] readBuffer = new byte[100];
    int numRead = keyboardPort.readBytes(readBuffer, readBuffer.length);
    return Arrays.copyOfRange(readBuffer, 0, numRead);
  }

  /**
   * Method called to close all the interfaces.
   */
  public void closeInterface()
  {
    if (keyboardPort != null && keyboardPort.closePort())
    {
      System.out.println("Keyboard port is closed.");
    }
    else
    {
      System.out.println("Failed to close keyboard port.");
    }

    if (indicatorsPort != null && indicatorsPort.closePort())
    {
      System.out.println("Indicators Port is closed.");
    }
    else
    {
      System.out.println("Failed to close indicators port.");
    }

    if (displayPort != null && displayPort.closePort())
    {
      System.out.println("Display port is closed.");
    }
    else
    {
      System.out.println("Failed to close display port.");
    }
  }
}
