package ftc3543;

import ftclib.FtcGamepad;
import ftclib.FtcOpMode;
import hallib.HalDashboard;
import trclib.TrcRobot;

public class FtcTeleOp extends FtcOpMode implements FtcGamepad.ButtonHandler
{
    public HalDashboard dashboard;
    public FtcRobot robot;

    private FtcGamepad driverGamepad;
    private FtcGamepad operatorGamepad;

    //
    // Implements FtcOpMode abstract method.
    //

    @Override
    public void initRobot()
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
    }   //initRobot

    //
    // Overrides TrcRobot.RobotMode methods.
    //

    @Override
    public void startMode()
    {
        dashboard.clearDisplay();
        robot.startMode(TrcRobot.RunMode.TELEOP_MODE);
        //
        // There is an issue with the gamepad objects that may not be valid
        // before waitForStart() is called. So we call the setGamepad method
        // here to update their references in case they have changed.
        //
        driverGamepad.setGamepad(gamepad1);
        operatorGamepad.setGamepad(gamepad2);
    }   //startMode

    @Override
    public void stopMode()
    {
        robot.stopMode(TrcRobot.RunMode.TELEOP_MODE);
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
        dashboard.displayPrintf(1, "leftPower=%.2f,rightPower=%.2f", leftPower, rightPower);
        dashboard.displayPrintf(2, "yPos=%.2f,heading=%.2f",
                                robot.driveBase.getYPosition(), robot.driveBase.getHeading());
        //
        // Elevator subsystem.
        //
        double elevatorPower = operatorGamepad.getRightStickY(true);
        robot.elevator.setPower(elevatorPower);
        dashboard.displayPrintf(3, "elevatorPower=%.2f,height=%.2f",
                                elevatorPower, robot.elevator.getHeight());
        dashboard.displayPrintf(4, "lowerLimit=%d,upperLimit=%d",
                                robot.elevator.isLowerLimitSwitchPressed()? 1: 0,
                                robot.elevator.isUpperLimitSwitchPressed()? 1: 0);
        //
        // Slider subsystem.
        //
        double slidePower = operatorGamepad.getLeftStickY(true);
        robot.slider.setPower(slidePower);
        dashboard.displayPrintf(5, "slidePower=%.2f,length=%.2f",
                                slidePower, robot.slider.getLength());
        dashboard.displayPrintf(6, "lowerLimit=%d,upperLimit=%d",
                                robot.slider.isLowerLimitSwitchPressed()? 1: 0,
                                robot.slider.isUpperLimitSwitchPressed()? 1: 0);
    }   //runPeriodic

    //
    // Implements FtcGamepad.ButtonHandler interface.
    //

    @Override
    public void gamepadButtonEvent(FtcGamepad gamepad, int btnMask, boolean pressed)
    {
        dashboard.displayPrintf(7, "%s: %04x->%s",
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
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    break;

                case FtcGamepad.GAMEPAD_LBUMPER:
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
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
                        robot.elevator.setBrakeOn(false);
                    }
                    break;

                case FtcGamepad.GAMEPAD_B:
                    if (pressed)
                    {
                        robot.rightButtonPusher.extend();
                    }
                    else
                    {
                        robot.rightButtonPusher.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_X:
                    if (pressed)
                    {
                        robot.leftButtonPusher.extend();
                    }
                    else
                    {
                        robot.leftButtonPusher.retract();
                    }
                    break;

                case FtcGamepad.GAMEPAD_Y:
                    if (pressed)
                    {
                        robot.elevator.setBrakeOn(true);
                    }
                    break;

                case FtcGamepad.GAMEPAD_LBUMPER:
                    if (pressed)
                    {
                        robot.leftWing.setPosition(RobotInfo.WING_LEFT_EXTEND_POSITION);
                    }
                    else
                    {
                        robot.leftWing.setPosition(RobotInfo.WING_LEFT_RETRACT_POSITION);
                    }
                    break;

                case FtcGamepad.GAMEPAD_RBUMPER:
                    if (pressed)
                    {
                        robot.rightWing.setPosition(RobotInfo.WING_RIGHT_EXTEND_POSITION);
                    }
                    else
                    {
                        robot.rightWing.setPosition(RobotInfo.WING_RIGHT_RETRACT_POSITION);
                    }
                    break;

                case FtcGamepad.GAMEPAD_START:
                    if (pressed)
                    {
                        robot.elevator.zeroCalibrate(RobotInfo.ELEVATOR_CAL_POWER);
                    }
                    break;

                case FtcGamepad.GAMEPAD_BACK:
                    if (pressed)
                    {
                        robot.slider.zeroCalibrate(RobotInfo.SLIDER_CAL_POWER);
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_UP:
                    if (pressed)
                    {
                        robot.hookServo.setPosition(RobotInfo.HANGINGHOOK_EXTEND_POSITION);
                    }
                    else
                    {
                        robot.hookServo.setControllerOn(false);
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_DOWN:
                    if (pressed)
                    {
                        robot.hookServo.setPosition(RobotInfo.HANGINGHOOK_RETRACT_POSITION);
                    }
                    else
                    {
                        robot.hookServo.setControllerOn(false);
                    }
                    break;

                case FtcGamepad.GAMEPAD_DPAD_LEFT:
                    break;

                case FtcGamepad.GAMEPAD_DPAD_RIGHT:
                    break;
            }
        }
    }   //gamepadButtonEvent

}   //class FtcTeleOp
