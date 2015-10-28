package hallib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class HalSpeedController
{
    private String instanceName;
    private HardwareMap hardwareMap;
    private DcMotor motor;
    private int zeroPosition;

    public HalSpeedController(String instanceName)
    {
        this.instanceName = instanceName;
        hardwareMap = (HardwareMap)HalPlatform.getPlatformObject();
        motor = hardwareMap.dcMotor.get(instanceName);
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

    public String toString()
    {
        return instanceName;
    }   //toString

}   //class HalSpeedController
