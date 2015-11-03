package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.hardware.Gamepad;

import hallib.FtcGamepad;
import hallib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcBooleanState;
import trclib.TrcRobot;

public class FtcTeleOp extends FtcOpMode implements FtcGamepad.ButtonHandler
{
    private HalDashboard dashboard;
    private FtcRobot robot;
    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;
    private TrcBooleanState climbMode;
    private TrcBooleanState hookDeployed;
    private TrcBooleanState cattleGuardDeployed;

    //
    // Implements FtcOpMode abstract methods.
    //

    @Override
    public void robotInit()
    {
        //
        // Initializing global objects.
        //
        dashboard = HalDashboard.getInstance();
        robot = new FtcRobot(TrcRobot.RunMode.TELEOP_MODE);
        //
        // Initializing Gamepads.
        //
        driverGamepad = new FtcGamepad("DriverGamepad", gamepad1, this);
        operatorGamepad = new FtcGamepad("OperatorGamepad", gamepad2, this);
        driverGamepad.setYInverted(true);
        operatorGamepad.setYInverted(true);
        //
        // Chainsaw subsystem.
        //
        climbMode = new TrcBooleanState("climbMode", false);
        //
        // Hanging Hook subsystem.
        //
        hookDeployed = new TrcBooleanState("hangingHook", false);
        //
        // Cattle Guard subsystem.
        //
        cattleGuardDeployed = new TrcBooleanState("cattleGuardDeployed", false);

    }   //robotInit

    @Override
    public void startMode()
    {
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
        robot.driveBase.tankDrive(leftPower, rightPower);
        dashboard.displayPrintf(1, "leftPower = %f", leftPower);
        dashboard.displayPrintf(2, "rightPower = %f", rightPower);
        //
        // Elevator subsystem.
        //
        double elevatorPower = operatorGamepad.getRightStickY(true);
        robot.elevator.setPower(elevatorPower);
        dashboard.displayPrintf(3, "elevatorPower = %f", elevatorPower);
        dashboard.displayPrintf(4, "lowerLimit = %s, upperLimit = %s",
                                robot.elevator.isLowerLimitSwitchPressed()? "pressed": "released",
                                robot.elevator.isUpperLimitSwitchPressed()? "pressed": "released");
        robot.elevator.displayDebugInfo(5);
        //
        // Chainsaw subsystem.
        //
        double chainsawPower = 0.0;
        if (climbMode.getState())
        {
            chainsawPower = (leftPower + rightPower)/2.0;
        }
        robot.chainsaw.setPower(chainsawPower);
        dashboard.displayPrintf(7, "chainsawPower = %f", chainsawPower);
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
                        robot.leftArm.extend();
                    }
                    else
                    {
                        robot.leftArm.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    if (pressed)
                    {
                        robot.rightArm.extend();
                    }
                    else
                    {
                        robot.rightArm.retract();
                    }
                    break;
            }
        }
        else if (gamepad == operatorGamepad)
        {
            switch (btnMask)
            {
                case FtcGamepad.GAMEPAD_A:
                    if (pressed)
                    {
                        cattleGuardDeployed.toggleState();
                        if (cattleGuardDeployed.getState())
                        {
                            robot.cattleGuard.extend();
                        }
                        else
                        {
                            robot.cattleGuard.retract();
                        }
                    }
                    break;

                case FtcGamepad.GAMEPAD_B:
                    if (pressed)
                    {
                        hookDeployed.toggleState();
                        if (hookDeployed.getState())
                        {
                            robot.hangingHook.extend();
                        }
                        else
                        {
                            robot.hangingHook.retract();
                        }
                    }
                    break;

                case FtcGamepad.GAMEPAD_X:
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    robot.elevator.setElevatorOverride(pressed);
                    break;

                case FtcGamepad.GAMEPAD_BACK:
                    if (pressed)
                    {
                        robot.elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
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
