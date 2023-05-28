package com.exp.hitlab.usbseriallibrary;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kien on 02-Aug-18.
 */

public class USBSerialClass{ //implements UsbSerialInterface.UsbReadCallback{

    Context unityContext;

    public void USBSerialClass(Context unityContextpassed) {
        try {
            unityContext = unityContextpassed;
            Log.d("Unity", "Context object is linked");
        } catch (NullPointerException e){
            Log.d("Unity", "Null PTR Exception");
            e.printStackTrace();
        }
    }

//    public final String ACTION_USB_PERMISSION = unityContext.getPackageName()+".USB_PERMISSION";
public final String ACTION_USB_PERMISSION = "com.test.test.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;



    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    };



    public UsbSerialDevice SerialConnectionInitialization(){

        String returnMessage = "NOT OK DEVICE";
        usbManager = (UsbManager) unityContext.getSystemService(unityContext.USB_SERVICE);

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID 0403 or 2341
                {
                    PendingIntent pi = PendingIntent.getBroadcast(unityContext.getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    //usbManager.grantPermission(device);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }

        //Permisison granted
        if(device != null){
            returnMessage = "device found";
            connection = usbManager.openDevice(device);
            serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
            if (serialPort != null) {
                if (serialPort.open()) { //Set Serial Connection Parameters.
                    serialPort.setBaudRate(9600);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialPort.read(mCallback);

                } else {

                    Log.d("SERIAL", "PORT NOT OPEN");
                }
            } else {

                Log.d("SERIAL", "PORT IS NULL");
            }
        } else{

        }

        //return returnMessage;
        return serialPort;

    }
    public void SendData(UsbSerialDevice parSerialPort, String data){
        if(parSerialPort != null){
            if(data.equals("N") || data.equals("F"))
            parSerialPort.write(data.getBytes());

//            String sendText = "";
//            for(int i = 0; i < 10; i++){
//                if(i%2 == 0){
//                    sendText = "N";
//                } else{
//                    sendText = "F";
//                }
//                try {
//                    if(parSerialPort != null){
//
//                        parSerialPort.write(sendText.getBytes());
//                    }
//
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }

    }


    public void LogTesting(){
        Log.d("Unity", "In java function resident");
    }

    public int simpleSum(int inNumber){
        Log.d("Unity", "Summation is: " + (inNumber+5));
        return 1;
    }



}
