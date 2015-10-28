package hallib;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import trclib.TrcUtil;

public class HalServo
{
    private static final double DEF_PHYSICAL_MIN = 0.0;
    private static final double DEF_PHYSICAL_MAX = 1.0;
    private static final double DEF_LOGICAL_MIN = 0.0;
    private static final double DEF_LOGICAL_MAX = 1.0;

    private String instanceName;
    private HardwareMap hardwareMap;
    private Servo servo;
    private double physicalMin = DEF_PHYSICAL_MIN;
    private double physicalMax = DEF_PHYSICAL_MAX;
    private double logicalMin = DEF_LOGICAL_MIN;
    private double logicalMax = DEF_LOGICAL_MAX;

    public HalServo(String instanceName)
    {
        this.instanceName = instanceName;
        hardwareMap = ((FtcRobot)HalPlatform.getPlatformObject()).hardwareMap;
        servo = hardwareMap.servo.get(instanceName);
    }   //HalServo

    public void setReverse(boolean reverse)
    {
        servo.setDirection(reverse ? Servo.Direction.REVERSE : Servo.Direction.FORWARD);
    }   //setReverse

    public boolean getReverse()
    {
        return servo.getDirection() == Servo.Direction.REVERSE;
    }   //getReverse

    public void setPosition(double position)
    {
        position = TrcUtil.limit(position, physicalMin, physicalMax);
        position = TrcUtil.scaleRange(position, physicalMin, physicalMax, logicalMin, logicalMax);
        servo.setPosition(position);
    }   //setPosition

    public double getPosition()
    {
        return TrcUtil.scaleRange(
                servo.getPosition(), logicalMin, logicalMax, physicalMin, physicalMax);
    }   //getPosition

    public void setScale(double physicalMin, double physicalMax)
    {
        if (physicalMin >= physicalMax)
        {
            throw new IllegalArgumentException("max must be greater than min.");
        }

        this.physicalMin = physicalMin;
        this.physicalMax = physicalMax;
    }   //setScale

    public void setScale(double physicalMin, double physicalMax, double logicalMin, double logicalMax)
    {
        if (physicalMin >= physicalMax || logicalMin >= logicalMax)
        {
            throw new IllegalArgumentException("max must be greater than min.");
        }

        this.physicalMin = physicalMin;
        this.physicalMax = physicalMax;
        this.logicalMin = logicalMin;
        this.logicalMax = logicalMax;
    }   //setScale

    public String toString()
    {
        return instanceName;
    }   //toString

}   //class HalServo
