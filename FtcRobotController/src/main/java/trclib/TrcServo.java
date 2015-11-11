package trclib;

public interface TrcServo
{
    public void setReverse(boolean reverse);

    public boolean getReverse();

    public void setPosition(double position);

    public double getPosition();

    public void setScale(double physicalMin, double physicalMax);

    public void setScale(double physicalMin, double physicalMax, double logicalMin, double logicalMax);

}   //interface TrcServo
