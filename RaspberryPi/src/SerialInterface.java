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
   * Method called to send a display command to the display Arduino.
   * For numbers, the format is command number followed by the actual number, all in ASCII. Ex: "4 1"
   * For signs, the format is same as above except value is -1 = blank, 0 = plus, 1 = minus.
   * For Comp Acty light, 0 = off, 1 = on.
   *
   * @param command The display command to send.
   * @param value The optional data value to send along with the command.
   */
  public void sendDisplayCommand(DisplayCommand command, int value)
  {
    byte[] commandToSend = null;

    // Convert command to ASCII number.  Note: if value = -1 then send a value of 32 or blank.
    if (command == DisplayCommand.RESET)
    {
      commandToSend = new byte[] { 49 };
    }
    else if (command == DisplayCommand.MD1)
    {
      commandToSend = new byte[] { 51, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.MD2)
    {
      commandToSend = new byte[] { 52, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.VD1)
    {
      commandToSend = new byte[] { 53, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.VD2)
    {
      commandToSend = new byte[] { 54, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.ND1)
    {
      commandToSend = new byte[] { 55, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.ND2)
    {
      commandToSend = new byte[] { 56, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R1S)
    {
      commandToSend = new byte[] { 57, 32, 0 };
      if (value == 0)
        commandToSend[2] = 48;
      else if (value == 1)
        commandToSend[2] = 49;
      else if (value == 2)
        commandToSend[2] = 50;
    }
    else if (command == DisplayCommand.R1D1)
    {
      commandToSend = new byte[] { 49, 48, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R1D2)
    {
      commandToSend = new byte[] { 49, 49, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R1D3)
    {
      commandToSend = new byte[] { 49, 50, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R1D4)
    {
      commandToSend = new byte[] { 49, 51, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R1D5)
    {
      commandToSend = new byte[] { 49, 52, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R2S)
    {
      commandToSend = new byte[] { 49, 53, 32, 0 };
      if (value == 0)
        commandToSend[3] = 48;
      else if (value == 1)
        commandToSend[3] = 49;
      else if (value == 2)
        commandToSend[3] = 50;
    }
    else if (command == DisplayCommand.R2D1)
    {
      commandToSend = new byte[] { 49, 54, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R2D2)
    {
      commandToSend = new byte[] { 49, 55, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R2D3)
    {
      commandToSend = new byte[] { 49, 56, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R2D4)
    {
      commandToSend = new byte[] { 49, 57, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R2D5)
    {
      commandToSend = new byte[] { 50, 48, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R3S)
    {
      commandToSend = new byte[] { 50, 49, 32, 0 };
      if (value == 0)
        commandToSend[3] = 48;
      else if (value == 1)
        commandToSend[3] = 49;
      else if (value == 2)
        commandToSend[3] = 50;
    }
    else if (command == DisplayCommand.R3D1)
    {
      commandToSend = new byte[] { 50, 50, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R3D2)
    {
      commandToSend = new byte[] { 50, 51, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R3D3)
    {
      commandToSend = new byte[] { 50, 52, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R3D4)
    {
      commandToSend = new byte[] { 50, 53, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.R3D5)
    {
      commandToSend = new byte[] { 50, 54, 32, (byte)(48 + value) };
    }
    else if (command == DisplayCommand.COMP_ACTY)
    {
      commandToSend = new byte[] { 50, 55, 32, (byte)(48 + value) };
    }

    try
    {
      if (commandToSend != null)
      {
        displayPort.writeBytes(commandToSend, commandToSend.length);
        Thread.sleep(60);
      }
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Method called to send a command to the indicator panel.
   *
   * @param command The command to send.
   * @param value The optional data value to send along with the command.
   */
  public void sendIndicatorCommand(IndicatorCommand command, boolean value)
  {
    byte[] commandToSend = null;

    // Convert command to ASCII number.
    if (command == IndicatorCommand.RESET)
    {
      commandToSend = new byte[] { 49 };
    }
    else if (command == IndicatorCommand.UPLINK_ACTY)
    {
      commandToSend = new byte[] { 51, 32, 0 };
      if (value)
        commandToSend[2] = 49;
      else
        commandToSend[2] = 48;
    }
    else if (command == IndicatorCommand.NO_ATT)
    {
      commandToSend = new byte[] { 52, 32, 0 };
      if (value)
        commandToSend[2] = 49;
      else
        commandToSend[2] = 48;
    }
    else if (command == IndicatorCommand.STBY)
    {
      commandToSend = new byte[] { 53, 32, 0 };
      if (value)
        commandToSend[2] = 49;
      else
        commandToSend[2] = 48;
    }
    else if (command == IndicatorCommand.KEY_REL)
    {
      commandToSend = new byte[] { 54, 32, 0 };
      if (value)
        commandToSend[2] = 49;
      else
        commandToSend[2] = 48;
    }
    else if (command == IndicatorCommand.OPR_ERR)
    {
      commandToSend = new byte[] { 55, 32, 0 };
      if (value)
        commandToSend[2] = 49;
      else
        commandToSend[2] = 48;
    }
    else if (command == IndicatorCommand.TEMP)
    {
      commandToSend = new byte[] { 56, 32, 0 };
      if (value)
        commandToSend[2] = 49;
      else
        commandToSend[2] = 48;
    }
    else if (command == IndicatorCommand.GIMBAL_LOCK)
    {
      commandToSend = new byte[] { 57, 32, 0 };
      if (value)
        commandToSend[2] = 49;
      else
        commandToSend[2] = 48;
    }
    else if (command == IndicatorCommand.PROG)
    {
      commandToSend = new byte[] { 49, 48, 32, 0 };
      if (value)
        commandToSend[3] = 49;
      else
        commandToSend[3] = 48;
    }
    else if (command == IndicatorCommand.RESTART)
    {
      commandToSend = new byte[] { 49, 49, 32, 0 };
      if (value)
        commandToSend[3] = 49;
      else
        commandToSend[3] = 48;
    }
    else if (command == IndicatorCommand.TRACKER)
    {
      commandToSend = new byte[] { 49, 50, 32, 0 };
      if (value)
        commandToSend[3] = 49;
      else
        commandToSend[3] = 48;
    }
    else if (command == IndicatorCommand.ALT)
    {
      commandToSend = new byte[] { 49, 51, 32, 0 };
      if (value)
        commandToSend[3] = 49;
      else
        commandToSend[3] = 48;
    }
    else if (command == IndicatorCommand.VEL)
    {
      commandToSend = new byte[] { 49, 52, 32, 0 };
      if (value)
        commandToSend[3] = 49;
      else
        commandToSend[3] = 48;
    }

    try
    {
      if (commandToSend != null)
      {
        indicatorsPort.writeBytes(commandToSend, commandToSend.length);
        Thread.sleep(60);
      }
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
    // TODO: implement
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
