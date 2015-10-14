package hallib;

import com.qualcomm.robotcore.hardware.GyroSensor;

public class HalGyro
{
    GyroSensor gyro;
    double zeroAngle;

    public HalGyro(GyroSensor gyro)
    {
        this.gyro = gyro;
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

}   //class HalGyro
