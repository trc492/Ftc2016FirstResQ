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

package trclib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import hallib.HalUtil;

/**
 * This class implements a platform independent I2C device. Typically,
 * this class is extended by a platform dependent I2C device class.
 * The platform dependent I2C device class must implement the abstract
 * methods required by this class. The abstract methods allow this class
 * to perform platform independent operations on the I2C device.
 */
public abstract class TrcI2cDevice implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcI2cDevice";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    /**
     * This method checks if the I2C port is ready for bus transaction.
     *
     * @return true if port is ready, false otherwise.
     */
    public abstract boolean isPortReady();

    /**
     * This method checks if the I2C port is in write mode.
     *
     * @return true if port is in write mode, false otherwise.
     */
    public abstract boolean isPortInWriteMode();

    /**
     * This method sets up the port for a read operation.
     *
     * @param regAddress specifies the register address.
     * @param length specifies the number of bytes to read.
     */
    public abstract void setupReadCommand(int regAddress, int length);

    /**
     * This method sets up the port for a write operation.
     *
     * @param regAddress specifies the register address.
     * @param length specifies the number of bytes to write.
     * @param data specifies the data buffer containing the data to write to the device.
     */
    public abstract void setupWriteCommand(int regAddress, int length, byte[] data);

    /**
     * This method initiates a bus transaction with the previous command.
     */
    public abstract void initiatePreviousCommand();

    /**
     * This method initiates a bus transaction with a new command either from
     * setupReadCommand or setupWriteCommand.
     */
    public abstract void initiatePortCommand();

    /**
     * This method retrieves the data read from the device.
     *
     * @return byte array containing the data read.
     */
    public abstract byte[] getData();

    /**
     * This class also implements a Data Reader Engine that allows the caller
     * to set up a register list to be read either in a continuous loop or
     * just read once. Some of the device registers may not have valid data
     * until some sort of status register indicates so. Since this class
     * doesn't understand or interpret the content of any registers, the
     * caller must implement this interface so it can call to figure out
     * if the register can be read.
     */
    public interface DataReader
    {
        /**
         * This method is called to check if the device register should be read.
         * Typically, a status register is checked to make sure the data is available.
         *
         * @param regAddress specifies the register address.
         * @return true to read the register, false otherwise.
         */
        public boolean shouldReadData(int regAddress);

    }   //interface DataReader

    /**
     * Specifies the Port Command state machine states.
     */
    private enum PortCommandState
    {
        //
        // Check the queue for next request.
        //
        START,
        //
        //Set up the port command.
        //
        SETUP_PORT_COMMAND,
        //
        // Initiate the port command operation.
        //
        INITIATE_PORT_COMMAND,
        //
        // Port command is completed, set completion event if necessary.
        //
        COMPLETE,
        //
        // No more request in the queue, stop the state machine.
        //
        DONE
    }   //enum PortCommandState

    /**
     * Specifies the Data Reader state machine states.
     */
    private enum DataReaderState
    {
        //
        // Initiates a read command on the register.
        //
        READ_REGISTER,
        //
        // Read operation is complete, transfer the data read.
        //
        READ_REGISTER_COMPLETE,
        //
        // Move to the next register in the array list. If reach
        // the end of the list, either loop back to the beginning
        // of the array if in continuous mode, or done.
        //
        NEXT_REGISTER,
        //
        // In one-shot mode and have reached the end of the array.
        // We are done.
        //
        DONE
    }   //enum DataReaderState

    /**
     * This class implements an I2C device request. It can be a read or write
     * request. This is implicitly indicated by the writeBuffer field. The
     * present of a writeBuffer indicates it is a write request. It is a
     * read request otherwise.
     */
    private class Request
    {
        private int regAddress;
        private int numBytes;
        private byte[] writeBuffer;
        private TrcEvent completionEvent;

        /**
         * Constructor: Create an instance of the object.
         *
         * @param regAddress specifies the register address.
         * @param numBytes specifies the number of bytes to read or write.
         * @param writeBuffer specifies the write buffer, null if read operation.
         * @param completionEvent specifies the completion event to signal when done,
         *                        can be null if none specified.
         */
        public Request(int regAddress, int numBytes, byte[] writeBuffer, TrcEvent completionEvent)
        {
            this.regAddress = regAddress;
            this.numBytes = numBytes;
            this.writeBuffer = writeBuffer;
            this.completionEvent = completionEvent;
        }   //Request

    }   //class Request

    private TrcStateMachine portCommandSM;
    private Queue<Request> requestQueue = new LinkedList<Request>();
    private Request currRequest = null;

    private TrcStateMachine dataReaderSM;
    private TrcEvent dataEvent;
    private ArrayList<Integer> regAddrList = new ArrayList<Integer>();
    private ArrayList<Integer> dataLenList = new ArrayList<Integer>();
    private ArrayList<TrcSensor.SensorData[]> dataBuffList =
            new ArrayList<TrcSensor.SensorData[]>();
    private ArrayList<DataReader> dataReaderList = new ArrayList<DataReader>();
    private boolean continuousMode = false;
    private int dataIndex = 0;
    private TrcEvent oneShotEvent = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public TrcI2cDevice(String instanceName)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        portCommandSM = new TrcStateMachine(instanceName + ".portCmd");

        dataReaderSM = new TrcStateMachine(instanceName + ".dataReader");
        dataEvent = new TrcEvent(instanceName + ".dataEvent");
        TrcTaskMgr.getInstance().registerTask(
                instanceName, this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
    }   //FtcI2cDevice

    /**
     * This method queues a read request to the I2C device.
     *
     * @param regAddress specifies the register address to read from.
     * @param numBytes specifies the number of bytes to read.
     * @param completionEvent specifies the completion event to signal when done.
     *                        It can be null if notification is not required.
     */
    public void sendReadCommand(int regAddress, int numBytes, TrcEvent completionEvent)
    {
        final String funcName = "sendReadCommand";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "addr=%x,len=%d,event=%s",
                                regAddress, numBytes,
                                completionEvent != null? completionEvent.toString(): "null");
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        requestQueue.add(new Request(regAddress, numBytes, null, completionEvent));
        //
        // If the PortCommand state machine is not already active, start it.
        //
        if (!portCommandSM.isEnabled())
        {
            portCommandSM.start(PortCommandState.START);
        }
    }   //sendReadCommand

    /**
     * This method queues a read request to the I2C device.
     *
     * @param regAddress specifies the register address to read from.
     * @param numBytes specifies the number of bytes to read.
     */
    public void sendReadCommand(int regAddress, int numBytes)
    {
        sendReadCommand(regAddress, numBytes, null);
    }   //sendReadCommand

    /**
     * This method queues a write request to the I2C device.
     *
     * @param regAddress specifies the register address to write to.
     * @param numBytes specifies the number of bytes to read.
     * @param writeBuffer specifies the buffer containing the data to be written to the device.
     * @param completionEvent specifies the completion event to signal when done.
     *                        It can be null if notification is not required.
     */
    public void sendWriteCommand(
            int regAddress, int numBytes, byte[] writeBuffer, TrcEvent completionEvent)
    {
        final String funcName = "sendWriteCommand";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "addr=%x,len=%d,event=%s",
                                regAddress, numBytes,
                                completionEvent != null? completionEvent.toString(): "null");
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        requestQueue.add(new Request(regAddress, numBytes, writeBuffer, completionEvent));
        //
        // If the PortCommand state machine is not already active, start it.
        //
        if (!portCommandSM.isEnabled())
        {
            portCommandSM.start(PortCommandState.START);
        }
    }   //sendWriteCommand

    /**
     * This method queues a write request to the I2C device.
     *
     * @param regAddress specifies the register address to write to.
     * @param numBytes specifies the number of bytes to read.
     * @param writeBuffer specifies the buffer containing the data to be written to the device.
     */
    public void sendWriteCommand(int regAddress, int numBytes, byte[] writeBuffer)
    {
        sendWriteCommand(regAddress, numBytes, writeBuffer, null);
    }   //sendWriteCommand

    /**
     * This method is called periodically to run the Port Command state machine.
     */
    private void runPortCommand()
    {
        final String funcName = "runPortCommand";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.TASK);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }

        if (portCommandSM.isReady())
        {
            PortCommandState state = (PortCommandState)portCommandSM.getState();
            switch (state)
            {
                case START:
                    //
                    // Dequeue a request from the beginning of the queue.
                    //
                    currRequest = requestQueue.poll();
                    if (currRequest == null)
                    {
                        //
                        // There is no request in the queue, we are done.
                        //
                        portCommandSM.setState(PortCommandState.DONE);
                        break;
                    }
                    else
                    {
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(funcName, "%s: Request(addr=%x,len=%d,%s,event=%s)",
                                               state.toString(), currRequest.regAddress,
                                               currRequest.numBytes,
                                               currRequest.writeBuffer == null? "read": "write",
                                               currRequest.completionEvent.toString());
                        }
                        portCommandSM.setState(PortCommandState.SETUP_PORT_COMMAND);
                    }
                    //
                    // Intentionally falling through to next case.
                    //
                case SETUP_PORT_COMMAND:
                    //
                    // Wait for the port to become ready before setting up the command.
                    //
                    if (isPortReady())
                    {
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(funcName, "%s", state.toString());
                        }

                        if (currRequest.writeBuffer == null)
                        {
                            //
                            // It's a read request, setup a read command.
                            //
                            setupReadCommand(currRequest.regAddress, currRequest.numBytes);
                        }
                        else
                        {
                            //
                            // It's a write request, setup a write command.
                            //
                            setupWriteCommand(currRequest.regAddress,
                                              currRequest.numBytes,
                                              currRequest.writeBuffer);
                        }
                        portCommandSM.setState(PortCommandState.INITIATE_PORT_COMMAND);
                    }
                    break;

                case INITIATE_PORT_COMMAND:
                    //
                    // Wait until the port is ready and is in the correct mode before
                    // initiating the port operation.
                    //
                    if (isPortReady() &&
                        (currRequest.writeBuffer == null && !isPortInWriteMode() ||
                         currRequest.writeBuffer != null && isPortInWriteMode()))
                    {
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(funcName, "%s", state.toString());
                        }

                        initiatePortCommand();
                        portCommandSM.setState(PortCommandState.COMPLETE);
                    }
                    break;

                case COMPLETE:
                    //
                    // Wait for the port command to complete.
                    //
                    if (isPortReady())
                    {
                        if (debugEnabled)
                        {
                            dbgTrace.traceInfo(funcName, "%s", state.toString());
                        }

                        //
                        // The port command is complete, set completion event if any.
                        //
                        if (currRequest.completionEvent != null)
                        {
                            currRequest.completionEvent.set(true);
                            currRequest.completionEvent = null;
                        }

                        currRequest.writeBuffer = null;
                        portCommandSM.setState(PortCommandState.START);
                    }
                    break;

                case DONE:
                default:
                    //
                    // There is no more request in the queue, stop the state machine.
                    //
                    if (debugEnabled)
                    {
                        dbgTrace.traceInfo(funcName, "%s", state.toString());
                    }

                    portCommandSM.stop();
                    break;
            }
        }
    }   //runPortCommand

    /**
     * This method adds a device register to the data reader list for the read operation.
     *
     * @param regAddr specifies the register address.
     * @param dataLen specifies the number of bytes to read from the register.
     * @param dataBuff specifies the data buffer to hold the data read.
     * @param dataReader specifies the object implementing the DataReader interface.
     *                   It can be set to null if none is provided.
     */
    public void addToDataReader(
            int regAddr, int dataLen, TrcSensor.SensorData[] dataBuff, DataReader dataReader)
    {
        final String funcName = "addToDataReader";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "addr=%x,len=%d", regAddr, dataLen);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        regAddrList.add(regAddr);
        dataLenList.add(dataLen);
        dataBuffList.add(dataBuff);
        dataReaderList.add(dataReader);
    }   //addToDataReader

    /**
     * This method sets up a one-shot read operation of all the registers in the array.
     *
     * @param oneShotEvent specifies the event object to signal when done. It can be
     *                     null if none is provided.
     */
    public void oneShotRead(TrcEvent oneShotEvent)
    {
        final String funcName = "oneShotRead";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "event=%s", oneShotEvent != null? oneShotEvent.toString(): "null");
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
        //
        // Initiate a one-shot read. It doesn't matter if the reader is in
        // continuous mode. If it is, the one shot event will still fire at
        // the end of the register list. The only difference is that if it
        // is in continuous mode, it will loop back to do it again whereas
        // if it is not in continuous mode, it will be done and stopped.
        //
        this.oneShotEvent = oneShotEvent;
        //
        // If the DataReader state machine is not active, start it.
        //
        if (!dataReaderSM.isEnabled())
        {
            dataIndex = 0;
            dataReaderSM.start(DataReaderState.READ_REGISTER);
        }
    }   //oneShotRead

    /**
     * This method sets up a one-shot read operation of all the registers in the array.
     */
    public void oneShotRead()
    {
        oneShotRead(null);
    }   //oneShotRead

    /**
     * This method enables/disbles continuous mode.
     *
     * @param continuousMode specifies true to enable continuous mode, false to disable it.
     */
    public void setContinuousMode(boolean continuousMode)
    {
        final String funcName = "setContinuousMode";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "continuous=%s", Boolean.toString(continuousMode));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }

        this.continuousMode = continuousMode;
        if (continuousMode && !dataReaderSM.isEnabled())
        {
            //
            // If the state machine is already enabled, just setting
            // the continuousMode variable to true is enough to keep
            // it going. But if the state machine is not already enabled,
            // we need to initialize and start it. If we are turning
            // continuous mode off, we just need to set the variable
            // to false. After the state machine is done, it will turn
            // itself off.
            //
            dataIndex = 0;
            dataReaderSM.start(DataReaderState.READ_REGISTER);
        }
    }   //setContinuousMode

    /**
     * This method is called periodcially to run the DataReader state machine.
     */
    private void runDataReader()
    {
        final String funcName = "runDataReader";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.TASK);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.TASK);
        }

        if (dataReaderSM.isReady())
        {
            DataReaderState state = (DataReaderState)dataReaderSM.getState();

            if (debugEnabled)
            {
                dbgTrace.traceInfo(funcName, "%s: index=%d", state.toString(), dataIndex);
            }

            switch (state)
            {
                case READ_REGISTER:
                    //
                    // Ask the caller if we should read this register.
                    //
                    int regAddr = regAddrList.get(dataIndex);
                    DataReader dataReader = dataReaderList.get(dataIndex);
                    if (dataReader == null || dataReaderList.get(dataIndex).shouldReadData(regAddr))
                    {
                        //
                        // We only read the register if the DataReader say we should
                        // read it. Only the DataReader knows if the value is unavailable
                        // or if there is no reason to read it. In that case, we must
                        // skip it. If there is no DataReader, assume always read.
                        //
                        sendReadCommand(regAddr, dataLenList.get(dataIndex), dataEvent);
                        dataReaderSM.addEvent(dataEvent);
                        dataReaderSM.waitForEvents(DataReaderState.READ_REGISTER_COMPLETE);
                    }
                    else
                    {
                        dataReaderSM.setState(DataReaderState.NEXT_REGISTER);
                    }
                    break;

                case READ_REGISTER_COMPLETE:
                    //
                    // Read operation completed normally, transfer the data to
                    // the provided buffer with timestamp.
                    //
                    byte[] data = getData();
                    double timestamp = HalUtil.getCurrentTime();
                    TrcSensor.SensorData[] dataBuff = dataBuffList.get(dataIndex);
                    for (int i = 0; i < data.length; i++)
                    {
                        dataBuff[i].timestamp = timestamp;
                        dataBuff[i].value = (int)(data[i] & 0xff);
                    }
                    dataReaderSM.setState(DataReaderState.NEXT_REGISTER);
                    break;

                case NEXT_REGISTER:
                    //
                    // Increment the index and check if we reached the end.
                    // If we are at the end and in one-shot mode, we are done.
                    // If we are at the end and in continuous mode, wrap back
                    // to the beginning of the array and start again. If we
                    // are not at the end, go back to the READ_REGISTER state
                    // to read the next register.
                    //
                    dataIndex++;
                    if (dataIndex >= regAddrList.size())
                    {
                        if (!continuousMode)
                        {
                            if (oneShotEvent != null)
                            {
                                oneShotEvent.set(true);
                                oneShotEvent = null;
                            }
                            dataReaderSM.setState(DataReaderState.DONE);
                        }
                        else
                        {
                            dataIndex = 0;
                            dataReaderSM.setState(DataReaderState.READ_REGISTER);
                        }
                    }
                    else
                    {
                        dataReaderSM.setState(DataReaderState.READ_REGISTER);
                    }
                    break;

                case DONE:
                default:
                    //
                    // We are in one-shot mode and reached the end of the array.
                    // We are done.
                    //
                    dataReaderSM.stop();
                    break;
            }
        }
    }   //runDataReader

    //
    // Implements TrcTaskMgr.Task
    //

    @Override
    public void startTask(TrcRobot.RunMode runMode)
    {
    }   //startTask

    @Override
    public void stopTask(TrcRobot.RunMode runMode)
    {
    }   //stopTask

    @Override
    public void prePeriodicTask(TrcRobot.RunMode runMode)
    {
    }   //prePeriodicTask

    @Override
    public void postPeriodicTask(TrcRobot.RunMode runMode)
    {
    }   //postPeriodicTask

    /**
     * This method is called periodically to run the DataReader and PortCommand
     * state machines.
     *
     * @param runMode specifies the competition mode that is running.
     */
    @Override
    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
        runDataReader();
        runPortCommand();
    }   //preContinuousTask

    @Override
    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcI2cDevice
