package trclib;

/**
 * This class contains platform independent utility methods.
 * All methods in this class are static. It is not necessary
 * to instantiate this class to call its methods.
 */
public class TrcUtil
{
    /**
     * This method clips the given value to the range limited
     * by the given low and high limits.
     *
     * @param value specifies the value to be clipped
     * @param lowLimit specifies the low limit of the range.
     * @param highLimit specifies the high limit of the range.
     * @return the result of the clipped value.
     */
    public static int limit(int value, int lowLimit, int highLimit)
    {
        return (value < lowLimit)?
                    lowLimit:
               (value > highLimit)?
                    highLimit:
                    value;
    }   //limit

    /**
     * This method clips the given value to the range limited
     * by the given low and high limits.
     *
     * @param value specifies the value to be clipped
     * @param lowLimit specifies the low limit of the range.
     * @param highLimit specifies the high limit of the range.
     * @return the result of the clipped value.
     */
    public static double limit(double value, double lowLimit, double highLimit)
    {
        return (value < lowLimit)?
                    lowLimit:
               (value > highLimit)?
                    highLimit:
                    value;
    }   //limit

    /**
     * This method clips the given value to the range between -1.0 and 1.0.
     *
     * @param value specifies the value to be clipped
     * @return the result of the clipped value.
     */
    public static double limit(double value)
    {
        return limit(value, -1.0, 1.0);
    }   //limit

    /**
     * This method scales the given value from the source range to the target range.
     *
     * @param value specifies the value to be scaled.
     * @param lowSrcRange specifies the low limit of the source range.
     * @param highSrcRange specifies the high limit of the source range.
     * @param lowDstRange specifies the low limit of the target range.
     * @param highDstRange specifies the high limit of the target range
     * @return the result of the scaled value.
     */
    public static int scaleRange(
            int value,
            int lowSrcRange, int highSrcRange,
            int lowDstRange, int highDstRange)
    {
        return lowDstRange +
               (value - lowSrcRange)*(highDstRange - lowDstRange)/
               (highSrcRange - lowSrcRange);
    }   //scaleRange

    /**
     * This method scales the given value from the source range to the target range.
     *
     * @param value specifies the value to be scaled.
     * @param lowSrcRange specifies the low limit of the source range.
     * @param highSrcRange specifies the high limit of the source range.
     * @param lowDstRange specifies the low limit of the target range.
     * @param highDstRange specifies the high limit of the target range
     * @return the result of the scaled value.
     */
    public static double scaleRange(
            double value,
            double lowSrcRange, double highSrcRange,
            double lowDstRange, double highDstRange)
    {
        return lowDstRange +
                (value - lowSrcRange)*(highDstRange - lowDstRange)/
                        (highSrcRange - lowSrcRange);
    }   //scaleRange

    /**
     * This method checks if the given value is within the deadband range.
     * If so, it returns 0.0 else it returns the unchanged value.
     *
     * @param value specifies the value to be chacked.
     * @param deadband specifies the deadband zone.
     * @return the value 0.0 if within deadband, unaltered otherwise.
     */
    public static double applyDeadband(double value, double deadband)
    {
        return Math.abs(value) >= deadband? value: 0.0;
    }   //applyDeadband

}   //class TrcUtil
