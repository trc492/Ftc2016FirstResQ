package hallib;

import com.qualcomm.robotcore.robocol.Telemetry;

import java.util.NoSuchElementException;

import ftclib.FtcOpMode;
import trclib.TrcDbgTrace;

/**
 * This class is a wrapper for the Telemetry class. In addition to providing
 * a way to send named data to the Driver Station to be displayed, it also
 * simulates an LCD display similar to the NXT Mindstorms. The Mindstorms
 * has only 8 lines but this dashboard can support as many lines as the
 * Driver Station can support. By default, we set the number of lines to 16.
 * By changing a constant here, you can have as many lines as you want. This
 * dashboard display is very useful for displaying debug information. In
 * particular, the TrcMenu class uses the dashboard to display a choice menu
 * and interact with the user for choosing autonomous strategies and options.
 * This class is a wrapper of the Telemetry class to send information to the
 * Driver Station to be displayed.
 */
public class HalDashboard
{
    private static final String moduleName = "HalDashboard";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    public static final int MAX_NUM_TEXTLINES = 16;

    private static final String displayKeyFormat = "[%02d]";
    private static Telemetry telemetry = null;
    private static HalDashboard instance = null;
    private static String[] display = new String[MAX_NUM_TEXTLINES];

    /**
     * Constructor: Creates an instance of the object.
     * There should only be one global instance of this object.
     * Only the FtcOpMode object should construct an instance of
     * this object and nobody else. However, we are not enforcing
     * this because there is no way for this constructor to know
     * whether the constructor call was initiated by loading a
     * new opMode or from the robot code somewhere.
     *
     * @param telemetry specifies the Telemetry object.
     */
    public HalDashboard(Telemetry telemetry)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        instance = this;
        this.telemetry = telemetry;
        telemetry.clearData();
        clearDisplay();
    }   //HalDashboard

    /**
     * Constructor: Creates an instance of the object.
     * There should only be one global instance of this object.
     * Only the FtcOpMode object should construct an instance of
     * this object and nobody else. However, we are not enforcing
     * this because there is no way for this constructor to know
     * whether the constructor call was initiated by loading a
     * new opMode or from the robot code somewhere.
     */
    private HalDashboard()
    {
        this(FtcOpMode.getInstance().telemetry);
    }    //HalDashboard

    /**
     * This static method allows any class to get an instance of
     * the dashboard so that it can display information on its
     * display.
     *
     * @return global instance of the dashboard object.
     */
    public static HalDashboard getInstance()
    {
        if (instance == null)
        {
            instance = new HalDashboard();
        }

        return instance;
    }   //getInstance

    /**
     * This method displays a formatted message to the display on the Driver Station.
     *
     * @param lineNum specifies the line number on the display.
     * @param format specifies the format string.
     * @param args specifies variable number of substitution arguments.
     */
    public void displayPrintf(int lineNum, String format, Object... args)
    {
        if (lineNum >= 0 && lineNum < display.length)
        {
            display[lineNum] = String.format(format, args);
            telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
        }
    }   //displayPrintf

    /**
     * This method clears all the display lines.
     */
    public void clearDisplay()
    {
        final String funcName = "clearDisplay";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        for (int i = 0; i < display.length; i++)
        {
            display[i] = "";
        }
        refreshDisplay();
    }   //clearDisplay

    /**
     * This method refresh the display lines to the Driver Station.
     */
    public void refreshDisplay()
    {
        final String funcName = "refreshDisplay";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        for (int i = 0; i < display.length; i++)
        {
            telemetry.addData(String.format(displayKeyFormat, i), display[i]);
        }
    }   //refreshDisplay

    /**
     * This method returns the value of the named boolean data read from the
     * Telemetry class.
     *
     * @param key specifies the name associated with the boolean data.
     * @return boolean data value.
     */
    public boolean getBoolean(String key)
    {
        final String funcName = "getBoolean";
        boolean value;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "key=%s", key);
        }

        String strValue = getValue(key);
        if (strValue.equals("true"))
        {
            value = true;
        }
        else if (strValue.equals("false"))
        {
            value = false;
        }
        else
        {
            throw new IllegalArgumentException("object is not boolean");
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(value));
        }

        return value;
    }   //getBoolean

    /**
     * This method returns the value of the named boolean data read from the
     * Telemetry class. If the named data does not exist, it is created and
     * assigned the given default value. Then it is sent to the Driver Station.
     *
     * @param key specifies the name associated with the boolean data.
     * @param defaultValue specifies the default value if it does not exist.
     * @return boolean data value.
     */
    public boolean getBoolean(String key, boolean defaultValue)
    {
        final String funcName = "getBoolean";
        boolean value;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "key=%s,defValue=%s", key, Boolean.toString(defaultValue));
        }

        try
        {
            value = getBoolean(key);
        }
        catch (NoSuchElementException e)
        {
            putBoolean(key, defaultValue);
            value = defaultValue;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%s", Boolean.toString(value));
        }

        return value;
    }   //getBoolean

    /**
     * This method sets the named boolean data with the given value and also
     * sends it to the Driver Station.
     *
     * @param key specifies the name associated with the boolean data.
     * @param value specifies the data value.
     */
    public void putBoolean(String key, boolean value)
    {
        final String funcName = "putBoolean";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "key=%s,value=%s", key, Boolean.toString(value));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        telemetry.addData(key, Boolean.toString(value));
    }   //putBoolean

    /**
     * This method returns the value of the named double data read from the
     * Telemetry class.
     *
     * @param key specifies the name associated with the double data.
     * @return double data value.
     */
    public double getNumber(String key)
    {
        final String funcName = "getNumber";
        double value;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "key=%s", key);
        }

        try
        {
            value = Double.parseDouble(getValue(key));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("object is not a number");
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%f", value);
        }

        return value;
    }   //getNumber

    /**
     * This method returns the value of the named double data read from the
     * Telemetry class. If the named data does not exist, it is created and
     * assigned the given default value. Then it is sent to the Driver Station.
     *
     * @param key specifies the name associated with the double data.
     * @param defaultValue specifies the default value if it does not exist.
     * @return double data value.
     */
    public double getNumber(String key, double defaultValue)
    {
        final String funcName = "getNumber";
        double value;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "key=%s,defValue=%f", key, defaultValue);
        }

        try
        {
            value = getNumber(key);
        }
        catch (NoSuchElementException e)
        {
            putNumber(key, defaultValue);
            value = defaultValue;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API,
                               "=%f", value);
        }

        return value;
    }   //getNumber

    /**
     * This method sets the named double data with the given value and also
     * sends it to the Driver Station.
     *
     * @param key specifies the name associated with the double data.
     * @param value specifies the data value.
     */
    public void putNumber(String key, double value)
    {
        final String funcName = "putNumber";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "key=%s,value=%f", key, value);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        telemetry.addData(key, Double.toString(value));
    }   //putNumber

    /**
     * This method returns the value of the named string data read from the
     * Telemetry class.
     *
     * @param key specifies the name associated with the string data.
     * @return string data value.
     */
    public String getString(String key)
    {
        final String funcName = "getString";
        String value = getValue(key);

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API, "key=%s", key);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", value);
        }

        return value;
    }   //getString

    /**
     * This method returns the value of the named string data read from the
     * Telemetry class. If the named data does not exist, it is created and
     * assigned the given default value. Then it is sent to the Driver Station.
     *
     * @param key specifies the name associated with the string data.
     * @param defaultValue specifies the default value if it does not exist.
     * @return string data value.
     */
    public String getString(String key, String defaultValue)
    {
        final String funcName = "getString";
        String value;

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "key=%s,defValue=%s", key, defaultValue);
        }

        try
        {
            value = getString(key);
        }
        catch (NoSuchElementException e)
        {
            putString(key, defaultValue);
            value = defaultValue;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API, "=%s", value);
        }

        return value;
    }   //getString

    /**
     * This method sets the named string data with the given value and also
     * sends it to the Driver Station.
     *
     * @param key specifies the name associated with the string data.
     * @param value specifies the data value.
     */
    public void putString(String key, String value)
    {
        final String funcName = "putString";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API,
                                "key=%s,value=%s", key, value);
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        telemetry.addData(key, value);
    }   //putString

    /**
     * This method calls Telemetry class to retrieve the named data item.
     *
     * @param key specifies the name associated with the string data.
     * @return string data associated with the given name.
     */
    private String getValue(String key)
    {
        final String funcName = "getValue";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.FUNC, "key=%s", key);
        }

        String value = telemetry.getDataStrings().get(key);
        if (value == null)
        {
            throw new NoSuchElementException("No such key");
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.FUNC, "=%s", value);
        }

        return value;
    }   //getValue

}   //class HalDashboard
