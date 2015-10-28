package hallib;

import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class HalGyro
{
    private String instanceName;
    private HardwareMap hardwareMap;
    private GyroSensor gyro;
    private double zeroAngle;

    public HalGyro(String instanceName)
    {
        this.instanceName = instanceName;
        hardwareMap = ((FtcRobot)HalPlatform.getPlatformObject()).hardwareMap;
        this.gyro = hardwareMap.gyroSensor.get(instanceName);
        zeroAngle = gyro.getRotation();
    }   //HalGyro

    public void reset()
    {
        zeroAngle = gyro.getRotation();
    }   //reset

    public double getAngle()
    {
        return gyro.getRotation() - zeroAngle;
    }   //getAngle

    public String toString()
    {
        return instanceName;
    }   //toString

}   //class HalGyro
