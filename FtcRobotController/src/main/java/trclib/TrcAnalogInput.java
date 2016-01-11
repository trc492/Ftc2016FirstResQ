/*
 * Titan Robotics Framework Library
 * Copyright (c) 2015 Titan Robotics Club (http://www.titanrobotics.net)
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

/**
 * This class implements a platform independent AnalogInput. Typically, this
 * class is extended by a platform dependent sensor class that produces value
 * data. The sensor doesn't have to be connected to the AnalogInput port. It
 * could be connected to an I2C port as long as it produces a value data.
 * The platform dependent sensor class must implement the abstract methods
 * required by this class. The abstract methods allow this class to get raw
 * data from the sensor.
 * Depending on the options specified in the constructor, this class may create
 * an integrator. If it needs data integration, it can set the INTEGRATE or the
 * DOUBLE_INTEGRATE options.
 */
public abstract class TrcAnalogInput extends TrcSensor
{
    //
    // AnalogInput data type.
    //
    public enum DataType
    {
        INPUT_DATA,
        INTEGRATED_DATA,
        DOUBLE_INTEGRATED_DATA
    }   //enum DataType

    /**
     * This abstract method returns the raw data with the specified type.
     *
     * @param dataType specifies the data type.
     * @return raw data with the specified type.
     */
    public abstract SensorData getRawData(DataType dataType);

    //
    // AnalogInput options.
    //
    public static final int ANALOGINPUT_INTEGRATE       = (1 << 0);
    public static final int ANALOGINPUT_DOUBLE_INTEGRATE= (1 << 1);

    private static final String moduleName = "TrcAnalogInput";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private final String instanceName;
    private TrcDataIntegrator dataIntegrator = null;

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param options specifies the AnalogInput options. Multiple options can be OR'd together.
     *                ANALOGINPUT_INTEGRATE - do integration on sensor data.
     *                ANALOGINPUT_DOUBLE_INTEGRATE - do double integration on sensor data.
     * @param filter specifies a filter object, null if none.
     */
    public TrcAnalogInput(final String instanceName, int options, TrcFilter filter)
    {
        super(instanceName, 1, new TrcFilter[] {filter});

        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(moduleName + "." + instanceName,
                                       false,
                                       TrcDbgTrace.TraceLevel.API,
                                       TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;

        //
        // Create the data integrator. Data integrator needs data providers to
        // provide processed data from the sensor.
        //
        if ((options & ANALOGINPUT_INTEGRATE) != 0)
        {
            dataIntegrator = new TrcDataIntegrator(
                    instanceName, this, DataType.INPUT_DATA,
                    (options & ANALOGINPUT_DOUBLE_INTEGRATE) != 0);
        }
    }   //TrcAnalogInput

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param options specifies the AnalogInput options. Multiple options can be OR'd together.
     *                ANALOGINPUT_INTEGRATE - do integration on sensor data.
     *                ANALOGINPUT_DOUBLE_INTEGRATE - do double integration on sensor data.
     */
    public TrcAnalogInput(final String instanceName, int options)
    {
        this(instanceName, options, null);
    }   //TrcAnalogInput

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public TrcAnalogInput(final String instanceName)
    {
        this(instanceName, 0, null);
    }   //TrcAnalogInput

    /**
     * This method returns the instance name.
     *
     * @return instance name.
     */
    public String toString()
    {
        return instanceName;
    }   //toString

    /**
     * The method enables/disables the processing of sensor data. It is not
     * automatically enabled when the TrcAnalogInput object is created. You
     * need to explicitly enable the it before data processing will start. As
     * part of enabling the sensor, calibrate() is also called. calibrate()
     * may be overridden by the platform dependent sensor if it is capable
     * of doing its own. Otherwise, calibrate will call the built-in
     * calibrator to do the calibration.
     * Enabling/disabling data processing for the sensor involves
     * enabling/disabling the integrator if it exists.
     *
     * @param enabled specifies true if enabling, false otherwise.
     */
    public void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "enabled=%s", Boolean.toString(enabled));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        //
        // Enable/disable integrator.
        //
        if (dataIntegrator != null)
        {
            dataIntegrator.setEnabled(enabled);
        }
    }   //setEnabled

    /**
     * This method inverts the sensor data. This is useful if the orientation of
     * the sensor is such that the data goes the wrong direction.
     *
     * @param inverted specifies true to invert sensor data, false otherwise.
     */
    public void setInverted(boolean inverted)
    {
        final String funcName = "setInverted";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "inverted=%s", Boolean.toString(inverted));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        setInverted(0, inverted);
    }   //setInverted

    /**
     * This method sets the scale factor on the sensor data.
     *
     * @param scale specifies the scale factor.
     */
    public void setScale(double scale)
    {
        final String funcName = "setScale";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "scale=%f", scale);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        setScale(0, scale);
    }   //setScale

    /**
     * This method returns the processed sensor data.
     *
     * @return processed data.
     */
    public TrcSensor.SensorData getData()
    {
        final String funcName = "getData";
        TrcSensor.SensorData data = getData(0, DataType.INPUT_DATA);;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getData

    /**
     * This method returns the integrated sensor data.
     *
     * @return integrated sensor data.
     */
    public TrcSensor.SensorData getIntegratedData()
    {
        final String funcName = "getIntegratedData";
        TrcSensor.SensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getIntegratedData(0);
        }
        else
        {
            data = getRawData(DataType.DOUBLE_INTEGRATED_DATA);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getIntegratedData

    /**
     * This method returns the double integrated sensor data.
     *
     * @return double integrated sensor data.
     */
    public TrcSensor.SensorData getDoubleIntegratedData()
    {
        final String funcName = "getDoubleIntegratedData";
        TrcSensor.SensorData data = null;

        if (dataIntegrator != null)
        {
            data = dataIntegrator.getDoubleIntegratedData(0);
        }
        else
        {
            data = getRawData(DataType.DOUBLE_INTEGRATED_DATA);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%f)", data.timestamp, data.value);
        }

        return data;
    }   //getDoubleIntegratedData

    //
    // The following methods can be overridden by a platform dependent AnalogInput class.
    //

    /**
     * This method resets the integrator.
     */
    public void resetIntegrator()
    {
        final String funcName = "resetIntegrator";

        if (dataIntegrator != null)
        {
            dataIntegrator.reset(0);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //resetIntegrator

    //
    // Implements TrcSensor abstract methods.
    //

    /**
     * This abstract method returns the raw sensor data for the specified axis and type.
     *
     * @param index specifies the axis index.
     * @param dataType specifies the data type.
     * @return raw data for the specified axis.
     */
    @Override
    public SensorData getRawData(int index, Object dataType)
    {
        final String funcName = "getRawData";
        SensorData data = null;

        if (index == 0)
        {
            data = getRawData((DataType)dataType);
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.CALLBK, "index=%d", index);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.CALLBK,
                               "=(timestamp=%.3f,value=%f", data.timestamp, data.value);
        }

        return data;
    }   //getRawData

}   //class TrcAnalogInput
