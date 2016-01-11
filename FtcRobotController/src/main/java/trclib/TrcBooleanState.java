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

public class TrcBooleanState
{
    private static final String moduleName = "TrcBooleanState";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    private boolean state;

    public TrcBooleanState(final String instanceName)
    {
        this(instanceName, false);
    }   //TrcBooleanState

    public TrcBooleanState(final String instanceName, boolean state)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.state = state;
    }   //TrcBooleanState

    public boolean getState()
    {
        final String funcName = "getState";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString(state));
        }

        return state;
    }   //getState

    public void setState(boolean state)
    {
        final String funcName = "setState";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "state=%s", Boolean.toString(state));
            dbgTrace.traceExit(funcName, TrcDbgTrace.TraceLevel.API);
        }

        this.state = state;
    }   //setState

    public boolean toggleState()
    {
        final String funcName = "toggleState";

        if (debugEnabled)
        {
            dbgTrace.traceEnter(funcName, TrcDbgTrace.TraceLevel.API);
        }

        state = !state;

        if (debugEnabled)
        {
            dbgTrace.traceExit(
                    funcName, TrcDbgTrace.TraceLevel.API,
                    "=%s", Boolean.toString(state));
        }
        return state;
    }   //toggleState

}   //class TrcToggle
