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

import java.util.IllegalFormatException;

public class TrcAnalogTrigger implements TrcTaskMgr.Task
{
    private static final String moduleName = "TrcAnalogTrigger";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    public enum Zone
    {
        UNKNOWN_ZONE,
        LOW_ZONE,
        MID_ZONE,
        HIGH_ZONE
    }   //enum Zone

    public interface TriggerHandler
    {
        public void AnalogTriggerEvent(
                TrcAnalogTrigger analogTrigger,
                Zone zone,
                double value);
    }   //interface TriggerHandler

    private String instanceName;
    private TrcSensorDataSource sensor;
    private int dataIndex;
    private double lowThreshold;
    private double highThreshold;
    private TriggerHandler triggerHandler;
    private Zone prevZone;

    public TrcAnalogTrigger(
            final String instanceName,
            TrcSensorDataSource sensor,
            int dataIndex,
            double lowThreshold,
            double highThreshold,
            TriggerHandler triggerHandler)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        if (sensor == null || triggerHandler == null)
        {
            throw new NullPointerException("Sensor/TriggerHandler must be provided");
        }

        this.instanceName = instanceName;
        this.sensor = sensor;
        this.dataIndex = dataIndex;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.triggerHandler = triggerHandler;
        prevZone = Zone.UNKNOWN_ZONE;
    }   //TrcAnalogTrigger

    public TrcAnalogTrigger(
            final String instanceName,
            TrcSensorDataSource sensor,
            int dataIndex,
            double threshold,
            TriggerHandler triggerHandler)
    {
        this(instanceName, sensor, dataIndex, threshold, threshold, triggerHandler);
    }   //TrcAnalogTrigger

    public void setThresholds(double lowThreshold, double highThreshold)
    {
        final String funcName = "setThreshold";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                    "lowThreshold=%f,highThreshold=%f",
                    lowThreshold, highThreshold);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }   //setThresholds

    public void setEnabled(boolean enabled)
    {
        final String funcName = "setEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.FUNC,
                    "enabled=%s", Boolean.toString(enabled));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC);
        }

        if (enabled)
        {
            TrcTaskMgr.getInstance().registerTask(
                    instanceName, this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
        }
        else
        {
            TrcTaskMgr.getInstance().unregisterTask(this, TrcTaskMgr.TaskType.PRECONTINUOUS_TASK);
        }
    }   //setEnabled

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

    @Override
    public void preContinuousTask(TrcRobot.RunMode runMode)
    {
        final String funcName = "preContinuousTask";
        TrcSensor.SensorData data = sensor.getSensorData(dataIndex);
        double value;

        if (data.value instanceof Integer)
        {
            value = (double)(Integer)data.value;
        }
        else if (data.value instanceof Double)
        {
            value = (Double)data.value;
        }
        else
        {
            throw new NumberFormatException("Sensor data must be either integer or double.");
        }

        Zone zone;
        if (value <= lowThreshold)
        {
            zone = Zone.LOW_ZONE;
        }
        else if (value <= highThreshold)
        {
            zone = Zone.MID_ZONE;
        }
        else
        {
            zone = Zone.HIGH_ZONE;
        }

        if (zone != prevZone)
        {
            //
            // We have crossed to another zone, let's notify somebody.
            //
            prevZone = zone;
            if (triggerHandler != null)
            {
                triggerHandler.AnalogTriggerEvent(this, zone, value);
            }

            if (debugEnabled)
            {
                dbgTrace.traceInfo(
                        funcName, "%s entering %s (data=%f)",
                        instanceName, zone.toString(), value);
            }
        }
    }   //preContinuousTask

    @Override
    public void postContinuousTask(TrcRobot.RunMode runMode)
    {
    }   //postContinuousTask

}   //class TrcAnalogTrigger
