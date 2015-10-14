package hallib;

import com.qualcomm.robotcore.hardware.DcMotor;

public class HalSpeedController
{
    private DcMotor motor;
    private int zeroPosition;

    public HalSpeedController(DcMotor motor)
    {
        this.motor = motor;
        zeroPosition = motor.getCurrentPosition();
    }   //HalSpeedController

    public void setPower(double power)
    {
        motor.setPower(power);
    }   //set

    public void setInverted(boolean isInverted)
    {
        if (isInverted)
        {
            motor.setDirection(DcMotor.Direction.REVERSE);
        }
        else
        {
            motor.setDirection(DcMotor.Direction.FORWARD);
        }
    }   //setInverted

    public int getCurrentPosition()
    {
        return motor.getCurrentPosition() - zeroPosition;
    }   //getCurrentPosition

    public void resetCurrentPosition()
    {
        zeroPosition = motor.getCurrentPosition();
    }   //resetCurrentPosition

}   //class HalSpeedController
