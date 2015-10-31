package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.Gamepad;

import hallib.FtcDcMotor;
import hallib.FtcGamepad;
import hallib.FtcRobot;
import hallib.HalDashboard;
import trclib.TrcBooleanState;
import trclib.TrcDriveBase;

public class FtcTeleOp extends FtcRobot implements FtcGamepad.ButtonHandler
{
    private HalDashboard dashboard;
    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;
    private FtcDcMotor leftFrontWheel;
    private FtcDcMotor rightFrontWheel;
    private FtcDcMotor leftRearWheel;
    private FtcDcMotor rightRearWheel;
    private TrcDriveBase driveBase;
    private Chainsaw chainsaw;
    private Elevator elevator;
    private HangingHook hangingHook;
    private ClimberRelease leftArm;
    private ClimberRelease rightArm;
    private CattleGuard cattleGuard;
    private TrcBooleanState cattleGuardDeployed;
    private TrcBooleanState climbMode;

    //
    // Implements FtcRobot abstract methods.
    //

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        hardwareMap.logDevices();
        dashboard = HalDashboard.getInstance();
        //
        // Initializing Gamepads.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, this);
        operatorGamepad = new FtcGamepad("OperatorGamepad", gamepad2, this);
        driverGamepad.setYInverted(true);
        operatorGamepad.setYInverted(true);
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
                null,
                null);
        //
        // Chainsaw subsystem.
        //
        chainsaw = new Chainsaw();
        climbMode = new TrcBooleanState("climbMode", false);
        //
        // Elevator subsystem.
        //
        elevator = new Elevator();
        //
        // Hanging Hook subsystem.
        //
        hangingHook = new HangingHook();
        //
        // Climber Release subsystem.
        //
        leftArm = new ClimberRelease("leftArm");
        rightArm = new ClimberRelease("rightArm");
        //
        // Cattle Guard subsystem.
        //
        cattleGuard = new CattleGuard();
        cattleGuardDeployed = new TrcBooleanState("cattleGuardDeployed", false);
    }   //robotInit

    @Override
    public void startMode()
    {
        elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
    }   //startMode

    @Override
    public void stopMode()
    {
    }   //stopMode

    @Override
    public void runPeriodic()
    {
        //
        // DriveBase subsystem.
        //
        double leftPower  = driverGamepad.getLeftStickY(true);
        double rightPower = driverGamepad.getRightStickY(true);
        driveBase.tankDrive(leftPower, rightPower);
        dashboard.displayPrintf(1, "leftPower = %f", leftPower);
        dashboard.displayPrintf(2, "rightPower = %f", rightPower);
        //
        // Elevator subsystem.
        //
        double elevatorPower = operatorGamepad.getRightStickY(true);
        elevator.setPower(elevatorPower);
        dashboard.displayPrintf(3, "elevatorPower = %f", elevatorPower);
        dashboard.displayPrintf(4, "lowerLimit = %s, upperLimit = %s",
                                elevator.isLowerLimitSwitchPressed()? "pressed": "released",
                                elevator.isUpperLimitSwitchPressed()? "pressed": "released");
        elevator.displayDebugInfo(5);
        //
        // Chainsaw subsystem.
        //
        if (climbMode.getState())
        {
            double chainsawPower = (leftPower + rightPower)/2.0;
            chainsaw.setPower(chainsawPower);
            dashboard.displayPrintf(7, "chainsawPower = %f", chainsawPower);
        }
    }   //runPeriodic

    @Override
    public void runContinuous()
    {
    }   //runContinuous

    //
    // Implements FtcGamepad.ButtonHandler interface.
    //

    @Override
    public void gamepadButtonEvent(FtcGamepad gamepad, final int btnMask, final boolean pressed)
    {
        dashboard.displayPrintf(8, "%s: %04x->%s",
                gamepad.toString(), btnMask, pressed? "Pressed": "Released");
        if (gamepad == driverGamepad)
        {
            switch (btnMask)
            {
                case FtcGamepad.GAMEPAD_A:
                    if (pressed)
                    {
                        cattleGuardDeployed.toggleState();
                        if (cattleGuardDeployed.getState())
                        {
                            cattleGuard.extend();
                        }
                        else
                        {
                            cattleGuard.retract();
                        }
                    }
                    break;

                case FtcGamepad.GAMEPAD_B:
                    break;

                case FtcGamepad.GAMEPAD_X:
                    if (pressed)
                    {
                        climbMode.toggleState();
                    }
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;

                case FtcGamepad.GAMEPAD_LBUMPER:
                    if (pressed)
                    {
                        leftArm.extend();
                    }
                    else
                    {
                        leftArm.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    if (pressed)
                    {
                        rightArm.extend();
                    }
                    else
                    {
                        rightArm.retract();
                    }
                    break;
            }
        }
        else if (gamepad == operatorGamepad)
        {
            switch (btnMask)
            {
                case FtcGamepad.GAMEPAD_A:
                    break;

                case FtcGamepad.GAMEPAD_B:
                    break;

                case FtcGamepad.GAMEPAD_X:
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    elevator.setElevatorOverride(pressed);
                    break;

                case FtcGamepad.GAMEPAD_BACK:
                    if (pressed)
                    {
                        elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
                    }
                    break;
            }
        }
    }   //gamepadButtonEvent

    public Gamepad getGamepad(String name)
    {
        if (name.equals("DriverGamepad"))
        {
            return gamepad1;
        }
        else if (name.equals("OperatorGamepad"))
        {
            return gamepad2;
        }
        else
        {
            return null;
        }
    }   //??? TEMP
}   //class FtcTeleOp
