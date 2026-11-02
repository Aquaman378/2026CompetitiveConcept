import os
import re

def resolve_ports(ports_file_path):
    """Parses Ports.java to map variable names to physical CAN/PWM/DIO IDs."""
    port_map = {}
    if not os.path.exists(ports_file_path):
        return port_map
        
    with open(ports_file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()
        
    # Match integer constants like: public static final int TOP_MOTOR_ID = 12;
    # or inside interfaces/classes: int LEFT_INTAKE = 5;
    matches = re.findall(r'(?:public\s+static\s+final\s+int|int)\s+([A-Za-z0-9_]+)\s*=\s*(\d+);', content)
    for name, value in matches:
        port_map[name] = value
    return port_map

def scan_repository():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    java_dir = os.path.join(script_dir, "src", "main", "java", "frc")
    
    if not os.path.exists(java_dir):
        print(f"ERROR: Could not locate directory: {java_dir}")
        return

    ports_file = os.path.join(java_dir, "robot", "Ports.java")
    port_map = resolve_ports(ports_file)

    # Specific WPILib & CTRE device pattern definitions
    patterns = [
        ('TalonFX', r'(?:TalonFX|WPI_TalonFX)\s*\(\s*([^,\)]+)'),
        ('TalonSRX', r'(?:TalonSRX|WPI_TalonSRX)\s*\(\s*([^,\)]+)'),
        ('CANSparkMax', r'CANSparkMax\s*\(\s*([^,\)]+)'),
        ('CANSparkFlex', r'CANSparkFlex\s*\(\s*([^,\)]+)'),
        ('CANcoder', r'(?:CANcoder|WPI_CANcoder)\s*\(\s*([^,\)]+)'),
        ('Pigeon2', r'Pigeon2\s*\(\s*([^,\)]+)'),
        ('DigitalInput', r'DigitalInput\s*\(\s*([^,\)]+)'),
        ('Solenoid', r'Solenoid\s*\([^,\)]*,\s*([^,\)]+)')
    ]

    hardware_found = []

    for root, _, files in os.walk(java_dir):
        for file in files:
            if file.endswith('.java'):
                filepath = os.path.join(root, file)
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                    text = f.read()

                for dev_type, regex in patterns:
                    for match in re.finditer(regex, text):
                        raw_arg = match.group(1).strip()
                        # Clean constant namespaces like Ports.Shooter.TOP_MOTOR -> TOP_MOTOR
                        clean_var = raw_arg.split('.')[-1]
                        resolved_id = port_map.get(clean_var, raw_arg)
                        
                        hardware_found.append({
                            'device': dev_type,
                            'id': resolved_id,
                            'file': file
                        })

    # Generate Mermaid Diagram File
    lines = [
        "# Robot Hardware Wiring Diagram",
        "",
        "```mermaid",
        "graph TD",
        "    rio[roboRIO Main Controller]"
    ]
    
    seen_nodes = set()
    for item in hardware_found:
        node_id = f"dev_{item['device']}_{re.sub(r'[^a-zA-Z0-9_]', '_', item['id'])}"
        label = f"{item['device']}<br/>ID/Port: {item['id']}<br/>({item['file']})"
        
        if node_id not in seen_nodes:
            seen_nodes.add(node_id)
            lines.append(f'    {node_id}["{label}"]')
            if item['device'] in ['DigitalInput', 'Solenoid']:
                lines.append(f"    rio -->|DIO/PCM| {node_id}")
            else:
                lines.append(f"    rio -->|CAN Bus| {node_id}")

    lines.append("```\n")
    
    output_path = os.path.join(script_dir, "WIRING_DIAGRAM.md")
    with open(output_path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines))
        
    print("=" * 60)
    print(f"SUCCESS: Mapped {len(seen_nodes)} hardware components.")
    print(f"Output generated at: {output_path}")
    print("=" * 60)

if __name__ == '__main__':
    scan_repository()