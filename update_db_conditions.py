import sqlite3

conn = sqlite3.connect("database.db")
cursor = conn.cursor()

cursor.execute("""
ALTER TABLE reports
ADD COLUMN conditions TEXT
""")

conn.commit()
conn.close()

print("conditions column added")
