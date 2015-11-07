package trclib;

public class TrcUtil
{
    public static int limit(int value, int lowLimit, int highLimit)
    {
        return (value < lowLimit)?
                    lowLimit:
               (value > highLimit)?
                    highLimit:
                    value;
    }   //limit

    public static double limit(double value, double lowLimit, double highLimit)
    {
        return (value < lowLimit)?
                    lowLimit:
               (value > highLimit)?
                    highLimit:
                    value;
    }   //limit

    public static double limit(double value)
    {
        return limit(value, -1.0, 1.0);
    }   //limit

    public static int scaleRange(
            int value,
            int lowSrcRange, int highSrcRange,
            int lowDstRange, int highDstRange)
    {
        return lowDstRange +
               (value - lowSrcRange)*(highDstRange - lowDstRange)/
               (highSrcRange - lowSrcRange);
    }   //scaleRange

    public static double scaleRange(
            double value,
            double lowSrcRange, double highSrcRange,
            double lowDstRange, double highDstRange)
    {
        return lowDstRange +
                (value - lowSrcRange)*(highDstRange - lowDstRange)/
                        (highSrcRange - lowSrcRange);
    }   //scaleRange

    public static double applyDeadband(double value, double deadband)
    {
        return Math.abs(value) >= deadband? value: 0.0;
    }   //applyDeadband

}   //class TrcUtil
