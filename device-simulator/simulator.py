import pika
import json
import time
import datetime
import os
import random

# --- CONFIGURATION (from Docker environment variables) ---
RABBITMQ_HOST = os.getenv('RABBITMQ_HOST', 'localhost')
DEVICE_ID = os.getenv('DEVICE_ID', '1') # The ID of the device this simulator mimics
DATA_QUEUE = 'device-data-queue'
INTERVAL_SECONDS = 1 * 60 # 1 minute

# --- SIMULATION LOGIC ---

# Base consumption rate (kWh for 10 minutes)
BASE_LOAD_MIN = 0.1 
BASE_LOAD_MAX = 0.5

def get_consumption_multiplier():
    """Adjusts consumption based on the hour to simulate day/night cycles."""
    current_hour = datetime.datetime.now().hour
    
    # Lower consumption during night (1 AM - 7 AM)
    if 1 <= current_hour <= 7:
        return 0.5 + random.uniform(-0.1, 0.1)  # Low period
    
    # Peak consumption during evening (5 PM - 10 PM)
    elif 17 <= current_hour <= 22:
        return 1.8 + random.uniform(-0.2, 0.2)  # Peak period
    
    # Moderate consumption during day
    else:
        return 1.0 + random.uniform(-0.3, 0.3) 

def generate_measurement():
    """Generates a synthetic measurement value."""
    multiplier = get_consumption_multiplier()
    base_consumption = random.uniform(BASE_LOAD_MIN, BASE_LOAD_MAX)
    
    # measurement_value represents consumption over a 10-minute interval
    value = round(base_consumption * multiplier, 3) 
    
    # The message structure required: <timestamp, device id, measurement_value>
    timestamp = datetime.datetime.now().isoformat()
    
    message = {
        "timestamp": timestamp,
        "device_id": int(DEVICE_ID), # Ensure ID is integer type
        "measurement_value": value
    }
    
    return message

# --- RABBITMQ PUBLISHER ---

def publish_data():
    """Connects to RabbitMQ and publishes the measurement."""
    try:
        # 1. Establish connection and channel
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(host=RABBITMQ_HOST))
        channel = connection.channel()
        
        # 2. Declare the queue (will create it if it doesn't exist)
        channel.queue_declare(queue=DATA_QUEUE, durable=True)
        
        # 3. Generate and format the message
        measurement = generate_measurement()
        json_body = json.dumps(measurement)
        
        # 4. Publish the message
        channel.basic_publish(
            exchange='',  # Use default exchange for direct routing to queue
            routing_key=DATA_QUEUE,
            body=json_body
        )
        
        print(f"[{measurement['timestamp']}] 🟢 Sent to Device {DEVICE_ID}: {json_body}")
        
    except pika.exceptions.AMQPConnectionError as e:
        print(f"❌ Connection error: Could not connect to RabbitMQ at {RABBITMQ_HOST}. Retrying...")
    finally:
        if 'connection' in locals() and connection.is_open:
            connection.close()


def main():
    print(f"Starting Device Simulator for Device ID: {DEVICE_ID}. Interval: {INTERVAL_SECONDS}s.")
    while True:
        publish_data()
        time.sleep(INTERVAL_SECONDS) # Wait for 10 minutes

if __name__ == '__main__':
    # Give RabbitMQ time to start up
    time.sleep(15) 
    main()