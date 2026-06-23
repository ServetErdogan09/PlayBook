import shutil
import os
import subprocess

src_path = "/Users/eyyuperdogan/.gemini/antigravity-ide/brain/cdae679f-62ee-4011-a5bb-3148006872ef/playbook_icon_1782243005911.png"
dest_base = "/Users/eyyuperdogan/AndroidStudioProjects/PlayBook/app/src/main/res"

sizes = {
    "xxxhdpi": 192,
    "xxhdpi": 144,
    "xhdpi": 96,
    "hdpi": 72,
    "mdpi": 48
}

for dpi, size in sizes.items():
    folder = os.path.join(dest_base, f"mipmap-{dpi}")
    os.makedirs(folder, exist_ok=True)
    out_file = os.path.join(folder, "ic_launcher.png")
    out_round = os.path.join(folder, "ic_launcher_round.png")
    
    # Resize using sips
    subprocess.run(["sips", "-z", str(size), str(size), src_path, "--out", out_file], check=True)
    
    # Copy for round icon
    shutil.copy(out_file, out_round)
    print(f"Generated {dpi} icons")

print("Tüm ikonlar kopyalandı ✅")
