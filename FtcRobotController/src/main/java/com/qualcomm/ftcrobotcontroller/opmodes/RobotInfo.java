package com.qualcomm.ftcrobotcontroller.opmodes;

public class RobotInfo
{
    //
    // DriveBase subsystem.
    //
    public static final double DRIVE_INCHES_PER_CLICK   = 1.0;

    public static final double DRIVE_KP                 = 1.0;
    public static final double DRIVE_KI                 = 0.0;
    public static final double DRIVE_KD                 = 0.0;
    public static final double DRIVE_KF                 = 0.0;
    public static final double DRIVE_TOLERANCE          = 2.0;
    public static final double DRIVE_SETTLING           = 0.2;

    public static final double TURN_KP                  = 1.0;
    public static final double TURN_KI                  = 0.0;
    public static final double TURN_KD                  = 0.0;
    public static final double TURN_KF                  = 0.0;
    public static final double TURN_TOLERANCE           = 2.0;
    public static final double TURN_SETTLING            = 0.2;

    //
    // ButtonPusher subsystem.
    //
    public static final double PUSHER_LEFT_POSITION = 0.0;
    public static final double PUSHER_NEUTRAL_POSITION = 0.5;
    public static final double PUSHER_RIGHT_POSITION = 1.0;

    //
    // CattleGuard subsystem.
    //
    public static final double CATTLEGUARD_RETRACT_POSITION = 0.0;
    public static final double CATTLEGUARD_EXTEND_POSITION = 1.0;

    //
    // ClimberRelease subsystem.
    //
    public static final double ARM_RETRACT_POSITION = 0.0;
    public static final double ARM_EXTEND_POSITION = 1.0;

    //
    // HangingHook subsystem.
    //
    public static final double HANGINGHOOK_RETRACT_POSITION = 0.0;
    public static final double HANGINGHOOK_EXTEND_POSITION = 1.0;

}   //class RobotInfo
