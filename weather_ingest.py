import requests
import pymysql
from datetime import datetime
import schedule
import time

# DB 연결 설정
conn = pymysql.connect(
    host='mysql',  #Docker에서 실행하기 위해서 mysql 로 변경 필요
#     host='127.0.0.1',
    user='root',
    password='12345',
    database='energy_db',
    charset='utf8mb4'
)

def fetch_and_store_weather():
    now = datetime.now().strftime('%Y%m%d%H00')  # 매 정시 기준
    url = f"https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php?tm={now}&stn=95&authKey=5UE8LfqfRPGBPC36n5Tx4A"

    response = requests.get(url)
    lines = response.text.splitlines()
    data_line = [line for line in lines if line.startswith(now)][0]
    fields = data_line.split()

    # 필드 매핑
    values = {
        'tm': datetime.strptime(fields[0], '%Y%m%d%H%M'),
        'stn': int(fields[1]),
        'wd': int(fields[2]),
        'ws': float(fields[3]),
        'gst_wd': int(fields[4]),
        'gst_ws': float(fields[5]),
        'gst_tm': fields[6],
        'pa': float(fields[7]),
        'ps': float(fields[8]),
        'pt': int(fields[9]),
        'pr': float(fields[10]),
        'ta': float(fields[11]),
        'td': float(fields[12]),
        'hm': float(fields[13]),
        'pv': float(fields[14]),
        'rn': float(fields[15]),
        'rn_day': float(fields[16]),
        'rn_jun': float(fields[17]),
        'rn_int': float(fields[18]),
        'sd_hr3': float(fields[19]),
        'sd_day': float(fields[20]),
        'sd_tot': float(fields[21]),
        'wc': int(fields[22]),
        'wp': int(fields[23]),
        'ww': fields[24],
        'ca_tot': int(fields[25]),
        'ca_mid': int(fields[26]),
        'ch_min': int(fields[27]),
        'ct': fields[28],
        'ct_top': int(fields[29]),
        'ct_mid': int(fields[30]),
        'ct_low': int(fields[31]),
        'vs': int(fields[32]),
        'ss': float(fields[33]),
        'si': float(fields[34]),
        'st_gd': int(fields[35]),
        'ts': float(fields[36]),
        'te_005': float(fields[37]),
        'te_01': float(fields[38]),
        'te_02': float(fields[39]),
        'te_03': float(fields[40]),
        'st_sea': int(fields[41]),
        'wh': float(fields[42]),
        'bf': int(fields[43]),
        'ir': int(fields[44]),
        'ix': int(fields[45]),
    }

    placeholders = ', '.join(['%s'] * len(values))
    columns = ', '.join(values.keys())

    sql = f"""
        INSERT INTO weather_observation ({columns})
        VALUES ({placeholders})
        ON DUPLICATE KEY UPDATE
        {', '.join([f"{col}=VALUES({col})" for col in values.keys() if col != 'tm' and col != 'stn'])}
    """

    with conn.cursor() as cursor:
        cursor.execute(sql, list(values.values()))
    conn.commit()
    print(f"[{now}] Inserted weather data.")

# 매시간 실행
schedule.every().hour.at(":01").do(fetch_and_store_weather)

print("🌐 Weather data ingestion started...")
while True:
    schedule.run_pending()
    time.sleep(10)