package frc.robot.subsystems;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Ports;

public class Shooter extends SubsystemBase {
    private final TalonFX middleMotor = new TalonFX(Ports.kShooterMiddle, Ports.kCANivoreBus);
    private final TalonFX leftMotor = new TalonFX(Ports.kShooterLeft, Ports.kCANivoreBus);
    private final TalonFX rightMotor = new TalonFX(Ports.kShooterRight, Ports.kCANivoreBus);

    private final DutyCycleOut dutyCycle = new DutyCycleOut(0);
    private double targetRPM = 0;

    public Shooter() {
        // Follower constructor matching Phoenix 6 API
        leftMotor.setControl(new Follower(Ports.kShooterMiddle, false));
        rightMotor.setControl(new Follower(Ports.kShooterMiddle, true));
    }

    public void setSpeed(double speed) {
        middleMotor.setControl(dutyCycle.withOutput(speed));
    }

    public void setRPM(double rpm) {
        this.targetRPM = rpm;
        setSpeed(rpm / 5000.0);
    }

    public boolean isVelocityWithinTolerance() {
        return true;
    }

    public void stop() {
        middleMotor.setControl(dutyCycle.withOutput(0));
    }

    public Command spinUpCommand(double rpm) {
        return Commands.run(() -> setRPM(rpm), this);
    }

    public Command spinUpCommand(int rpm) {
        return spinUpCommand((double) rpm);
    }

    public Command dashboardSpinUpCommand() {
        return Commands.run(() -> setSpeed(0.85), this);
    }
}