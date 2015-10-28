package trclib;

import java.util.ArrayList;

import hallib.HalPlatform;

public class TrcStateMachine
{
    private static final String moduleName = "TrcStateMachine";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    public static final int STATE_DISABLED = -1;
    public static final int STATE_STARTED = 0;

    private ArrayList<TrcEvent> eventList;
    private int currState;
    private int nextState;
    private boolean ready;
    private boolean expired;
    private double expiredTime;
    private boolean waitForAllEvents;

    public TrcStateMachine(final String instanceName)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        eventList = new ArrayList<TrcEvent>();
        currState = STATE_DISABLED;
        nextState = STATE_DISABLED;
        ready = false;
        expired = false;
        expiredTime = 0.0;
        waitForAllEvents = false;
    }   //TrcStateMachine

    public void start()
    {
        start(STATE_STARTED);
    }   //start

    public void start(int state)
    {
        final String funcName = "start";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "state=%d", state);
        }

        eventList.clear();
        currState = state;
        nextState = state;
        ready = true;
        expired = false;
        expiredTime = 0.0;
        waitForAllEvents = false;

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //start

    public void stop()
    {
        final String funcName = "stop";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        eventList.clear();
        currState = STATE_DISABLED;
        nextState = STATE_DISABLED;
        ready = false;
        expired = false;
        expiredTime = 0.0;
        waitForAllEvents = false;

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //stop


    public int getState()
    {
        final String funcName = "getState";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%d", currState);
        }

        return currState;
    }   //getState

    public void setState(int state)
    {
        final String funcName = "setState";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "state=%d", state);
        }

        currState = state;

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //setState

    public boolean isEnabled()
    {
        final String funcName = "isEnabled";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString(currState != STATE_DISABLED));
        }

        return currState != STATE_DISABLED;
    }   //isEnabled

    public boolean isReady()
    {
        final String funcName = "isReady";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.TASK);
        }

        if (currState != STATE_DISABLED && !ready)
        {
            if (expiredTime > 0.0 && HalPlatform.getCurrentTime() >= expiredTime)
            {
                expiredTime = 0.0;
                ready = true;
                expired = true;
            }
            else
            {
                int count = 0;
                for (int i = 0; i < eventList.size(); i++)
                {
                    TrcEvent event = eventList.get(i);
                    if (event.isSignaled() || event.isCanceled())
                    {
                        count++;
                    }
                }
                if (!waitForAllEvents && count > 0 ||
                    waitForAllEvents && count == eventList.size())
                {
                    ready = true;
                }
            }

            if (ready)
            {
                clearAllEvents();
                eventList.clear();
                currState = nextState;
            }
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.TASK,
                    "=%s",
                    Boolean.toString(currState != STATE_DISABLED && ready));
        }

        return currState != STATE_DISABLED && ready;
    }   //isReady

    public boolean isTimedout()
    {
        final String funcName = "isTimedout";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString(expired));
        }

        return expired;
    }   //isTimedout

    public void addEvent(TrcEvent event)
    {
        final String funcName = "addEvent";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "event=%s", event.getName());
        }

        if (!eventList.contains(event))
        {
            eventList.add(event);
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //addEvent

    public void waitForEvents(int nextState)
    {
        waitForEvents(nextState, false, 0.0);
    }   //waitForEvents

    public void waitForEvents(
            int nextState,
            boolean waitForAllEvents,
            double timeout)
    {
        final String funcName = "waitForEvents";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "nextState=%d,waitForAll=%s,timeout=%f",
                    nextState, Boolean.toString(waitForAllEvents), timeout);
        }

        this.nextState = nextState;
        this.expiredTime = timeout;
        if (timeout > 0.0)
        {
            this.expiredTime += HalPlatform.getCurrentTime();
        }
        this.waitForAllEvents = waitForAllEvents;
        ready = false;

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }
    }   //waitForEvents

    private void clearAllEvents()
    {
        final String funcName =  "clearAllEvents";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.UTIL);
        }

        for (int i = 0; i < eventList.size(); i++)
        {
            TrcEvent event = eventList.get(i);
            event.clear();;
        }

        if (debugEnabled)
        {
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.UTIL);
        }
    }   //clearAllEvents

}   //class TrcStateMachine
