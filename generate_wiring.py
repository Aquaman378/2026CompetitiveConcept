import os
import re

# Regex patterns to catch FRC hardware initializations & CAN IDs
PATTERNS = {
    'TalonFX / Falcon 500': r'(?:TalonFX|WPI_TalonFX)\s*\(\s*([^,\)]+)',
    'TalonSRX': r'(?:TalonSRX|WPI_TalonSRX)\s*\(\s*([^,\)]+)',
    'CANSparkMax': r'CANSparkMax\s*\(\s*([^,\)]+)',
    'CANSparkFlex': r'CANSparkFlex\s*\(\s*([^,\)]+)',
    'VictorSPX': r'(?:VictorSPX|WPI_VictorSPX)\s*\(\s*([^,\)]+)',
    'CANcoder': r'(?:CANcoder|WPI_CANcoder)\s*\(\s*([^,\)]+)',
    'Pigeon2 / IMU': r'Pigeon2\s*\(\s*([^,\)]+)',
    'Solenoid': r'Solenoid\s*\([^,\)]*,\s*([^,\)]+)',
    'Digital Input': r'DigitalInput\s*\(\s*([^,\)]+)',
    'Analog Input': r'AnalogInput\s*\(\s*([^,\)]+)',
}

def scan_frc_code(project_dir):
    hardware_list = []
    
    for root, _, files in os.walk(project_dir):
        for file in files:
            if file.endswith(('.java', '.cpp', '.h')):
                filepath = os.path.join(root, file)
                with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read()
                    
                    for dev_type, pattern in PATTERNS.items():
                        matches = re.finditer(pattern, content)
                        for m in matches:
                            raw_id = m.group(1).strip()
                            hardware_list.append({
                                'type': dev_type,
                                'id': raw_id,
                                'source_file': file
                            })
    return hardware_list

def generate_mermaid_diagram(hardware):
    lines = ["graph TD", "    rio[roboRIO Main Controller]"]
    seen = set()
    
    for hw in hardware:
        # Clean ID string for node identifier
        clean_id_name = re.sub(r'[^a-zA-Z0-9_]', '_', hw['id'])
        node_id = f"dev_{hw['type'].replace(' ', '_').replace('/', '_')}_{clean_id_name}"
        label = f"{hw['type']}<br/>ID: {hw['id']}<br/>({hw['source_file']})"
        
        if node_id not in seen:
            seen.add(node_id)
            lines.append(f'    {node_id}["{label}"]')
            
            if any(k in hw['type'] for k in ["Digital", "Analog", "Solenoid"]):
                lines.append(f"    rio -->|DIO/PWM/Pneumatics| {node_id}")
            else:
                lines.append(f"    rio -->|CAN Bus| {node_id}")
                
    return "\n".join(lines)

if __name__ == '__main__':
    # Build absolute path to src directory based on script location
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_path = os.path.join(script_dir, 'src', 'main', 'java')
    
    print(f"Scanning directory: {project_path}")
    
    if os.path.exists(project_path):
        detected_hardware = scan_frc_code(project_path)
        if detected_hardware:
            mermaid_code = generate_mermaid_diagram(detected_hardware)
            out_path = os.path.join(script_dir, "WIRING_DIAGRAM.md")
            with open(out_path, "w", encoding='utf-8') as f:
                f.write("# FRC Hardware & Wiring Map\n\n```mermaid\n")
                f.write(mermaid_code)
                f.write("\n```\n")
            print(f"SUCCESS: Found {len(detected_hardware)} device declaration(s). Written to WIRING_DIAGRAM.md!")
        else:
            print("Scan complete, but no motor/sensor instantiations were detected in your Java files yet.")
    else:
        print(f"ERROR: Directory path does not exist: {project_path}")