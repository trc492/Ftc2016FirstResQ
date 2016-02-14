package hallib;

public interface HalMotorController
{
    /**
     * This method returns the motor position by reading the position sensor. The position
     * sensor can be an encoder or a potentiometer.
     *
     * @return current motor position.
     */
    public double getPosition();

    /**
     * This method returns the speed of the motor rotation.
     *
     * @return motor rotation speed.
     */
    public double getSpeed();

    /**
     * This method returns the state of the forward limit switch.
     *
     * @return true if forward limit switch is closed, false otherwise.
     */
    public boolean isFwdLimitSwitchClosed();

    /**
     * This method returns the state of the reverse limit switch.
     *
     * @return true if reverse limit switch is closed, false otherwise.
     */
    public boolean isRevLimitSwitchClosed();

    /**
     * This method resets the motor position sensor, typically an encoder.
     */
    public void resetPosition();

    /**
     * This method enables/disables motor brake mode. In motor brake mode, set power to 0 would
     * stop the motor very abruptly by shorting the motor wires together using the generated
     * back EMF to stop the motor. When brakMode is false (i.e. float/coast mode), the motor wires
     * are just disconnected from the motor controller so the motor will stop gradually.
     *
     * @param brakeMode specifies true to enable brake mode, false otherwise.
     */
    public void setBrakeModeEnabled(boolean enabled);

    /**
     * This method inverts the motor direction.
     *
     * @param inverted specifies true to invert motor direction, false otherwise.
     */
    public void setInverted(boolean inverted);

    /**
     * This method sets the output of the motor controller. Typically, the output is power.
     * However, some motor controllers are capable of other operating modes such as position,
     * speed, voltage, current, etc. When operating in those modes, output specifies the
     * appropriate value for that operating mode.
     *
     * @param output specifies the output for the motor controller. If the output is power, it
     *               is in the range of -1.0 to 1.0.
     */
    public void setOutput(double output);

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
    public void setPositionSensorInverted(boolean inverted);

}   //interface HalMotorController
