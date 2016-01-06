/*
 * Titan Robotics Framework Library
 * Copyright (c) 2015 Michael H. Tsang (http://www.titanrobotics.net)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ftclib;

import com.qualcomm.robotcore.hardware.HardwareMap;

import trclib.TrcDbgTrace;
import trclib.TrcI2cDevice;
import trclib.TrcSensor;

/**
 * This class implements the Modern Robotics Gyro extending FtcI2cDevice.
 * It provides the TrcI2cDevice.CompletionHandler interface to read the
 * received data.
 */
public class FtcMRI2cGyro extends FtcI2cDevice implements TrcI2cDevice.CompletionHandler
{
    private static final String moduleName = "FtcMRI2cGyro";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    public static final int DEF_I2CADDRESS          = 0x20;

    //
    // I2C registers.
    //
    private static final int REG_FIRMWARE_REVISION  = 0x00;
    private static final int REG_MANUFACTURER_CODE  = 0x01;
    private static final int REG_ID_CODE            = 0x02;
    private static final int REG_COMMAND            = 0x03;
    private static final int REG_HEADING_LSB        = 0x04;
    private static final int REG_HEADING_MSB        = 0x05;
    private static final int REG_INTEGRATED_Z_LSB   = 0x06;
    private static final int REG_INTEGRATED_Z_MSB   = 0x07;
    private static final int REG_RAW_X_LSB          = 0x08;
    private static final int REG_RAW_X_MSB          = 0x09;
    private static final int REG_RAW_Y_LSB          = 0x0a;
    private static final int REG_RAW_Y_MSB          = 0x0b;
    private static final int REG_RAW_Z_LSB          = 0x0c;
    private static final int REG_RAW_Z_MSB          = 0x0d;
    private static final int REG_Z_OFFSET_LSB       = 0x0e;
    private static final int REG_Z_OFFSET_MSB       = 0x0f;
    private static final int REG_Z_SCALING_LSB      = 0x10;
    private static final int REG_Z_SCALING_MSB      = 0x11;

    private static final int CMD_MEASUREMENT_MODE   = 0x00;
    private static final int CMD_RESET_OFFSET_CAL   = 0x4e;
    private static final int CMD_RESET_Z_INTEGRATOR = 0x52;
    private static final int CMD_WRITE_EEPROM_DATA  = 0x57;

    private int firmwareRev = 0;
    private int manufacturerCode = 0;
    private int idCode = 0;
    private TrcSensor.SensorData heading = null;
    private TrcSensor.SensorData integratedZ = null;
    private TrcSensor.SensorData rawX = null;
    private TrcSensor.SensorData rawY = null;
    private TrcSensor.SensorData rawZ = null;
    private TrcSensor.SensorData zOffset = null;
    private TrcSensor.SensorData zScaling = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param hardwareMap specifies the global hardware map.
     * @param instanceName specifies the instance name.
     * @param i2cAddress specifies the I2C address of the device.
     */
    public FtcMRI2cGyro(HardwareMap hardwareMap, String instanceName, int i2cAddress)
    {
        super(hardwareMap, instanceName, i2cAddress);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        resetZIntegrator();
        read(REG_FIRMWARE_REVISION, REG_ID_CODE - REG_FIRMWARE_REVISION + 1, this);
        read(REG_HEADING_LSB, REG_Z_SCALING_MSB - REG_HEADING_LSB + 1, this);
    }   //FtcMRI2cGyro

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param i2cAddress specifies the I2C address of the device.
     */
    public FtcMRI2cGyro(String instanceName, int i2cAddress)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, i2cAddress);
    }   //FtcMRI2cGyro

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public FtcMRI2cGyro(String instanceName)
    {
        this(instanceName, DEF_I2CADDRESS);
    }   //FtcMRI2cGyro

    public void resetZIntegrator()
    {
        byte[] data = {CMD_RESET_Z_INTEGRATOR};
        sendWriteCommand(REG_COMMAND, 1, data);
    }   //resetZIntegrator

    public int getFirmwareRevision()
    {
        return firmwareRev;
    }   //getFirmwareRevision

    public int getManufacturerCode()
    {
        return manufacturerCode;
    }   //getManufacturerCode

    public int getIdCode()
    {
        return idCode;
    }   //getManufacturerCode

    public TrcSensor.SensorData getHeading()
    {
        return heading;
    }   //getHeading

    public TrcSensor.SensorData getIntegratedZ()
    {
        return integratedZ;
    }   //getIntegratedZ

    public TrcSensor.SensorData getRawX()
    {
        return rawX;
    }   //getRawX

    public TrcSensor.SensorData getRawY()
    {
        return rawY;
    }   //getRawY

    public TrcSensor.SensorData getRawZ()
    {
        return rawZ;
    }   //getRawZ

    public TrcSensor.SensorData getZOffset()
    {
        return zOffset;
    }   //getZOffset

    public TrcSensor.SensorData getZScaling()
    {
        return zScaling;
    }   //getZScaling

    //
    // Implements TrcI2cDevice.CompletionHandler interface.
    //

    /**
     * This method is called to notify the completion of the read operation.
     *
     * @param timestamp specified the timestamp of the data retrieved.
     * @param regAddress specifies the starting register address.
     * @param length specifies the number of bytes read.
     * @param data specifies the data byte array.
     * @return true to repeat the operation, false otherwise.
     */
    @Override
    public boolean readCompletion(double timestamp, int regAddress, int length, byte[] data)
    {
        final String funcName = "readCompletion";
        boolean repeat = false;

        if (debugEnabled)
        {
            dbgTrace.traceInfo(funcName, "readCompletion(t=%.3f,addr=%x,length=%d,size=%d",
                               timestamp, regAddress, length, data.length);
            dbgTrace.traceInfo(funcName, "data=%s", data.toString());
        }

        if (regAddress == REG_FIRMWARE_REVISION)
        {
            //
            // These only need to be read once, so no repeat.
            //
            firmwareRev = data[REG_FIRMWARE_REVISION] & 0xff;
            manufacturerCode = data[REG_MANUFACTURER_CODE] & 0xff;
            idCode = data[REG_ID_CODE] & 0xff;
        }
        else if (regAddress == REG_HEADING_LSB)
        {
            //
            // Read these repeatedly.
            //
            heading = new TrcSensor.SensorData(
                    timestamp,
                    (int)(short)((data[REG_HEADING_LSB - REG_HEADING_LSB] & 0xff) |
                                 ((data[REG_HEADING_MSB - REG_HEADING_LSB] & 0xff) << 8)));
            integratedZ = new TrcSensor.SensorData(
                    timestamp,
                    (int)(short)((data[REG_INTEGRATED_Z_LSB - REG_HEADING_LSB] & 0xff) |
                                 ((data[REG_INTEGRATED_Z_MSB - REG_HEADING_LSB] & 0xff) << 8)));
            rawX = new TrcSensor.SensorData(
                    timestamp,
                    (int)(short)((data[REG_RAW_X_LSB - REG_HEADING_LSB] & 0xff) |
                                 ((data[REG_RAW_X_MSB - REG_HEADING_LSB] & 0xff) << 8)));
            rawY = new TrcSensor.SensorData(
                    timestamp,
                    (int)(short)((data[REG_RAW_Y_LSB - REG_HEADING_LSB] & 0xff) |
                                 ((data[REG_RAW_Y_MSB - REG_HEADING_LSB] & 0xff) << 8)));
            rawZ = new TrcSensor.SensorData(
                    timestamp,
                    (int)(short)((data[REG_RAW_Z_LSB - REG_HEADING_LSB] & 0xff) |
                                 ((data[REG_RAW_Z_MSB - REG_HEADING_LSB] & 0xff) << 8)));
            zOffset = new TrcSensor.SensorData(
                    timestamp,
                    (int)(short)((data[REG_Z_OFFSET_LSB - REG_HEADING_LSB] & 0xff) |
                                 ((data[REG_Z_OFFSET_MSB - REG_HEADING_LSB] & 0xff) << 8)));
            zScaling = new TrcSensor.SensorData(
                    timestamp,
                    (int)(short)((data[REG_Z_SCALING_LSB - REG_HEADING_LSB] & 0xff) |
                                 ((data[REG_Z_SCALING_MSB - REG_HEADING_LSB] & 0xff) << 8)));
            repeat = true;
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.CALLBK,
                                "timestamp=%.3f,regAddr=%x,len=%d", timestamp, regAddress, length);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.CALLBK,
                               "=%s", Boolean.toString(repeat));
        }

        return repeat;
    }   //readCompletion

    /**
     * This method is called to notify the completion of the write operation.
     */
    @Override
    public void writeCompletion()
    {
    }   //writeCompletion

}   //class FtcMRI2cGyro
