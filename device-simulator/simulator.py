import pika
import json
import bisect
import random
import time
from datetime import datetime

# --- CONFIGURATION CONSTANTS ---
RABBITMQ_HOST = 'localhost'
QUEUE_NAME = 'device-data-queue' # Must match 'monitoring.queue.data' in Spring

# 🛠️ EDIT THESE VALUES 🛠️
DEVICE_ID = "1"              # The ID of the device to simulate
DATE_STRING = "10-01-2026"   # The date for the data (Format: DD-MM-YYYY)

# --- CONSUMPTION LOGIC (Approved by Teacher) ---
# (Index, Consumption_kW)
KEY_POINTS = [
    (0, 0.3),    # 00:00 - Low base load
    (42, 1.5),   # 07:00 - Morning peak
    (54, 1.2),   # 09:00 - Morning drop
    (72, 0.8),   # 12:00 - Daytime lull
    (102, 1.0),  # 17:00 - People returning home
    (114, 2.2),  # 19:00 - Evening peak
    (126, 1.8),  # 21:00 - Peak drops
    (138, 0.5),  # 23:00 - Winding down
    (144, 0.3)   # 24:00 - Back to base
]

breakpoints = [x[0] for x in KEY_POINTS[1:]] # Extract indices for bisect

def get_current_consumption(index):
    # Find the interval this index falls into using bisect
    idx = bisect.bisect_right(breakpoints, index)
    base = KEY_POINTS[idx][1]
    
    # Add randomness (+/- 15%)
    noise = random.uniform(-0.15, 0.15)
    value = base + (base * noise) + 100000
    
    return max(0, round(value, 2))

def main():
    print(f"--- Starting One-Shot Simulation ---")
    print(f"Target: Device {DEVICE_ID} | Date: {DATE_STRING}")

    # 1. Setup Connection
    try:
        connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
        channel = connection.channel()
        channel.queue_declare(queue=QUEUE_NAME, durable=True)
    except Exception as e:
        print(f"❌ Error connecting to RabbitMQ at {RABBITMQ_HOST}: {e}")
        return

    # 2. Parse Date Constant
    try:
        parsed_date = datetime.strptime(DATE_STRING.strip(), "%d-%m-%Y")
    except ValueError:
        print(f"❌ Invalid date format in constant: '{DATE_STRING}'. Expected DD-MM-YYYY.")
        return

    # 3. Generate 144 points (every 10 mins for 24h)
    print("🚀 Sending data points...")
    
    for index in range(144):
        total_minutes = index * 10
        hour = total_minutes // 60
        minute = total_minutes % 60
        
        # Create timestamp for specific time of that day
        timestamp = parsed_date.replace(hour=hour, minute=minute, second=0, microsecond=0)
        
        consumption = get_current_consumption(index)
        
        # Construct JSON payload matching the approved format
        data = {
            "timestamp": timestamp.isoformat(),
            "device": {
                "id": DEVICE_ID
            },
            "consumption": consumption
        }
        
        message = json.dumps(data)
        
        channel.basic_publish(
            exchange='',
            routing_key=QUEUE_NAME,
            body=message
        )
        
        # Print progress (optional: remove time.sleep for instant execution)
        print(f"[{timestamp.time()}] Sent {consumption} kW")
        time.sleep(0.05) 

    connection.close()
    print(f"✅ Completed. 144 measurements sent for Device {DEVICE_ID}.")

if __name__ == '__main__':
    main()