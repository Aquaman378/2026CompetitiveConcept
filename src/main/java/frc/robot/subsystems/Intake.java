package frc.robot.subsystems;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Ports;

public class Intake extends SubsystemBase {
    public enum Position {
        INTAKE,
        STOW
    }

    private final TalonFX pivotMotor = new TalonFX(Ports.kIntakePivot, new CANBus(Ports.kCANivoreBus));
    private final TalonFX rollerMotor = new TalonFX(Ports.kIntakeRollers, new CANBus(Ports.kCANivoreBus));
    private final DutyCycleOut dutyCycle = new DutyCycleOut(0);

    public Intake() {}

    public void set(Position pos) {
        if (pos == Position.INTAKE) {
            pivotMotor.setControl(dutyCycle.withOutput(0.5));
        } else {
            pivotMotor.setControl(dutyCycle.withOutput(0.0));
        }
    }

    public void setRollers(double speed) {
        rollerMotor.setControl(dutyCycle.withOutput(speed));
    }

    public void stop() {
        rollerMotor.setControl(dutyCycle.withOutput(0));
        pivotMotor.setControl(dutyCycle.withOutput(0));
    }

    public Command intakeCommand() {
        return Commands.run(() -> setRollers(0.8), this);
    }

    public Command agitateCommand() {
        return Commands.run(() -> setRollers(0.4), this);
    }
}