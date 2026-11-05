package frc.robot.subsystems;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Ports;

public class Feeder extends SubsystemBase {
    private final DutyCycleOut dutyCycle = new DutyCycleOut(0);
    private final TalonFX feederMotor = new TalonFX(Ports.kFeeder, new CANBus(Ports.kCANivoreBus));

    public Feeder() {}

    public void runFeeder(double speed) {
        feederMotor.setControl(dutyCycle.withOutput(speed));
    }

    public void stop() {
        feederMotor.setControl(dutyCycle.withOutput(0));
    }

    public Command feedCommand() {
        return Commands.run(() -> runFeeder(0.75), this);
    }
}