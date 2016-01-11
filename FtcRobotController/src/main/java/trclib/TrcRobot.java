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
 * This class is a container. It contains some robot related definitions.
 */
public class TrcRobot
{
    public static enum RunMode
    {
        /**
         * The robot is only in this mode very briefly during initializaton.
         */
        INVALID_MODE,

        /**
         * In FRC, the robot is in this mode before competition starts and between
         * mode transitions from Autonomous to TeleOp, for example.
         * (i.e. DISABLED_MODE->AUTO_MODE->DISABLED_MODE->TELEOP_MODE->DISABLED_MODE).
         * This mode does not exist in FTC.
         */
        DISABLED_MODE,

        /**
         * The robot is in this mode during the autonomous period.
         */
        AUTO_MODE,

        /**
         * The robot is in this mode during the operator control period.
         */
        TELEOP_MODE,

        /**
         * The robot is in this mode when Test Mode is selected on the DriverStation.
         */
        TEST_MODE

    }   //enum RunMode

    /**
     * Any class that implements an autonomous strategy must implement this interface.
     */
    public interface AutoStrategy
    {
        /**
         * This method is called periodically to perform autonomous operation.
         * Typically, this method runs a state machine.
         *
         * @param elapsedTime specifies the elapsed time of the autonomous period in seconds.
         */
        public void autoPeriodic(double elapsedTime);

    }   //interface AutoStrategy

}   //class TrcRobot
