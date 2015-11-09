package ftc3543.opmodes;

public class RobotInfo
{
    //
    // DriveBase subsystem.
    //
    public static final double DRIVE_KP                         = 1.0;
    public static final double DRIVE_KI                         = 0.0;
    public static final double DRIVE_KD                         = 0.0;
    public static final double DRIVE_KF                         = 0.0;
    public static final double DRIVE_TOLERANCE                  = 2.0;
    public static final double DRIVE_SETTLING                   = 0.2;
    public static final double DRIVE_INCHES_PER_CLICK           = 1.0;

    public static final double TURN_KP                          = 1.0;
    public static final double TURN_KI                          = 0.0;
    public static final double TURN_KD                          = 0.0;
    public static final double TURN_KF                          = 0.0;
    public static final double TURN_TOLERANCE                   = 2.0;
    public static final double TURN_SETTLING                    = 0.2;

    public static final double LINE_THRESHOLD                   = 80.0;
    public static final double LINEFOLLOW_KP                    = 1.0;
    public static final double LINEFOLLOW_KI                    = 0.0;
    public static final double LINEFOLLOW_KD                    = 0.0;
    public static final double LINEFOLLOW_KF                    = 0.0;
    public static final double LINEFOLLOW_TOLERANCE             = 2.0;
    public static final double LINEFOLLOW_SETTLING              = 0.2;

    //
    // Elevator subsystem.
    //
    public static final double ELEVATOR_KP                      = 1.0;
    public static final double ELEVATOR_KI                      = 0.0;
    public static final double ELEVATOR_KD                      = 0.0;
    public static final double ELEVATOR_KF                      = 0.0;
    public static final double ELEVATOR_TOLERANCE               = 2.0;
    public static final double ELEVATOR_SETTLING                = 0.2;
    public static final double ELEVATOR_MIN_HEIGHT              = 0.0;
    public static final double ELEVATOR_MAX_HEIGHT              = 48.0;
    public static final double ELEVATOR_INCHES_PER_CLICK        = 1.0;
    public static final double ELEVATOR_CAL_POWER               = -0.2;
    public static final double CHAINLOCK_UNLOCK_POSITION        = 0.0;
    public static final double CHAINLOCK_LOCK_POSITION          = 1.0;

    //
    // ButtonPusher subsystem.
    //
    public static final double PUSHER_EXTEND_LEFT               = 0.0;
    public static final double PUSHER_RETRACT_LEFT              = 1.0;
    public static final double PUSHER_EXTEND_RIGHT              = 1.0;
    public static final double PUSHER_RETRACT_RIGHT             = 0.0;

    //
    // CattleGuard subsystem.
    //
    public static final double CATTLEGUARD_RETRACT_POSITION     = 0.0;
    public static final double CATTLEGUARD_EXTEND_POSITION      = 1.0;

    //
    // ClimberRelease subsystem.
    //
    public static final double ARM_RETRACT_POSITION             = 0.0;
    public static final double ARM_EXTEND_POSITION              = 1.0;

    //
    // HangingHook subsystem.
    //
    public static final double HANGINGHOOK_RETRACT_POSITION     = 0.0;
    public static final double HANGINGHOOK_EXTEND_POSITION      = 1.0;

}   //class RobotInfo
