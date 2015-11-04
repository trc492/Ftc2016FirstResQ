package hallib;

public interface HalSpeedController
{
    public void setPower(double power);
    public void setInverted(boolean isInverted);
    public int getCurrentPosition();
    public void resetCurrentPosition();

}   //interface HalSpeedController
