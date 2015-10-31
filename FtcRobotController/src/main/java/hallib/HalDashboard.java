package hallib;

import com.qualcomm.robotcore.robocol.Telemetry;

import java.util.NoSuchElementException;

import trclib.TrcDbgTrace;

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

    public HalDashboard()
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
        this.telemetry = FtcRobot.getInstance().telemetry;
        telemetry.clearData();
        clearDisplay();
    }   //HalDashboard

    public static HalDashboard getInstance()
    {
        return instance;
    }   //getInstance

    public void displayPrintf(int lineNum, String format, Object... args)
    {
        if (lineNum >= 0 && lineNum < display.length)
        {
            display[lineNum] = String.format(format, args);
            telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
        }
    }   //displayPrintf

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
