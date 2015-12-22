package trclib;

public class TrcRobot
{
    public static enum RunMode
    {
        INVALID_MODE,
        DISABLED_MODE,
        AUTO_MODE,
        TELEOP_MODE,
        TEST_MODE
    }   //enum RunMode

    public interface AutoStrategy
    {
        public void autoPeriodic(double elapsedTime);
    }   //interface AutoStrategy

}   //class TrcRobot
