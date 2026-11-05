package frc.robot.subsystems;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Ports;

public class Shooter extends SubsystemBase {

    private final TalonFX middleMotor = new TalonFX(Ports.kShooterMiddle, new CANBus(Ports.kCANivoreBus));
    private final TalonFX leftMotor = new TalonFX(Ports.kShooterLeft, new CANBus(Ports.kCANivoreBus));
    private final TalonFX rightMotor = new TalonFX(Ports.kShooterRight, new CANBus(Ports.kCANivoreBus));

    private final VelocityVoltage velocityRequest = new VelocityVoltage(0);
    private double targetRPM = 0;

    public Shooter() {
        TalonFXConfiguration cfg = new TalonFXConfiguration();
        cfg.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        middleMotor.getConfigurator().apply(cfg);
        leftMotor.getConfigurator().apply(cfg);
        rightMotor.getConfigurator().apply(cfg);

        leftMotor.setControl(new Follower(Ports.kShooterMiddle, true));
        rightMotor.setControl(new Follower(Ports.kShooterMiddle, false));
    }

    public void setSpeed(double speed) {
        middleMotor.set(speed);
    }

    public void setRPM(double rpm) {
        this.targetRPM = rpm;
        double rps = rpm / 60.0;
        middleMotor.setControl(velocityRequest.withVelocity(rps));
    }

    public void stop() {
        middleMotor.stopMotor();
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }

    public boolean isVelocityWithinTolerance() {
        double currentRPS = middleMotor.getVelocity().getValueAsDouble();
        double targetRPS = targetRPM / 60.0;
        return Math.abs(currentRPS - targetRPS) < 2.0;
    }

    public Command spinUpCommand(double rpm) {
        return this.run(() -> setRPM(rpm));
    }

    public Command dashboardSpinUpCommand() {
        return this.run(() -> setSpeed(0.85));
    }
}
