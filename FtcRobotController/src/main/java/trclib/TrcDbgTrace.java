package trclib;

import hallib.HalTimer;

public class TrcDbgTrace
{
    public static final String ESC_PREFIX       = "\u001b[";
    public static final String ESC_SUFFIX       = "m";
    public static final String ESC_SEP          = ";";

    public static final String SGR_RESET        = "0";
    public static final String SGR_BRIGHT       = "1";
    public static final String SGR_DIM          = "2";
    public static final String SGR_ITALIC       = "3";
    public static final String SGR_UNDERLINE    = "4";
    public static final String SGR_BLINKSLOW    = "5";
    public static final String SGR_BLINKFAST    = "6";
    public static final String SGR_REVERSE      = "7";
    public static final String SGR_HIDDEN       = "8";
    public static final String SGR_CROSSEDOUT   = "9";

    public static final String SGR_FG_BLACK     = "30";
    public static final String SGR_FG_RED       = "31";
    public static final String SGR_FG_GREEN     = "32";
    public static final String SGR_FG_YELLOW    = "33";
    public static final String SGR_FG_BLUE      = "34";
    public static final String SGR_FG_MAGENTA   = "35";
    public static final String SGR_FG_CYAN      = "36";
    public static final String SGR_FG_WHITE     = "37";

    public static final String SGR_BG_BLACK     = "40";
    public static final String SGR_BG_RED       = "41";
    public static final String SGR_BG_GREEN     = "42";
    public static final String SGR_BG_YELLOW    = "43";
    public static final String SGR_BG_BLUE      = "44";
    public static final String SGR_BG_MAGENTA   = "45";
    public static final String SGR_BG_CYAN      = "46";
    public static final String SGR_BG_WHITE     = "47";

    public static final String ESC_NORMAL       = ESC_PREFIX
                                                    + ESC_SUFFIX;
    public static final String ESC_BLINKSLOW    = ESC_PREFIX
                                                    + SGR_BLINKSLOW
                                                    + ESC_SUFFIX;
    public static final String ESC_BLINKFAST    = ESC_PREFIX
                                                    + SGR_BLINKFAST
                                                    + ESC_SUFFIX;

    public static final String ESC_FG_BLACK     = ESC_PREFIX
                                                    + SGR_FG_BLACK
                                                    + ESC_SUFFIX;
    public static final String ESC_FG_RED       = ESC_PREFIX
                                                    + SGR_FG_RED
                                                    + ESC_SUFFIX;
    public static final String ESC_FG_GREEN     = ESC_PREFIX
                                                    + SGR_FG_GREEN
                                                    + ESC_SUFFIX;
    public static final String ESC_FG_YELLOW    = ESC_PREFIX
                                                    + SGR_FG_YELLOW
                                                    + ESC_SUFFIX;
    public static final String ESC_FG_BLUE      = ESC_PREFIX
                                                    + SGR_FG_BLUE
                                                    + ESC_SUFFIX;
    public static final String ESC_FG_MAGENTA   = ESC_PREFIX
                                                    + SGR_FG_MAGENTA
                                                    + ESC_SUFFIX;
    public static final String ESC_FG_CYAN      = ESC_PREFIX
                                                    + SGR_FG_CYAN
                                                    + ESC_SUFFIX;
    public static final String ESC_FG_WHITE     = ESC_PREFIX
                                                    + SGR_FG_WHITE
                                                    + ESC_SUFFIX;

    public static final String ESC_BG_BLACK     = ESC_PREFIX
                                                    + SGR_BG_BLACK
                                                    + ESC_SUFFIX;
    public static final String ESC_BG_RED       = ESC_PREFIX
                                                    + SGR_BG_RED
                                                    + ESC_SUFFIX;
    public static final String ESC_BG_GREEN     = ESC_PREFIX
                                                    + SGR_BG_GREEN
                                                    + ESC_SUFFIX;
    public static final String ESC_BG_YELLOW    = ESC_PREFIX
                                                    + SGR_BG_YELLOW
                                                    + ESC_SUFFIX;
    public static final String ESC_BG_BLUE      = ESC_PREFIX
                                                    + SGR_BG_BLUE
                                                    + ESC_SUFFIX;
    public static final String ESC_BG_MAGENTA   = ESC_PREFIX
                                                    + SGR_BG_MAGENTA
                                                    + ESC_SUFFIX;
    public static final String ESC_BG_CYAN      = ESC_PREFIX
                                                    + SGR_BG_CYAN
                                                    + ESC_SUFFIX;
    public static final String ESC_BG_WHITE     = ESC_PREFIX
                                                    + SGR_BG_WHITE
                                                    + ESC_SUFFIX;

    public static final String ESC_FGB_BLACK    = ESC_PREFIX
                                                    + SGR_FG_BLACK
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_FGB_RED      = ESC_PREFIX
                                                    + SGR_FG_RED
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_FGB_GREEN    = ESC_PREFIX
                                                    + SGR_FG_GREEN
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_FGB_YELLOW   = ESC_PREFIX
                                                    + SGR_FG_YELLOW
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_FGB_BLUE     = ESC_PREFIX
                                                    + SGR_FG_BLUE
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_FGB_MAGENTA  = ESC_PREFIX
                                                    + SGR_FG_MAGENTA
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_FGB_CYAN     = ESC_PREFIX
                                                    + SGR_FG_CYAN
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_FGB_WHITE    = ESC_PREFIX
                                                    + SGR_FG_WHITE
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;

    public static final String ESC_BGB_BLACK    = ESC_PREFIX
                                                    + SGR_BG_BLACK
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_BGB_RED      = ESC_PREFIX
                                                    + SGR_BG_RED
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_BGB_GREEN    = ESC_PREFIX
                                                    + SGR_BG_GREEN
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_BGB_YELLOW   = ESC_PREFIX
                                                    + SGR_BG_YELLOW
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_BGB_BLUE     = ESC_PREFIX
                                                    + SGR_BG_BLUE
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_BGB_MAGENTA  = ESC_PREFIX
                                                    + SGR_BG_MAGENTA
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_BGB_CYAN     = ESC_PREFIX
                                                    + SGR_BG_CYAN
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;
    public static final String ESC_BGB_WHITE    = ESC_PREFIX
                                                    + SGR_BG_WHITE
                                                    + ESC_SEP
                                                    + SGR_BRIGHT
                                                    + ESC_SUFFIX;

    public static enum TraceLevel
    {
        QUIET(0),
        INIT(1),
        API(2),
        CALLBK(3),
        EVENT(4),
        FUNC(5),
        TASK(6),
        UTIL(7),
        HIFREQ(8);

        private int value;

        TraceLevel(int value)
        {
            this.value = value;
        }   //TraceLevel

        public int getValue()
        {
            return this.value;
        }   //getValue

    }   //enum TraceLevel

    public static enum MsgLevel
    {
        FATAL(1),
        ERR(2),
        WARN(3),
        INFO(4),
        VERBOSE(5);

        private int value;

        MsgLevel(int value)
        {
            this.value = value;
        }   //MsgLevel

        public int getValue()
        {
            return this.value;
        }   //getValue

    }   //enum MsgLevel

    private static int indentLevel = 0;

    private String instanceName;
    private boolean traceEnabled;
    private TraceLevel traceLevel;
    private MsgLevel msgLevel;
    private double nextTraceTime;

    public void setDbgTraceConfig(
            boolean traceEnabled,
            TraceLevel traceLevel,
            MsgLevel msgLevel)
    {
        this.traceEnabled = traceEnabled;
        this.traceLevel = traceLevel;
        this.msgLevel = msgLevel;
    }   //setDbgTraceConfig

    public TrcDbgTrace(
            final String instanceName,
            boolean traceEnabled,
            TraceLevel traceLevel,
            MsgLevel msgLevel)
    {
        this.instanceName = instanceName;
        setDbgTraceConfig(traceEnabled, traceLevel, msgLevel);
        this.nextTraceTime = HalTimer.getCurrentTime();
    }   //TrcDbgTrace

    public void traceEnter(
            final String funcName,
            final TraceLevel funcLevel,
            final String format,
            Object... args)
    {
        if (traceEnabled &&
            funcLevel.getValue() <= traceLevel.getValue())
        {
            printTracePrefix(funcName, true, false);
            System.out.printf(format, args);
            System.out.print(")\n" + ESC_NORMAL);
        }
    }   //traceEnter

    public void traceEnter(
            final String funcName,
            final TraceLevel funcLevel)
    {
        if (traceEnabled &&
            funcLevel.getValue() <= traceLevel.getValue())
        {
            printTracePrefix(funcName, true, true);
        }
    }   //traceEnter

    public void traceExit(
            final String funcName,
            final TraceLevel funcLevel,
            final String format,
            Object... args)
    {
        if (traceEnabled &&
            funcLevel.getValue() <= traceLevel.getValue())
        {
            printTracePrefix(funcName, false, false);
            System.out.printf(format, args);
            System.out.print("\n" + ESC_NORMAL);
        }
    }   //traceExitMsg

    public void traceExit(
            final String funcName,
            final TraceLevel funcLevel)
    {
        if (traceEnabled &&
            funcLevel.getValue() <= traceLevel.getValue())
        {
            printTracePrefix(funcName, false, true);
        }
    }   //traceExit

    public void traceFatal(
            final String funcName,
            final String format,
            Object... args)
    {
        traceMsg(funcName, MsgLevel.FATAL, 0.0, format, args);
    }   //traceFatal

    public void traceErr(
            final String funcName,
            final String format,
            Object... args)
    {
        traceMsg(funcName, MsgLevel.ERR, 0.0, format, args);
    }   //traceErr

    public void traceWarn(
            final String funcName,
            final String format,
            Object... args)
    {
        traceMsg(funcName, MsgLevel.WARN, 0.0, format, args);
    }   //traceWarn

    public void traceInfo(
            final String funcName,
            final String format,
            Object... args)
    {
        traceMsg(funcName, MsgLevel.INFO, 0.0, format, args);
    }   //traceInfo

    public void traceVerbose(
            final String funcName,
            final String format,
            Object... args)
    {
        traceMsg(funcName, MsgLevel.VERBOSE, 0.0, format, args);
    }   //traceVerbose

    public void tracePeriodic(
            final String funcName,
            double traceInterval,
            final String format,
            Object... args)
    {
        traceMsg(funcName, MsgLevel.INFO, traceInterval, format, args);
    }   //tracePeriodic

    private void traceMsg(
            final String funcName,
            MsgLevel level,
            double traceInterval,
            final String format,
            Object... args)
    {
        if (level.getValue() <= msgLevel.getValue())
        {
            double currTime = HalTimer.getCurrentTime();
            if (currTime >= nextTraceTime)
            {
                nextTraceTime = currTime + traceInterval;
                printMsgPrefix(funcName, level);
                System.out.printf(format, args);
                System.out.print("\n" + ESC_NORMAL);
            }
        }
    }   //traceMsg

    private void printTracePrefix(
            final String funcName,
            boolean enter,
            boolean newline)
    {
        if (enter)
        {
            indentLevel++;
        }

        System.out.print(ESC_FGB_MAGENTA);
        for (int i = 0; i < indentLevel; i++)
        {
            System.out.print("| ");
        }

        System.out.print(instanceName + "." + funcName);

        if (enter)
        {
            System.out.print(newline? "()\n": "(");
        }
        else
        {
            System.out.print(newline? "!\n" + ESC_NORMAL: "");
            indentLevel--;
        }
    }   //printTracePrefix

    private void printMsgPrefix(
            final String funcName,
            MsgLevel level)
    {
        String prefix;
        String color;

        switch (level)
        {
        case FATAL:
            prefix = "_Fatal: ";
            color = ESC_PREFIX + SGR_FG_YELLOW +
                    ESC_SEP + SGR_BRIGHT +
                    ESC_SEP + SGR_BG_RED +
                    ESC_SUFFIX;
            break;

        case ERR:
            prefix = "_Err: ";
            color = ESC_FGB_RED;
            break;

        case WARN:
            prefix = "_Warn: ";
            color = ESC_FGB_YELLOW;
            break;

        case INFO:
            prefix = "_Info: ";
            color = ESC_FGB_GREEN;
            break;

        case VERBOSE:
            prefix = "_Verbose: ";
            color = ESC_FGB_WHITE;
            break;

        default:
            prefix = "_Unk: ";
            color = ESC_NORMAL;
            break;
        }

        System.out.print(color + instanceName + "." + funcName + prefix);
    }   //printMsgPrefix

}   //class TrcDbgTrace
