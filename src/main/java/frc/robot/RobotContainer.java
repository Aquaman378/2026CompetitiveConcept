package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Feeder;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Shooter;

public class RobotContainer {
    private final CommandXboxController driverController = new CommandXboxController(0);

    // Subsystems
    private final Intake intake = new Intake();
    private final Feeder feeder = new Feeder();
    private final Shooter shooter = new Shooter();

    public RobotContainer() {
        configureButtonBindings();
    }

    private void configureButtonBindings() {
        // Right Trigger: Run Intake Rollers
        driverController.rightTrigger()
            .whileTrue(Commands.run(() -> intake.setRollers(0.8), intake))
            .onFalse(Commands.runOnce(intake::stop, intake));

        // Left Trigger: Run Shooter at 85%
        driverController.leftTrigger()
            .whileTrue(Commands.run(() -> shooter.setSpeed(0.85), shooter))
            .onFalse(Commands.runOnce(shooter::stop, shooter));

        // A Button: Run Feeder to load game piece into shooter
        driverController.a()
            .whileTrue(Commands.run(() -> feeder.runFeeder(0.7), feeder))
            .onFalse(Commands.runOnce(feeder::stop, feeder));
    }

    public Command getAutonomousCommand() {
        return Commands.none();
    }
}