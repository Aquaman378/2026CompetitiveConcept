package frc.robot;

public final class Ports {
    // CAN Bus Identifiers
    public static final String kCANivoreBus = "canivore";
    public static final String kRoboRioCANBus = "rio";

    // Power & Sensors
    public static final int kPigeon = 1;

    // Intake Subsystem
    public static final int kIntakePivot = 9;
    public static final int kIntakeRollers = 10;

    // Indexer / Transport (Floor & Feeder)
    public static final int kFloor = 11;
    public static final int kFeeder = 12;

    // Shooter Subsystem (Krakens / Falcon 500s)
    public static final int kShooterLeft = 13;
    public static final int kShooterMiddle = 14;
    public static final int kShooterRight = 15;

    // Hood & Hanger
    public static final int kHood = 16;
    public static final int kHoodLeftServo = 0;  // PWM Channel 0
    public static final int kHoodRightServo = 1; // PWM Channel 1
    public static final int kHanger = 17;
}