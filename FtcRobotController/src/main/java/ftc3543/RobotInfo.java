package ftc3543;

public class RobotInfo
{
    //
    // DriveBase subsystem.
    //
    public static final double DRIVE_KP                         = 0.04;
    public static final double DRIVE_KI                         = 0.0;
    public static final double DRIVE_KD                         = 0.0;
    public static final double DRIVE_KF                         = 0.0;
    public static final double DRIVE_TOLERANCE                  = 2.0;
    public static final double DRIVE_SETTLING                   = 0.2;
    public static final double DRIVE_INCHES_PER_CLICK           = (67.0/4941.0);

    public static final double TURN_KP                          = 0.10;
    public static final double TURN_KI                          = 0.0;
    public static final double TURN_KD                          = 0.0;
    public static final double TURN_KF                          = 0.0;
    public static final double TURN_TOLERANCE                   = 2.0;
    public static final double TURN_SETTLING                    = 0.2;

    public static final double BEACON_DISTANCE                  = 2.0;
    public static final double LINE_THRESHOLD                   = ((10.0 + 60.0)/2.0);
    public static final double LINEFOLLOW_KP                    = 0.02;
    public static final double LINEFOLLOW_KI                    = 0.0;
    public static final double LINEFOLLOW_KD                    = 0.0;
    public static final double LINEFOLLOW_KF                    = 0.0;
    public static final double LINEFOLLOW_TOLERANCE             = 2.0;
    public static final double LINEFOLLOW_SETTLING              = 0.2;

    public static final double SONAR_KP                         = 0.25;
    public static final double SONAR_KI                         = 0.0;
    public static final double SONAR_KD                         = 0.0;
    public static final double SONAR_KF                         = 0.0;
    public static final double SONAR_TOLERANCE                  = 0.2;
    public static final double SONAR_SETTLING                   = 0.2;
    public static final double SONAR_INCHES_PER_CM              = (1.0/2.54);
    public static final double SONAR_SCALE_TO_INCHES            = (512.0/(5.0/1024));
    //
    // Elevator subsystem.
    //
    public static final double ELEVATOR_KP                      = 0.5;
    public static final double ELEVATOR_KI                      = 0.0;
    public static final double ELEVATOR_KD                      = 0.0;
    public static final double ELEVATOR_KF                      = 0.0;
    public static final double ELEVATOR_TOLERANCE               = 0.2;
    public static final double ELEVATOR_SETTLING                = 0.2;
    public static final double ELEVATOR_MIN_HEIGHT              = 0.0;
    public static final double ELEVATOR_MAX_HEIGHT              = 23.5;
    public static final double ELEVATOR_INCHES_PER_CLICK        = (23.5/9700.0);
    public static final double ELEVATOR_CAL_POWER               = -0.2;
    public static final double BRAKE_OFF_POSITION               = 0.1;
    public static final double BRAKE_ON_POSITION                = 0.25;

    //
    // Slider subsystem.
    //
    public static final double SLIDER_KP                        = 0.15;
    public static final double SLIDER_KI                        = 0.0;
    public static final double SLIDER_KD                        = 0.0;
    public static final double SLIDER_KF                        = 0.0;
    public static final double SLIDER_TOLERANCE                 = 0.2;
    public static final double SLIDER_SETTLING                  = 0.2;
    public static final double SLIDER_MIN_LENGTH                = 0.0;
    public static final double SLIDER_MAX_LENGTH                = 10.0;
    public static final double SLIDER_INCHES_PER_CLICK          = (10.0/20402.0);
    public static final double SLIDER_CAL_POWER                 = -1.0;

    //
    // ButtonPusher subsystem.
    //
    public static final double PUSHER_EXTEND_LEFT               = 1.0;
    public static final double PUSHER_RETRACT_LEFT              = 0.0;
    public static final double PUSHER_EXTEND_RIGHT              = 1.0;
    public static final double PUSHER_RETRACT_RIGHT             = 0.0;

    //
    // ClimberRelease subsystem.
    //
    public static final double WING_LEFT_RETRACT_POSITION       = 0.1;
    public static final double WING_LEFT_EXTEND_POSITION        = 0.75;
    public static final double WING_RIGHT_RETRACT_POSITION      = 0.0;
    public static final double WING_RIGHT_EXTEND_POSITION       = 0.65;

    //
    // HangingHook subsystem.
    //
    public static final double HANGINGHOOK_RETRACT_POSITION     = 0.11;
    public static final double HANGINGHOOK_EXTEND_POSITION      = 0.8;

}   //class RobotInfo
