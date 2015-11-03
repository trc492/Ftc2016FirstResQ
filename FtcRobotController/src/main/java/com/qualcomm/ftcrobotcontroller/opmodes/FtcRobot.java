package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

import ftclib.FtcDcMotor;
import ftclib.FtcHiTechnicGyro;
import ftclib.FtcOpMode;
import hallib.HalSpeedController;
import trclib.TrcDriveBase;
import trclib.TrcMotorPosition;
import trclib.TrcPidController;
import trclib.TrcPidDrive;
import trclib.TrcRobot;

public class FtcRobot implements TrcPidController.PidInput, TrcMotorPosition
{
    private FtcOpMode ftcOpMode = FtcOpMode.getInstance();
    //
    // Sensors.
    //
    public FtcHiTechnicGyro gyro;
    public TouchSensor touchSensor;
    public UltrasonicSensor sonarSensor;
    public ColorSensor colorSensor;
    public OpticalDistanceSensor lightSensor;
    //
    // DriveBase subsystem.
    //
    public FtcDcMotor leftFrontWheel;
    public FtcDcMotor rightFrontWheel;
    public FtcDcMotor leftRearWheel;
    public FtcDcMotor rightRearWheel;
    public TrcDriveBase driveBase;
    public TrcPidController pidCtrlDrive;
    public TrcPidController pidCtrlTurn;
    public TrcPidDrive pidDrive;
    //
    // Chainsaw subsystem.
    //
    public Chainsaw chainsaw;
    //
    // Elevator subsystem.
    //
    public Elevator elevator;
    //
    // HangingHook subsystem.
    //
    public HangingHook hangingHook;
    //
    // ClimberRelease subsystem.
    //
    public ClimberRelease leftArm;
    public ClimberRelease rightArm;
    //
    // CattleGuard subsystem.
    //
    public CattleGuard cattleGuard;

    public FtcRobot(TrcRobot.RunMode runMode)
    {
        ftcOpMode.hardwareMap.logDevices();
        //
        // Initialize sensors.
        //
        gyro = new FtcHiTechnicGyro("gyroSensor");
        colorSensor = ftcOpMode.hardwareMap.colorSensor.get("colorSensor");
        lightSensor = ftcOpMode.hardwareMap.opticalDistanceSensor.get("lightSensor");
        touchSensor = ftcOpMode.hardwareMap.touchSensor.get("touchSensor");
        sonarSensor = ftcOpMode.hardwareMap.ultrasonicSensor.get("sonarSensor");
        //
        // DriveBase subsystem.
        //
        leftFrontWheel = new FtcDcMotor("leftFrontWheel");
        rightFrontWheel = new FtcDcMotor("rightFrontWheel");
        leftRearWheel = new FtcDcMotor("leftRearWheel");
        rightRearWheel = new FtcDcMotor("rightRearWheel");
        leftFrontWheel.setInverted(true);
        leftRearWheel.setInverted(true);
        driveBase = new TrcDriveBase(
                leftFrontWheel,
                leftRearWheel,
                rightFrontWheel,
                rightRearWheel,
                this,
                gyro);
        pidCtrlDrive = new TrcPidController(
                "DrivePid",
                RobotInfo.DRIVE_KP, RobotInfo.DRIVE_KI, RobotInfo.DRIVE_KD,
                RobotInfo.DRIVE_KF, RobotInfo.DRIVE_TOLERANCE, RobotInfo.DRIVE_SETTLING,
                this, 0);
        pidCtrlTurn = new TrcPidController(
                "TurnPid",
                RobotInfo.TURN_KP, RobotInfo.TURN_KI, RobotInfo.TURN_KD,
                RobotInfo.TURN_KF, RobotInfo.TURN_TOLERANCE, RobotInfo.TURN_SETTLING,
                this, 0);
        pidDrive = new TrcPidDrive("PidDrive", driveBase, null, pidCtrlDrive, pidCtrlTurn);
        //
        // Chainsaw subsystem.
        //
        chainsaw = new Chainsaw();
        //
        // Elevator subsystem.
        //
        elevator = new Elevator();
        elevator.reverseEncoder(true);
        elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
        //
        // Hanging Hook subsystem.
        //
        hangingHook = new HangingHook();

    }   //FtcRobot

    //
    // Implements TrcPidController.PidInput
    //

    @Override
    public double getInput(TrcPidController pidCtrl)
    {
        double input = 0.0;

        if (pidCtrl == pidCtrlDrive)
        {
            input = driveBase.getYPosition()*RobotInfo.DRIVE_INCHES_PER_CLICK;
        }
        else if (pidCtrl == pidCtrlTurn)
        {
            input = driveBase.getHeading();
        }

        return input;
    }   //getInput

    //
    // Implements TrcMotorPosition
    //
    @Override
    public double getMotorPosition(HalSpeedController speedController)
    {
        return speedController.getCurrentPosition();
    }   //getMotorPosition

    @Override
    public double getMotorSpeed(HalSpeedController speedController)
    {
        return 0.0;
    }   //getMotorSpeed

    @Override
    public void resetMotorPosition(HalSpeedController speedController)
    {
        speedController.resetCurrentPosition();
    }   //resetMotorPosition

    @Override
    public void reversePositionSensor(HalSpeedController speedController, boolean flip)
    {
    }   //reversePositionSensor

    @Override
    public boolean isForwardLimitSwitchActive(HalSpeedController speedController)
    {
        return false;
    }   //isForwardLimitSwitchActive

    @Override
    public boolean isReverseLimitSwitchActive(HalSpeedController speedController)
    {
        return false;
    }   //isReverseLimitSwitchActive

}   //class FtcRobot
