package com.qualcomm.ftcrobotcontroller.opmodes;

import hallib.HalServo;
import trclib.TrcServo;

public class CattleGuard
{
    //
    // This component consists of two servo motors that
    // will deploy the cattle guard out to deflect debris.
    // It provides a method to deploy or undeploy the
    // cattle guard.
    //
    private static final double CATTLEGUARD_RETRACT_POSITION = 0.0;
    private static final double CATTLEGUARD_EXTEND_POSITION = 1.0;

    private HalServo leftServo;
    private HalServo rightServo;
    private TrcServo cattleGuardServo;

    public CattleGuard()
    {
        leftServo = new HalServo("leftCattleGuard");
        rightServo = new HalServo("rightCattleGuard");
        rightServo.setReverse(true);
        cattleGuardServo = new TrcServo(leftServo, rightServo);
    }   //CattleGuard

    public void extend()
    {
        cattleGuardServo.setPosition(CATTLEGUARD_EXTEND_POSITION);
    }   //extend

    public void retract()
    {
        cattleGuardServo.setPosition(CATTLEGUARD_RETRACT_POSITION);
    }   //retract

}   //class CattleGuard
