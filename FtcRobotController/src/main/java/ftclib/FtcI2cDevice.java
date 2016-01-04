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
import com.qualcomm.robotcore.hardware.I2cDevice;

import trclib.TrcDbgTrace;
import trclib.TrcI2cDevice;

/**
 * This class implements a platform dependent I2C device.
 * extending TrcI2cDevice. It provides implementation of the
 * abstract methods in TrcI2cDevice.
 */
public class FtcI2cDevice extends TrcI2cDevice
{
    private static final String moduleName = "FtcI2cDevice";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private int i2cAddress;
    private I2cDevice sensor;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param hardwareMap specifies the global hardware map.
     * @param instanceName specifies the instance name.
     * @param i2cAddress specifies the I2C address of the device.
     */
    public FtcI2cDevice(HardwareMap hardwareMap, String instanceName, int i2cAddress)
    {
        super(instanceName);

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.i2cAddress = i2cAddress;
        sensor = hardwareMap.i2cDevice.get(instanceName);
    }   //FtcI2cDevice

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param i2cAddress specifies the I2C address of the device.
     */
    public FtcI2cDevice(String instanceName, int i2cAddress)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, i2cAddress);
    }   //FtcI2cDevice

    //
    // Implements TrcI2cDevice abstract methods.
    //

    /**
     * This method checks if the I2C port is ready for bus transaction.
     *
     * @return true if port is ready, false otherwise.
     */
    @Override
    public boolean isPortReady()
    {
        final String funcName = "isPortReady";
        boolean ready = sensor.isI2cPortReady();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(ready));
        }

        return ready;
    }   //isPortReady

    /**
     * This method checks if the I2C port is in write mode.
     *
     * @return true if port is in write mode, false otherwise.
     */
    @Override
    public boolean isPortInWriteMode()
    {
        final String funcName = "isPortInWriteMode";
        boolean writeMode = sensor.isI2cPortInWriteMode();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(writeMode));
        }

        return writeMode;
    }   //isPortInWriteMode

    /**
     * This method sets up the port for a read operation.
     *
     * @param regAddress specifies the register address.
     * @param length specifies the number of bytes to read.
     */
    @Override
    public void setupReadCommand(int regAddress, int length)
    {
        final String funcName = "setupReadCommand";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "addr=%x,len=%d", regAddress, length);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        sensor.enableI2cReadMode(i2cAddress, regAddress, length);
    }   //setupReadCommand

    /**
     * This method sets up the port for a write operation.
     *
     * @param regAddress specifies the register address.
     * @param length specifies the number of bytes to write.
     * @param data specifies the data buffer containing the data to write to the device.
     */
    @Override
    public void setupWriteCommand(int regAddress, int length, byte[] data)
    {
        final String funcName = "setupWriteCommand";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "addr=%x,len=%d", regAddress, length);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        sensor.copyBufferIntoWriteBuffer(data);
        sensor.enableI2cWriteMode(i2cAddress, regAddress, length);
    }   //setupWriteCommand

    /**
     * This method initiates a bus transaction with the previous command.
     */
    @Override
    public void initiatePreviousCommand()
    {
        final String funcName = "initiatePreviousCommand";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        sensor.setI2cPortActionFlag();
        sensor.writeI2cPortFlagOnlyToController();
    }   //initiatePreviousCommand

    /**
     * This method initiates a bus transaction with a new command either from
     * setupReadCommand or setupWriteCommand.
     */
    @Override
    public void initiatePortCommand()
    {
        final String funcName = "initiatePortCommand";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        sensor.setI2cPortActionFlag();
        sensor.writeI2cCacheToController();
    }   //initiatePortCommand

    /**
     * This method retrieves the data read from the device.
     *
     * @return byte array containing the data read.
     */
    @Override
    public byte[] getData()
    {
        final String funcName = "getData";

        sensor.readI2cCacheFromController();
        byte[] data = sensor.getCopyOfReadBuffer();

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", data.toString());
        }

        return data;
    }   //getData

}   //class FtcI2cDevice
