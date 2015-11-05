package hallib;

public interface HalGyro
{
    public void calibrate();
    public boolean isCalibrating();
    public void reset();
    public double getRawX();
    public double getRawY();
    public double getRawZ();
    public double getRotation();
    public double getHeading();
}   //interface HalGyro
