import os
import re

layout_dir = r"c:\Users\suman\OneDrive\Desktop\osmiumapp-master\app\src\main\res\layout"

# Patterns to identify headings (textStyle="bold" or larger text sizes)
heading_patterns = [
    (r'(android:textStyle="bold")', r'android:fontFamily="@font/libre_baskerville_bold"'),
    (r'(android:textStyle=\'bold\')', r'android:fontFamily="@font/libre_baskerville_bold"'),
]

# Add Public Sans to text elements without fontFamily
def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original = content
    
    # Replace textStyle="bold" with Libre Baskerville Bold
    content = re.sub(r'android:textStyle="bold"', 'android:fontFamily="@font/libre_baskerville_bold"', content)
    content = re.sub(r"android:textStyle='bold'", 'android:fontFamily="@font/libre_baskerville_bold"', content)
    
    # Add Public Sans to TextViews without fontFamily (paragraphs/body text)
    # Match TextView blocks and add fontFamily if missing
    lines = content.split('\n')
    result = []
    in_textview = False
    textview_lines = []
    
    for line in lines:
        if '<TextView' in line:
            in_textview = True
            textview_lines = [line]
        elif in_textview:
            textview_lines.append(line)
            if '/>' in line or '</TextView>' in line:
                # Process complete TextView
                textview_block = '\n'.join(textview_lines)
                
                # Check if it has fontFamily or textStyle
                has_font = 'fontFamily' in textview_block
                has_serif = 'serif' in textview_block
                has_libre = 'libre_baskerville' in textview_block
                
                # If no fontFamily and not a heading, add Public Sans
                if not has_font and not has_serif and not has_libre:
                    # Find the line with textSize or textColor to insert fontFamily
                    for i, tline in enumerate(textview_lines):
                        if 'android:text' in tline and 'android:textSize' not in tline:
                            # Insert fontFamily after text attribute
                            indent = len(tline) - len(tline.lstrip())
                            textview_lines.insert(i+1, ' ' * indent + 'android:fontFamily="@font/public_sans"')
                            break
                        elif 'android:textSize' in tline:
                            indent = len(tline) - len(tline.lstrip())
                            textview_lines.insert(i+1, ' ' * indent + 'android:fontFamily="@font/public_sans"')
                            break
                
                result.extend(textview_lines)
                in_textview = False
                textview_lines = []
        else:
            result.append(line)
    
    content = '\n'.join(result)
    
    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

# Process all XML files
count = 0
for filename in os.listdir(layout_dir):
    if filename.endswith('.xml'):
        filepath = os.path.join(layout_dir, filename)
        if process_file(filepath):
            count += 1
            print(f"Updated: {filename}")

print(f"\nTotal files updated: {count}")
