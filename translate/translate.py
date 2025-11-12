import mysql.connector
from deep_translator import GoogleTranslator
import time

# ======== Cấu hình MySQL ========
DB_CONFIG = {
    "host": "localhost",
    "user": "root",
    "password": "Duong1997@",
    "database": "englishapp"
}

BATCH_SIZE = 50
SLEEP_BETWEEN = 1
RETRY_COUNT = 3

conn = mysql.connector.connect(**DB_CONFIG)
cursor = conn.cursor()

cursor.execute("SELECT flashcard_id, meaning_en FROM flashcards WHERE meaning_vn IS NULL")
rows = cursor.fetchall()
print(f"Tổng số flashcards cần dịch: {len(rows)}")

def translate_text(text):
    for attempt in range(RETRY_COUNT):
        try:
            return GoogleTranslator(source='en', target='vi').translate(text)
        except Exception as e:
            print(f"Lỗi khi dịch: {e}. Thử lại {attempt+1}/{RETRY_COUNT}")
            time.sleep(2)
    return None

for i, (flashcard_id, meaning_en) in enumerate(rows, 1):
    meaning_vn = translate_text(meaning_en)
    if meaning_vn:
        cursor.execute(
            "UPDATE flashcards SET meaning_vn = %s WHERE flashcard_id = %s",
            (meaning_vn, flashcard_id)
        )
        conn.commit()
        print(f"[{i}/{len(rows)}] Đã cập nhật flashcard_id={flashcard_id}")
    else:
        print(f"[{i}/{len(rows)}] Dịch thất bại flashcard_id={flashcard_id}")
    
    if i % BATCH_SIZE == 0:
        print(f"Đã xử lý {i} flashcards, nghỉ {SLEEP_BETWEEN}s...")
        time.sleep(SLEEP_BETWEEN)

cursor.close()
conn.close()
print("Hoàn tất cập nhật tất cả flashcards!")
