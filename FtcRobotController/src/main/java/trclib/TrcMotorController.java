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
 * This class should be extended by a platform dependent motor controller class
 * which will provide all the required abstract methods in this class. The
 * abstract methods allow platform independent access to all the features of
 * the motor controller.
 */
public abstract class TrcMotorController
{
    private static final String moduleName = "TrcMotorController";
    private static final boolean debugEnabled = false;
    private TrcDbgTrace dbgTrace = null;

    /**
     * This method sets the motor power. If limit switches are present,
     * it will make sure the motor won't move into the direction where
     * the limit switch is activated.
     *
     * @param power specifies the motor power in the range of -1.0 to 1.0.
     */
    public abstract void setPower(double power);

    /**
     * This method enables/disables motor brake mode. In motor brake mode, setPower(0) would
     * stop the motor very abruptly by shorting the motor wires together using the generated
     * back EMF to s5op the motor. When brakMode is false (i.e. float mode), the motor wires
     * are just disconnected from the motor controller so the motor will stop gradually.
     *
     * @param brakeMode specifies true to enable brake mode, false otherwise.
     */
    public abstract void setBrakeMode(boolean brakeMode);

    /**
     * This method inverts the motor direction.
     *
     * @param inverted specifies true to invert motor direction, false otherwise.
     */
    public abstract void setInverted(boolean inverted);

    /**
     * This method resets the motor position sensor, typically an encoder.
     */
    public abstract void resetPosition();

    /**
     * This method inverts the position sensor direction. This may be rare but
     * there are scenarios where the motor encoder may be mounted somewhere in
     * the power train that it rotates opposite to the motor rotation. This will
     * cause the encoder reading to go down when the motor is receiving positive
     * power. This method can correct this situation.
     *
     * @param inverted specifies true to invert position sensor direction,
     *                 false otherwise.
     */
    public abstract void setPositionSensorInverted(boolean inverted);

    /**
     * This method returns the motor position by reading the position sensor.
     *
     * @return current motor position.
     */
    public abstract double getPosition();

    /**
     * This method returns the speed of the motor rotation.
     *
     * @return motor rotation speed.
     */
    public abstract double getSpeed();

    /**
     * This method returns the state of the reverse limit switch.
     *
     * @return true if reverse limit switch is activated, false otherwise.
     */
    public abstract boolean isReverseLimitSwitchActive();

    /**
     * This method returns the state of the forward limit switch.
     *
     * @return true if forward limit switch is activated, false otherwise.
     */
    public abstract boolean isForwardLimitSwitchActive();

    private String instanceName;

    /**
     * Constructor: Create an instance of the object.
     *
     * @param instanceName specifies the instance name.
     */
    public TrcMotorController(String instanceName)
    {
        if (debugEnabled)
        {
            dbgTrace = new TrcDbgTrace(
                    moduleName + "." + instanceName,
                    false,
                    TrcDbgTrace.TraceLevel.API,
                    TrcDbgTrace.MsgLevel.INFO);
        }

        this.instanceName = instanceName;
    }   //TrcMotorController

    /**
     * This method returns the instance name.
     *
     * @return instance name.
     */
    public String toString()
    {
        return instanceName;
    }   //toString

}   //class TrcMotorController
