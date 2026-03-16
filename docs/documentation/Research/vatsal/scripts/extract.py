import fitz
import os
import glob

def extract_pdf_text(filepath, out_dir):
    try:
        doc = fitz.open(filepath)
        text = ""
        for page in doc:
            text += page.get_text()
        
        base_name = os.path.basename(filepath)
        out_path = os.path.join(out_dir, base_name + ".txt")
        with open(out_path, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"Extracted: {base_name}")
    except Exception as e:
        print(f"Error extracting {filepath}: {e}")

out_dir = r"d:\vatoo\Downloads\Keyboard\Research Papers\extracted_texts"
os.makedirs(out_dir, exist_ok=True)

# Extract specific papers
papers_to_extract = [
    r"C:\Users\vatoo\.gemini\antigravity\brain\dd54bfdc-b4e2-47d2-956c-9c268390ad66\.tempmediaStorage\9ca338b81868c923.pdf", # orbiTouch_research_study
    r"d:\vatoo\Downloads\Keyboard\Research Papers\orbiTouch_whitepaper.pdf",
    r"d:\vatoo\Downloads\Keyboard\Research Papers\research_study.pdf",
    r"d:\vatoo\Downloads\Keyboard\Research Papers\kirschenbaum-et-al-1986-performance-of-disabled-persons-on-a-chordic-keyboard.pdf",
    r"d:\vatoo\Downloads\Keyboard\Research Papers\norman-fisher-1982-why-alphabetic-keyboards-are-not-easy-to-use-keyboard-layout-doesn-t-much-matter.pdf",
    r"d:\vatoo\Downloads\Keyboard\Research Papers\The input efficiency of chord keyboards.pdf",
    r"d:\vatoo\Downloads\Keyboard\Research Papers\Typing_with_a_two-hand_chord_keyboard_will_the_QWERTY_become_obsolete.pdf",
    r"d:\vatoo\Downloads\Keyboard\Research Papers\Design and Implementation of Chorded on screen Keyboards.pdf"
]

for p in papers_to_extract:
    extract_pdf_text(p, out_dir)

print("Done extracting.")
