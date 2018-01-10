package com.example.user.ipcamera;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by user on 2018-01-10.
 */
public class UsbCommunicationManager
{
    static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    UsbManager usbManager;
    UsbDevice usbDevice;
    UsbInterface intf = null;
    UsbEndpoint input, output;
    UsbDeviceConnection connection;

    PendingIntent permissionIntent;

    Context context;

    byte[] readBytes = new byte[64];

    public UsbCommunicationManager(Context context)
    {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        // ask permission from user to use the usb device
        permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbReceiver, filter);
    }

    public void connect()
    {
        // check if there's a connected usb device
        if(usbManager.getDeviceList().isEmpty())
        {
            Log.d("trebla", "No connected devices");
            return;
        }

        // get the first (only) connected device
        usbDevice = usbManager.getDeviceList().values().iterator().next();

        // user must approve of connection
        usbManager.requestPermission(usbDevice, permissionIntent);
    }

    public void stop()
    {
        context.unregisterReceiver(usbReceiver);
    }

    public String send(String data)
    {
        if(usbDevice == null)
        {
            return "no usb device selected";
        }

        int sentBytes = 0;
        if(!data.equals(""))
        {
            synchronized(this)
            {
                // send data to usb device
                byte[] bytes = data.getBytes();
                sentBytes = connection.bulkTransfer(output, bytes, bytes.length, 1000);
            }
        }

        return Integer.toString(sentBytes);
    }

    public String read()
    {
        // reinitialize read value byte array
        Arrays.fill(readBytes, (byte) 0);

        // wait for some data from the mcu
        int recvBytes = connection.bulkTransfer(input, readBytes, readBytes.length, 3000);

        if(recvBytes > 0)
        {
            Log.d("trebla", "Got some data: " + new String(readBytes));
        }
        else
        {
            Log.d("trebla", "Did not get any data: " + recvBytes);
        }

        return Integer.toString(recvBytes);
    }

    public String listUsbDevices()
    {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        if(deviceList.size() == 0)
        {
            return "no usb devices found";
        }

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        String returnValue = "";
        UsbInterface usbInterface;

        while(deviceIterator.hasNext())
        {
            UsbDevice device = deviceIterator.next();
            returnValue += "Name: " + device.getDeviceName();
            returnValue += "\nID: " + device.getDeviceId();
            returnValue += "\nProtocol: " + device.getDeviceProtocol();
            returnValue += "\nClass: " + device.getDeviceClass();
            returnValue += "\nSubclass: " + device.getDeviceSubclass();
            returnValue += "\nProduct ID: " + device.getProductId();
            returnValue += "\nVendor ID: " + device.getVendorId();
            returnValue += "\nInterface count: " + device.getInterfaceCount();

            for(int i = 0; i < device.getInterfaceCount(); i++)
            {
                usbInterface = device.getInterface(i);
                returnValue += "\n  Interface " + i;
                returnValue += "\n\tInterface ID: " + usbInterface.getId();
                returnValue += "\n\tClass: " + usbInterface.getInterfaceClass();
                returnValue += "\n\tProtocol: " + usbInterface.getInterfaceProtocol();
                returnValue += "\n\tSubclass: " + usbInterface.getInterfaceSubclass();
                returnValue += "\n\tEndpoint count: " + usbInterface.getEndpointCount();

                for(int j = 0; j < usbInterface.getEndpointCount(); j++)
                {
                    returnValue += "\n\t  Endpoint " + j;
                    returnValue += "\n\t\tAddress: " + usbInterface.getEndpoint(j).getAddress();
                    returnValue += "\n\t\tAttributes: " + usbInterface.getEndpoint(j).getAttributes();
                    returnValue += "\n\t\tDirection: " + usbInterface.getEndpoint(j).getDirection();
                    returnValue += "\n\t\tNumber: " + usbInterface.getEndpoint(j).getEndpointNumber();
                    returnValue += "\n\t\tInterval: " + usbInterface.getEndpoint(j).getInterval();
                    returnValue += "\n\t\tType: " + usbInterface.getEndpoint(j).getType();
                    returnValue += "\n\t\tMax packet size: " + usbInterface.getEndpoint(j).getMaxPacketSize();
                }
            }
        }

        return returnValue;
    }

    private void setupConnection()
    {
        // find the right interface
        for(int i = 0; i < usbDevice.getInterfaceCount(); i++)
        {
            // communications device class (CDC) type device
            if(usbDevice.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA)
            {
                intf = usbDevice.getInterface(i);

                // find the endpoints
                for(int j = 0; j < intf.getEndpointCount(); j++)
                {
                    if(intf.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_OUT && intf.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
                    {
                        // from android to device
                        output = intf.getEndpoint(j);
                    }

                    if(intf.getEndpoint(j).getDirection() == UsbConstants.USB_DIR_IN && intf.getEndpoint(j).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
                    {
                        // from device to android
                        input = intf.getEndpoint(j);
                    }
                }
            }
        }
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(ACTION_USB_PERMISSION.equals(action))
            {
                // broadcast is like an interrupt and works asynchronously with the class, it must be synced just in case
                synchronized(this)
                {
                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        setupConnection();

                        connection = usbManager.openDevice(usbDevice);
                        connection.claimInterface(intf, true);

                        // set flow control to 8N1 at 9600 baud
                        int baudRate = 9600;
                        byte stopBitsByte = 1;
                        byte parityBitesByte = 0;
                        byte dataBits = 8;
                        byte[] msg = {
                                (byte) (baudRate & 0xff),
                                (byte) ((baudRate >> 8) & 0xff),
                                (byte) ((baudRate >> 16) & 0xff),
                                (byte) ((baudRate >> 24) & 0xff),
                                stopBitsByte,
                                parityBitesByte,
                                (byte) dataBits
                        };

                        connection.controlTransfer(UsbConstants.USB_TYPE_CLASS | 0x01, 0x20, 0, 0, msg, msg.length, 5000);
                    }
                    else
                    {
                        Log.d("trebla", "Permission denied for USB device");
                    }
                }
            }
            else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
            {
                Log.d("trebla", "USB device detached");
            }
        }
    };
}