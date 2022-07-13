/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class encapsulates the socket connection.

  Mods:		  07/15/22  Initial Release.
*/
import java.io.*;
import java.net.Socket;

public class SocketClient
{
  private OutputStream out = null;
  private InputStream in = null;
  private Socket socket = null;

  /**
   * Open the socket connection to the simulator.
   */
  public void openInterface()
  {
    try
    {
      socket = new Socket( "127.0.0.1", 19697 );
      if (!socket.isConnected())
      {
        System.out.println("Connection failed to open.");
      }

      out = new DataOutputStream(socket.getOutputStream());
      in = new DataInputStream(socket.getInputStream());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Send an array of bytes over the socket.
   *
   * @param data The data to send.
   */
  public void sendData(byte[] data)
  {
    try
    {
      out.write(data);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Receive data from the socket.
   *
   * @return The array of bytes read from the socket.
   */
  public byte[] receiveData()
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      baos.write(buffer, 0 , in.read(buffer));
      return baos.toByteArray();
    }
    catch (IOException e)
    {
      return null;
    }
  }

  /**
   * Close the socket connection.
   */
  public void closeInterfaces()
  {
    try
    {
      // Close our streams
      in.close();
      out.close();
      socket.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}