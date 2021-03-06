/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frc869.robot.code2014;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Kevvers
 */
public class Catapult {
    
    private static final int TALON_PWM = 8;
    private static final int CATAPULT_LIMIT_DIO = 1;
    private static final int BALL_SETTLED_LIMIT_DIO = 2;
    private static final int SAFETY_DIO = 5;
    private final Talon catapultMotor;
    private final DigitalInput catapultSwitch;
    private DigitalInput safety;
    
    private DigitalInput ballSettled;
    
    private boolean firing;
    
    private double fireTime;
    private static Catapult instance;
    private boolean firedAuto;
    
    public boolean isFiredAuto() {
        return firedAuto;
    }
    
    public void setFiredAuto(boolean firedAuto) {
        this.firedAuto = firedAuto;
    }
    
    private Catapult() {
        firing = false;
        firedAuto = false;
        // ballSettled = new DigitalInput(BALL_SETTLED_LIMIT_DIO);
        catapultSwitch = new DigitalInput(CATAPULT_LIMIT_DIO);
        safety = new DigitalInput(SAFETY_DIO);
        catapultMotor = new Talon(TALON_PWM);
    }
    
    public static Catapult getInstance() {
        if (instance == null) {
            instance = new Catapult();
        }
        return instance;
    }
    
    public boolean isFiring() {
        return firing;
    }
    
    public void control() {
        if (!isSafetyIn()) {
            firedAuto = false;
            if (Logitech.getInstance().getR2()) {                 
                firing = true;
                catapultMotor.set(-1.00);
            } else if (!catapultSwitch.get() || !firing) {
                catapultMotor.set(0);
                firing = false;
            } else {
                catapultMotor.set(-1.00);
            }
        } else {
            catapultMotor.set(0);
            firing = false;
        }
    }
    
    public boolean fire() {
        if (!isSafetyIn()) {
            if (!firedAuto) {
                fireTime = System.currentTimeMillis();
                firedAuto = true;
                firing = true;
                catapultMotor.set(-1.00);
                return false;
            } else if ((!catapultSwitch.get() || !firing) && (System.currentTimeMillis() - fireTime) > 1000) {
                catapultMotor.set(0);
                firing = false;
                return true;
            } else {
                catapultMotor.set(-1.00);
                return false;
            }
        } else {
            catapultMotor.set(0);
            firing = false;
            return true;
        }
        
    }
    
    public void resetAuto() {
        firedAuto = false;
        fireTime = 0;
        firing = false;
    }
    
    public boolean isLimitHit() {
        return !(catapultSwitch.get());
    }
    
    public DigitalInput getBallSettledSwitch() {
        return ballSettled;
    }
    
    public boolean isSafetyIn() {        
        return !safety.get() && !DriverStation.getInstance().getDigitalIn(8);
    }
    
    public boolean showSafetyValue() {
        return safety.get();
    }
}
