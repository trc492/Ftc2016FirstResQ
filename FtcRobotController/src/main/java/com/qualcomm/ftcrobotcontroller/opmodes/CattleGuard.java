package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.FtcServo;
import trclib.TrcServo;

public class CattleGuard
{
    //
    // This component consists of two servo motors that
    // will deploy the cattle guard out to deflect debris.
    // It provides a method to deploy or undeploy the
    // cattle guard.
    //
    private FtcServo leftServo;
    private FtcServo rightServo;
    private TrcServo cattleGuardServo;

    public CattleGuard()
    {
        leftServo = new FtcServo("leftCattleGuard");
        rightServo = new FtcServo("rightCattleGuard");
        rightServo.setReverse(true);
        cattleGuardServo = new TrcServo(leftServo, rightServo);
    }   //CattleGuard

    public void extend()
    {
        cattleGuardServo.setPosition(RobotConfig.CATTLEGUARD_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        cattleGuardServo.setPosition(RobotConfig.CATTLEGUARD_RETRACT_POSITION);
    }   //retract

}   //class CattleGuard
