package ftc3543;

public class RobotInfo
{
    //
    // DriveBase subsystem.
    //
    public static final double DRIVE_KP                         = 0.04; //TODO: needs verifying
    public static final double DRIVE_KI                         = 0.0;
    public static final double DRIVE_KD                         = 0.0;
    public static final double DRIVE_KF                         = 0.0;
    public static final double DRIVE_TOLERANCE                  = 2.0;
    public static final double DRIVE_SETTLING                   = 0.2;
    public static final double DRIVE_INCHES_PER_CLICK           = (67.0/4941.0);

    public static final double GYRO_KP                          = 0.10;
    public static final double GYRO_KI                          = 0.0;
    public static final double GYRO_KD                          = 0.0;
    public static final double GYRO_KF                          = 0.0;
    public static final double GYRO_TOLERANCE                   = 2.0;
    public static final double GYRO_SETTLING                    = 0.2;

    //
    // Line following PID control.
    //
    public static final double SONAR_KP                         = 0.1;  //TODO: needs tuning
    public static final double SONAR_KI                         = 0.0;
    public static final double SONAR_KD                         = 0.0;
    public static final double SONAR_KF                         = 0.0;
    public static final double SONAR_TOLERANCE                  = 0.2;
    public static final double SONAR_SETTLING                   = 0.2;
    public static final double SONAR_INCHES_PER_CM              = (1.0/2.54);
    public static final double SONAR_BEACON_DISTANCE            = 2.0;

    public static final double COLOR_KP                         = 0.01;
    public static final double COLOR_KI                         = 0.0;
    public static final double COLOR_KD                         = 0.0;
    public static final double COLOR_KF                         = 0.0;
    public static final double COLOR_TOLERANCE                  = 2.0;
    public static final double COLOR_SETTLING                   = 0.2;
    public static final double COLOR_RED_THRESHOLD              = 8.0;
    public static final double COLOR_WHITE_THRESHOLD            = 12.0;
    public static final double COLOR_DARK_LEVEL                 = 20.0;
    public static final double COLOR_WHITE_LEVEL                = 30.0;
    public static final double COLOR_LINE_EDGE_LEVEL            = ((COLOR_DARK_LEVEL + COLOR_WHITE_LEVEL)/2.0);
    public static final double COLOR_LINE_EDGE_DEADBAND         = (COLOR_LINE_EDGE_LEVEL*0.25);

    //
    // Winch subsystem.
    //
    public static final double WINCH_KP                         = 0.5;  //TODO: needs tuning
    public static final double WINCH_KI                         = 0.0;
    public static final double WINCH_KD                         = 0.0;
    public static final double WINCH_KF                         = 0.0;
    public static final double WINCH_TOLERANCE                  = 0.2;
    public static final double WINCH_SETTLING                   = 0.2;
    public static final double WINCH_INCHES_PER_CLICK           = 1.0;
    public static final double WINCH_MIN_LENGTH                 = 0.0;
    public static final double WINCH_MAX_LENGTH                 = 23.5;
    public static final double WINCH_CAL_POWER                  = -0.2;
    public static final double WINCH_BRAKE_ON_POSITION          = 1.0;
    public static final double WINCH_BRAKE_OFF_POSITION         = 0.0;
    public static final double WINCH_TILTER_MIN_POSITION        = 0.1;
    public static final double WINCH_TILTER_MAX_POSITION        = 0.85;
    public static final double WINCH_TILTER_MAX_STEPRATE        = 0.2;

    //
    // Climber Depositor subsystem.
    //
    public static final double DEPOSITOR_RETRACT_POSITION       = 0.22; //TODO: needs tuning
    public static final double DEPOSITOR_EXTEND_POSITION        = 0.85;

    //
    // ClimberRelease subsystem.
    //
    public static final double WING_LEFT_RETRACT_POSITION       = 0.1;
    public static final double WING_LEFT_EXTEND_POSITION        = 0.75;
    public static final double WING_RIGHT_RETRACT_POSITION      = 0.0;
    public static final double WING_RIGHT_EXTEND_POSITION       = 0.65;

    //
    // ButtonPusher subsystem.
    //
    public static final double PUSHER_EXTEND_TIME               = 1.00;
    public static final double PUSHER_RETRACT_TIME              = 1.00;

}   //class RobotInfo
