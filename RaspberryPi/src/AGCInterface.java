/*
  Copyright 2022, William Glasford

  This file is part of the DSKY Model.  You can redistribute it
  and/or modify it under the terms of the GNU General Public License as
  published by the Free Software Foundation; either version 3 of the License,
  or any later version. This software is distributed without any warranty
  expressed or implied. See the GNU General Public License for more details.

  Purpose:	This class provides the interface to the hardware AGC via the channel bus and various control lines.
            The interface to this connector is via Raspberry Pi GPIO pins.  All activity related to the GPIO
            pins is encapsulated within this class.  Each control pin has an event listener that when the pin
            is asserted, the appropriate action is taken.  Notice that these control pins use inverse logic.
            The Channel bus is a bidirectional bus.  The bus's pins are normally kept in an input mode.  If
            keyboard data is to be sent back to the AGC, it is requested by the AGC and the pin modes are
            changed to output, the data is sent and the process sleeps long enough for the AGC to read the
            data before the pin modes are changed back to input.

  Mods:		  07/15/22  Initial Release.
*/
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.BitSet;

/**
 *   This interface encapsulates the Raspberry Pi GPIO interface.  Since this only functions on a Raspberry Pi,
 *   encapsulating this interface allows for a test package to replace this package.
 *
 *   The AGC interface uses the following pins:
 *
 *   Assignment		  GPIO Pin
 *   Channel Bus 1			4
 *   Channel Bus 2			5
 *   Channel Bus 3			6
 *   Channel Bus 4			7
 *   Channel Bus 5			8
 *   Channel Bus 6			9
 *   Channel Bus 7			10
 *   Channel Bus 8			11
 *   Channel Bus 9			12
 *   Channel Bus 10	  	13
 *   Channel Bus 11	  	14
 *   Channel Bus 12	  	15
 *   Channel Bus 13	  	16
 *   Channel Bus 14		  17
 *   Channel Bus 15		  18
 *   CLK1			      	  19
 *   RST				        20
 *   STBY				        21
 *   KBD1				        22
 *   DISP 			      	23
 *   INDC				        24
 *   RPRO				        25
 *   KB_STR			        26
 *   PARALM			        27
 */
public class AGCInterface
{
  private static final Pin CH1_PIN = RaspiPin.GPIO_04;
  private static final Pin CH2_PIN = RaspiPin.GPIO_05;
  private static final Pin CH3_PIN = RaspiPin.GPIO_06;
  private static final Pin CH4_PIN = RaspiPin.GPIO_07;
  private static final Pin CH5_PIN = RaspiPin.GPIO_08;
  private static final Pin CH6_PIN = RaspiPin.GPIO_09;
  private static final Pin CH7_PIN = RaspiPin.GPIO_10;
  private static final Pin CH8_PIN = RaspiPin.GPIO_11;
  private static final Pin CH9_PIN = RaspiPin.GPIO_12;
  private static final Pin CH10_PIN = RaspiPin.GPIO_13;
  private static final Pin CH11_PIN = RaspiPin.GPIO_14;
  private static final Pin CH12_PIN = RaspiPin.GPIO_15;
  private static final Pin CH13_PIN = RaspiPin.GPIO_16;
  private static final Pin CH14_PIN = RaspiPin.GPIO_17;
  private static final Pin CH15_PIN = RaspiPin.GPIO_18;

  private static final Pin CLK1_PIN = RaspiPin.GPIO_19;
  private static final Pin RST_PIN = RaspiPin.GPIO_20;
  private static final Pin STBY_PIN = RaspiPin.GPIO_21;
  private static final Pin KBD1_PIN = RaspiPin.GPIO_22;
  private static final Pin DISP_PIN = RaspiPin.GPIO_23;
  private static final Pin INDC_PIN = RaspiPin.GPIO_24;
  private static final Pin RPRO_PIN = RaspiPin.GPIO_25;
  private static final Pin KB_STR_PIN = RaspiPin.GPIO_26;
  private static final Pin PARALM_PIN = RaspiPin.GPIO_27;

  private GpioPinDigitalInput clk1;
  private GpioPinDigitalInput rst;
  private GpioPinDigitalInput stby;
  private GpioPinDigitalInput kbd1;
  private GpioPinDigitalInput disp;
  private GpioPinDigitalInput indc;
  private GpioPinDigitalInput rpro;
  private GpioPinDigitalOutput kb_str;
  private GpioPinDigitalInput paralm;

  private GpioPinDigitalMultipurpose channelBit1;
  private GpioPinDigitalMultipurpose channelBit2;
  private GpioPinDigitalMultipurpose channelBit3;
  private GpioPinDigitalMultipurpose channelBit4;
  private GpioPinDigitalMultipurpose channelBit5;
  private GpioPinDigitalMultipurpose channelBit6;
  private GpioPinDigitalMultipurpose channelBit7;
  private GpioPinDigitalMultipurpose channelBit8;
  private GpioPinDigitalMultipurpose channelBit9;
  private GpioPinDigitalMultipurpose channelBit10;
  private GpioPinDigitalMultipurpose channelBit11;
  private GpioPinDigitalMultipurpose channelBit12;
  private GpioPinDigitalMultipurpose channelBit13;
  private GpioPinDigitalMultipurpose channelBit14;
  private GpioPinDigitalMultipurpose channelBit15;

  private final GpioController gpio = GpioFactory.getInstance();
  private final DisplayInterface displayInterface = DisplayInterface.getInstance();
  private final IndicatorInterface indicatorInterface = IndicatorInterface.getInstance();
  private final KeyboardInterface keyboardInterface = KeyboardInterface.getInstance();

  public void init()
  {
    // Provision the Channel Bus pins.
    channelBit1 = gpio.provisionDigitalMultipurposePin(CH1_PIN,PinMode.DIGITAL_INPUT);
    channelBit2 = gpio.provisionDigitalMultipurposePin(CH2_PIN,PinMode.DIGITAL_INPUT);
    channelBit3 = gpio.provisionDigitalMultipurposePin(CH3_PIN,PinMode.DIGITAL_INPUT);
    channelBit4 = gpio.provisionDigitalMultipurposePin(CH4_PIN,PinMode.DIGITAL_INPUT);
    channelBit5 = gpio.provisionDigitalMultipurposePin(CH5_PIN,PinMode.DIGITAL_INPUT);
    channelBit6 = gpio.provisionDigitalMultipurposePin(CH6_PIN,PinMode.DIGITAL_INPUT);
    channelBit7 = gpio.provisionDigitalMultipurposePin(CH7_PIN,PinMode.DIGITAL_INPUT);
    channelBit8 = gpio.provisionDigitalMultipurposePin(CH8_PIN,PinMode.DIGITAL_INPUT);
    channelBit9 = gpio.provisionDigitalMultipurposePin(CH9_PIN,PinMode.DIGITAL_INPUT);
    channelBit10 = gpio.provisionDigitalMultipurposePin(CH10_PIN,PinMode.DIGITAL_INPUT);
    channelBit11 = gpio.provisionDigitalMultipurposePin(CH11_PIN,PinMode.DIGITAL_INPUT);
    channelBit12 = gpio.provisionDigitalMultipurposePin(CH12_PIN,PinMode.DIGITAL_INPUT);
    channelBit13 = gpio.provisionDigitalMultipurposePin(CH13_PIN,PinMode.DIGITAL_INPUT);
    channelBit14 = gpio.provisionDigitalMultipurposePin(CH14_PIN,PinMode.DIGITAL_INPUT);
    channelBit15 = gpio.provisionDigitalMultipurposePin(CH15_PIN,PinMode.DIGITAL_INPUT);

    // Provision the various control pins as input or output.
    clk1 = gpio.provisionDigitalInputPin(CLK1_PIN);
    rst = gpio.provisionDigitalInputPin(RST_PIN);
    stby = gpio.provisionDigitalInputPin(STBY_PIN);
    kbd1 = gpio.provisionDigitalInputPin(KBD1_PIN);
    disp = gpio.provisionDigitalInputPin(DISP_PIN);
    indc = gpio.provisionDigitalInputPin(INDC_PIN);
    rpro = gpio.provisionDigitalInputPin(RPRO_PIN);
    kb_str = gpio.provisionDigitalOutputPin(KB_STR_PIN, PinState.LOW);
    paralm = gpio.provisionDigitalInputPin(PARALM_PIN);

    // Create a CLK1 listener.
    clk1.addListener((GpioPinListenerDigital) event ->
    {
      System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
    });

    // Create a DISP listener.
    disp.addListener((GpioPinListenerDigital) event ->
    {
      // TODO: For each of these control pins, use inverse logic.......

      // Read Channel bus and send data to DisplayInterface.
      // Note: The Channel bus should be set to input unless a write is occurring.
      displayInterface.setChannel10Bit(1, channelBit1.getState().isHigh());
      displayInterface.setChannel10Bit(2, channelBit2.getState().isHigh());
      displayInterface.setChannel10Bit(3, channelBit3.getState().isHigh());
      displayInterface.setChannel10Bit(4, channelBit4.getState().isHigh());
      displayInterface.setChannel10Bit(5, channelBit5.getState().isHigh());
      displayInterface.setChannel10Bit(6, channelBit6.getState().isHigh());
      displayInterface.setChannel10Bit(7, channelBit7.getState().isHigh());
      displayInterface.setChannel10Bit(8, channelBit8.getState().isHigh());
      displayInterface.setChannel10Bit(9, channelBit9.getState().isHigh());
      displayInterface.setChannel10Bit(10, channelBit10.getState().isHigh());
      displayInterface.setChannel10Bit(11, channelBit11.getState().isHigh());
      displayInterface.setChannel10Bit(12, channelBit12.getState().isHigh());
      displayInterface.setChannel10Bit(13, channelBit13.getState().isHigh());
      displayInterface.setChannel10Bit(14, channelBit14.getState().isHigh());
      displayInterface.setChannel10Bit(15, channelBit15.getState().isHigh());

      displayInterface.decodeData();
    });

    // Create a DISP listener.
    indc.addListener((GpioPinListenerDigital) event ->
    {
      // Read Channel bus and send data to IndicatorInterface.
      // Note: The Channel bus should be set to input unless a write is occurring.
      indicatorInterface.setCompActy(channelBit2.getState().isHigh());
      indicatorInterface.setUplinkActy(channelBit3.getState().isHigh());
      indicatorInterface.setTemp(channelBit4.getState().isHigh());
      indicatorInterface.setKeyRel(channelBit5.getState().isHigh());
      indicatorInterface.setFlashVerbNoun(channelBit6.getState().isHigh());
      indicatorInterface.setOprErr(channelBit7.getState().isHigh());
    });

    // Create a PARALM listener.
    paralm.addListener((GpioPinListenerDigital) event ->
    {
      // Read pin state and send data to IndicatorInterface.
      indicatorInterface.setParalm(event.getState().isHigh());
    });

    // Create a RST listener.
    rst.addListener((GpioPinListenerDigital) event ->
    {
      // Blank displays and clear out persistant data.
      displayInterface.resetDisplay();
      indicatorInterface.resetDisplay();
    });

    // Create a STBY listener.
    stby.addListener((GpioPinListenerDigital) event ->
    {
      // TODO: Blank display and indicators if standby pin is low.
    });

    // Create a KBD1 listener.
    kbd1.addListener((GpioPinListenerDigital) event ->
    {
      setChannelBusDirection(PinMode.DIGITAL_OUTPUT);
      BitSet data = keyboardInterface.getChannnel15Data();
      channelBit1.setState(data.get(0));
      channelBit2.setState(data.get(1));
      channelBit3.setState(data.get(2));
      channelBit4.setState(data.get(3));
      channelBit5.setState(data.get(4));
      try
      {
        Thread.sleep(100);
      }
      catch (InterruptedException e)
      {
        System.out.println("Sleep interrupted.");
      }
      setChannelBusDirection(PinMode.DIGITAL_INPUT);
    });

    // Create a RPRO listener.
    rpro.addListener((GpioPinListenerDigital) event ->
    {
      // Note: this bit is inverse logic.
      channelBit14.setState(true);
      if (keyboardInterface.isProKeyPressed())
      {
        setChannelBusDirection(PinMode.DIGITAL_OUTPUT);
        channelBit14.setState(false);
      }
    });
  }

  public void assertKbStr()
  {
    kb_str.pulse(100, true);
  }

  private void setChannelBusDirection(PinMode mode)
  {
    channelBit1.setMode(mode);
    channelBit2.setMode(mode);
    channelBit3.setMode(mode);
    channelBit4.setMode(mode);
    channelBit5.setMode(mode);
    channelBit6.setMode(mode);
    channelBit7.setMode(mode);
    channelBit8.setMode(mode);
    channelBit9.setMode(mode);
    channelBit10.setMode(mode);
    channelBit11.setMode(mode);
    channelBit12.setMode(mode);
    channelBit13.setMode(mode);
    channelBit14.setMode(mode);
    channelBit15.setMode(mode);
  }
}
