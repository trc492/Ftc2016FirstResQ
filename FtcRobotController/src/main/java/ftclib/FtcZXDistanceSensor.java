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
 * This class implements a ZX Distance sensor extending FtcI2cDevice.
 * It provides implementation of the TrcI2cDevice.DataReader interface
 * for continuously reading all the registers.
 */
public class FtcZXDistanceSensor extends FtcI2cDevice implements TrcI2cDevice.DataReader
{
    private static final String moduleName = "FtcZXDistanceSensor";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    public static final int DEF_I2CADDRESS          = 0x10;
    public static final int ALTERNATE_I2CADDRESS    = 0x11;

    //
    // GestureSense XZ01 Sensor I2C Register Map Version 1.
    //
    private static final int ZXREG_STATUS           = 0x00;     //Sensor and Gesture Status
    private static final int ZXREG_DRE              = 0x01;     //Data Ready Enable Bitmap
    private static final int ZXREG_DRCFG            = 0x02;     //Data Ready Configuration
    private static final int ZXREG_GESTURE          = 0x04;     //Last Detected Gesture
    private static final int ZXREG_GSPEED           = 0x05;     //Last Detected Gesture Speed
    private static final int ZXREG_DCM              = 0x06;     //Data Confidence Metric
    private static final int ZXREG_XPOS             = 0x08;     //X Coordinate
    private static final int ZXREG_ZPOS             = 0x0a;     //Z Coordinate
    private static final int ZXREG_LRNG             = 0x0c;     //Left Emitter Ranging Data
    private static final int ZXREG_RRNG             = 0x0e;     //Right Emitter Ranging Data
    private static final int ZXREG_REGVER           = 0xfe;     //Register Map Version
    private static final int ZXREG_MODEL            = 0xff;     //Sensor Model ID

    //
    // Register 0x00 - STATUS:
    //  DAV - Position Data Available (RO).
    //      1 indicates that new position value is available in the coordinate registers.
    //      This bit automatically resets to zero after being read.
    //  OVF - Brightness value overflow (RO).
    //      Currently unused, reads 0.
    //  SWP - Swipe Gesture Available (RO).
    //      1 indicates that a swipe gesture has been detected.
    //      Gesture value is available in the gesture register.
    //      This bit automatically resets to zero after being read.
    //  HOVER - Hover Gesture Available (RO).
    //      1 indicates that a hover gesture has been detected.
    //      Gesture value is available in the gesture register.
    //      This bit automatically resets to zero after being readn
    //  HVG - Hover-Move Gesture Available (RO).
    //      1 indicates that a hover-and-move gesture has been detected.
    //      Gesture value is available in the gesture register.
    //      This bit automatically resets to zero after being read.
    //  EDGE - Edge Detection Event (RO).
    //      Currently unused, reads 0.
    //  HB - Heartbeat (RO).
    //      This bit will toggle every time the status register has been read.
    //
    public static final int STATUS_DAV              = (1 << 0);
    public static final int STATUS_OVF              = (1 << 1);
    public static final int STATUS_SWP              = (1 << 2);
    public static final int STATUS_HOVER            = (1 << 3);
    public static final int STATUS_HVG              = (1 << 4);
    public static final int STATUS_EDGE             = (1 << 5);
    public static final int STATUS_HB               = (1 << 7);
    public static final int STATUS_GESTURES         = (STATUS_SWP | STATUS_HOVER | STATUS_HVG);

    //
    // Register 0x01 - DRE (Data Ready Enable):
    //  A '1' in any of these bits will allow the DR pin to assert when the respective event
    //  or gesture occurs. The default value of this register is 0x00, meaning that nothing
    //  will cause the DR pin to assert. The value of this register does not prevent gestures
    //  or events from being detected. It only controls which gestures or events will cause
    //  the DR pin to assert.
    //  RNG - Ranging Data Available (RW).
    //      Enable 1 = assert DR when new ranging value is available.
    //  CRD - Coordinate Data Available (RW).
    //      Enable 1 = assert DR when new coordinate value is available.
    //  SWP - Swipe Gestures (RW).
    //      Enable 1 = assert DR when swipe gestures are detected.
    //  HOVER - Hover Gestures (RW).
    //      Enable 1 = assert DR when hover gestures are detected.
    //  HVG - Hover-Move Gestures (RW).
    //      Enable 1 = assert DR when "hover-move" gestures are detected.
    //  EDGE - Edge Detection Events (RW).
    //      Enable 1 = assert DR when edge detection occurs.
    //
    private static final int DRE_RNG                = (1 << 0);
    private static final int DRE_CRD                = (1 << 1);
    private static final int DRE_SWP                = (1 << 2);
    private static final int DRE_HOVER              = (1 << 3);
    private static final int DRE_HVG                = (1 << 4);
    private static final int DRE_EDGE               = (1 << 5);
    private static final int DRE_ALL                = (DRE_RNG | DRE_CRD | DRE_SWP |
                                                       DRE_HOVER | DRE_HVG | DRE_EDGE);

    //
    // Register 0x02 - DRCFG (Data Ready Config):
    //  The default value of this register is 0x81.
    //  POLARITY - DR pin Polarity Select (RW).
    //      1 = DR pin is active-high.
    //      0 = DR pin is active-low.
    //  EDGE - DR pin Edge/Level Select (RW).
    //      1 = DR pin asserts for 1 pulse.
    //      0 = DR pin asserts until STATUS is read.
    //  FORCE - Force DR pin to assert, this bit auto-clears (RW).
    //      1 = Force DR pin to assert.
    //      0 = normal DR operation.
    //  EN - Enable DR (RW).
    //      1 = DR enabled.
    //      0 = DR always negated.
    //
    private static final int DRCFG_POLARITY         = (1 << 0);
    private static final int DRCFG_EDGE             = (1 << 1);
    private static final int DRCFG_FORCE            = (1 << 6);
    private static final int DRCFG_EN               = (1 << 7);

    //
    // Register 0x04 - Last Detected Gesture (RO).
    //  The most recent gesture appears in this register. The gesture value remains until
    //  a new gesture is detected. The gesture bits in the status register can be used to
    //  determine when to read a new value from this register.
    //  0x01 - Right Swipe.
    //  0x02 - Left Swipe
    //  0x03 - Up Swipe
    //  0x05 - Hover
    //  0x06 - Hover-Left
    //  0x07 - Hover-Right
    //  0x08 - Hover-Up
    //
    private static final int GESTURE_RIGHT_SWIPE    = 0x01;
    private static final int GESTURE_LEFT_SWIPE     = 0x02;
    private static final int GESTURE_UP_SWIPE       = 0x03;
    private static final int GESTURE_HOVER          = 0x05;
    private static final int GESTURE_HOVER_LEFT     = 0x06;
    private static final int GESTURE_HOVER_RIGHT    = 0x07;
    private static final int GESTURE_HOVER_UP       = 0x08;

    /**
     * Specifies the various detected gestures.
     */
    public enum Gesture
    {
        RIGHT_SWIPE(GESTURE_RIGHT_SWIPE),
        LEFT_SWIPE(GESTURE_LEFT_SWIPE),
        UP_SWIPE(GESTURE_UP_SWIPE),
        HOVER(GESTURE_HOVER),
        HOVER_LEFT(GESTURE_HOVER_LEFT),
        HOVER_RIGHT(GESTURE_HOVER_RIGHT),
        HOVER_UP(GESTURE_HOVER_UP);

        private final int value;

        /**
         * Constructor: Create an instance of the enum type.
         *
         * @param value specifies the enum ordinal value.
         */
        private Gesture(int value)
        {
            this.value = value;
        }   //Gesture

        /**
         * This method returns the ordinal value of the enum type.
         *
         * @return ordinal value of the enum type.
         */
        public int getValue()
        {
            return value;
        }   //getValue

        /**
         * This method returns the Gesture enum object matching the specified ordinal value.
         *
         * @param value specifies the ordinal value to match for.
         * @return Gesture enum object matching the ordinal value.
         */
        public static Gesture getGesture(int value)
        {
            for (Gesture g: Gesture.values())
            {
                if (value == g.value)
                {
                    return g;
                }
            }
            return null;
        }   //getGesture

    }   //enum Gesture

    //
    // Register 0x05 - Last Detected Gesture Speed (RO).
    //  The speed of the most recently detected gesture is stored here. The value remains until
    //  a new gesture is detected.
    //

    //
    // Register 0x06 - Data Confidence Metric (RO).
    //  Currently unused. Returns 0.
    //

    //
    // Register 0x08 - X Position (RO).
    //  The most recently calculated X position is stored in this register.
    //
    private static final int MAX_XPOSITION          = 240;

    //
    // Register 0x0a - Z Position (RO).
    //  The most recently calculated Z position is stored in this register.
    //
    private static final int MAX_ZPOSITION          = 240;

    //
    // Register 0x0c - Left Emitter Ranging Data (RO).
    //  The left emitter ranging value is stored in this register.
    //

    //
    // Register 0x0e - Right Emitter Ranging Data (RO).
    //  The right emitter ranging value is stored in this register.
    //

    //
    // Register 0xfe - Register Map Version (RO).
    //  This register is used to identify the register map version of attached sensor.
    //  All sensors share a register map. Sensors with the same register map have the
    //  same value arrangement.
    //  0x01 = Register Map v1.
    //
    public static final int REGISTERMAP_VERSION     = 0x01;

    //
    // Register 0xff - Sensor Model (RO).
    //  This register is used to identify the type of sensor attached.
    //  0x01 = XZ01.
    //
    public static final int MODEL_VERSION           = 0x01;

    private TrcSensor.SensorData[] deviceStatus = new TrcSensor.SensorData[1];
    private TrcSensor.SensorData[] gesture = new TrcSensor.SensorData[1];
    private TrcSensor.SensorData[] gestureSpeed = new TrcSensor.SensorData[1];
    private TrcSensor.SensorData[] xPos = new TrcSensor.SensorData[1];
    private TrcSensor.SensorData[] zPos = new TrcSensor.SensorData[1];
    private TrcSensor.SensorData[] leftRangingData = new TrcSensor.SensorData[1];
    private TrcSensor.SensorData[] rightRangingData = new TrcSensor.SensorData[1];
    private TrcSensor.SensorData[] regMapVersion = new TrcSensor.SensorData[1];
    private TrcSensor.SensorData[] modelVersion = new TrcSensor.SensorData[1];

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param hardwareMap specifies the global hardware map.
     * @param instanceName specifies the instance name.
     * @param i2cAddress specifies the I2C address of the device.
     */
    public FtcZXDistanceSensor(HardwareMap hardwareMap, String instanceName, int i2cAddress)
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

        addToDataReader(ZXREG_STATUS, deviceStatus.length, deviceStatus, null);
        addToDataReader(ZXREG_GESTURE, gesture.length, gesture, this);
        addToDataReader(ZXREG_GSPEED, gestureSpeed.length, gestureSpeed, this);
        addToDataReader(ZXREG_XPOS, xPos.length, xPos, this);
        addToDataReader(ZXREG_ZPOS, zPos.length, zPos, this);
        addToDataReader(ZXREG_LRNG, leftRangingData.length, leftRangingData, this);
        addToDataReader(ZXREG_RRNG, rightRangingData.length, rightRangingData, this);
        addToDataReader(ZXREG_REGVER, regMapVersion.length, regMapVersion, this);
        addToDataReader(ZXREG_MODEL, modelVersion.length, modelVersion, this);
        regMapVersion[0].value = -1.0;
        modelVersion[0].value = -1.0;
        setContinuousMode(true);
    }   //FtcZXDistanceSensor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     * @param i2cAddress specifies the I2C address of the device.
     */
    public FtcZXDistanceSensor(String instanceName, int i2cAddress)
    {
        this(FtcOpMode.getInstance().hardwareMap, instanceName, i2cAddress);
    }   //FtcZXDistanceSensor

    /**
     * Constructor: Creates an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public FtcZXDistanceSensor(String instanceName)
    {
        this(instanceName, DEF_I2CADDRESS);
    }   //FtcZXDistanceSensor

    /**
     * This method returns the data from the Status register.
     *
     * @return status register data.
     */
    public int getStatus()
    {
        final String funcName = "getStatus";
        int data = (Integer)deviceStatus[0].value;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%x", data);
        }

        return data;
    }   //getStatus

    /**
     * This method returns the detected gesture type.
     *
     * @return detected gesture type.
     */
    public Gesture getGesture()
    {
        final String funcName = "getGesture";
        Gesture data = Gesture.getGesture((Integer)gesture[0].value);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", data.toString());
        }

        return data;
    }   //getGesture

    /**
     * This method returns the data from the Gesture Speed register.
     *
     * @return gesture speed.
     */
    public TrcSensor.SensorData getGestureSpeed()
    {
        final String funcName = "getGestureSpeed";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%d)",
                               gestureSpeed[0].timestamp, gestureSpeed[0].value);
        }

        return gestureSpeed[0];
    }   //getGestureSpeed

    /**
     * This method returns the data from teh X Position register.
     *
     * @return X position.
     */
    public TrcSensor.SensorData getX()
    {
        final String funcName = "getX";

        if ((Integer)xPos[0].value > MAX_XPOSITION)
        {
            xPos[0].value = -1;
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%d)",
                               xPos[0].timestamp, xPos[0].value);
        }

        return xPos[0];
    }   //getX

    /**
     * This method returns the data from teh Z Position register.
     *
     * @return Z position.
     */
    public TrcSensor.SensorData getZ()
    {
        final String funcName = "getZ";

        if ((Integer)zPos[0].value > MAX_ZPOSITION)
        {
            zPos[0].value = -1;
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%d)",
                               zPos[0].timestamp, zPos[0].value);
        }

        return zPos[0];
    }   //getZ

    /**
     * This method returns the data from the Left Ranging Data register.
     *
     * @return left ranging data.
     */
    public TrcSensor.SensorData getLeftRangingData()
    {
        final String funcName = "getLeftRangingData";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%d)",
                               leftRangingData[0].timestamp, leftRangingData[0].value);
        }

        return leftRangingData[0];
    }   //getLeftRangingData

    /**
     * This method returns the data from the Right Ranging Data register.
     *
     * @return right ranging data.
     */
    public TrcSensor.SensorData getRightRangingData()
    {
        final String funcName = "getRightRangingData";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=(timestamp=%.3f,value=%d)",
                               rightRangingData[0].timestamp, rightRangingData[0].value);
        }

        return rightRangingData[0];
    }   //getRightRangingData

    /**
     * This method returns the data from the Register Map Version register.
     *
     * @return register map version.
     */
    public int getRegMapVersion()
    {
        final String funcName = "getRegMapVersion";
        int data = (Integer)regMapVersion[0].value;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%x", data);
        }

        return data;
    }   //getRegMapVersion

    /**
     * This method returns the data from the Model Version register.
     *
     * @return model version.
     */
    public int getModelVersion()
    {
        final String funcName = "getModelVersion";
        int data = (Integer)modelVersion[0].value;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%x", data);
        }

        return data;
    }   //getModelVersion

    //
    // Implements TrcI2cDevice.DataReader interface.
    //

    /**
     * This method is called to check if the device register should be read.
     * Typically, a status register is checked to make sure the data is
     * available.
     *
     * @param regAddress specifies the register address.
     * @return true to read the register, false otherwise.
     */
    @Override
    public boolean shouldReadData(int regAddress)
    {
        final String funcName = "shouldReadData";
        boolean shouldRead;

        switch (regAddress)
        {
            case ZXREG_STATUS:
                //
                // Always reads Status register.
                //
                shouldRead = true;
                break;

            case ZXREG_GESTURE:
            case ZXREG_GSPEED:
                //
                // Reads these only if any of the gesture bits in the Status register
                // are set.
                //
                shouldRead = (getStatus() & STATUS_GESTURES) != 0;
                break;

            case ZXREG_XPOS:
            case ZXREG_ZPOS:
            case ZXREG_LRNG:
            case ZXREG_RRNG:
                //
                // Reads these only if the STATUS_DAV bit is set.
                //
                shouldRead = (getStatus() & STATUS_DAV) != 0;
                break;

            case ZXREG_REGVER:
                //
                // RegVer register doesn't change. So we just need to read it once.
                //
                shouldRead = (Integer)regMapVersion[0].value == -1;
                break;

            case ZXREG_MODEL:
                //
                // Model register doesn't change. So we just need to read it once.
                //
                shouldRead = (Integer)modelVersion[0].value == -1;
                break;

            default:
                //
                // We don't know this register, don't read it.
                //
                shouldRead = false;
        }

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.CALLBK, "addr=%x", regAddress);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.CALLBK,
                               "=%s", Boolean.toString(shouldRead));
        }

        return shouldRead;
    }   //shouldReadData

}   //class FtcZXDistanceSensor
