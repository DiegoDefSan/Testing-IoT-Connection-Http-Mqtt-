##Libreria para configurar la conexión WiFi del dispositivo
import network
##Libreria para gestionar la conexión al servidor MQTT.
from umqtt.simple import MQTTClient
##Libreria para para manejar datos en formato JSON
import ujson 
##Libreria para controlar hardware del ESP32 (pines, PWM, etc.).
import machine
from machine import Pin, PWM
##Libreria para manejar hilos, permitiendo que el código escuche mensajes MQTT mientras realiza otras tareas.
import _thread
##Libreria para para manejar retardos y sincronización de tiempo
import time
##Libreria para leer datos del sensor de temperatura y humedad DHT22.
import dht

# Configuro los leds
coldLed = Pin(13, machine.Pin.OUT)
normalLed = Pin(12, machine.Pin.OUT)
warmLed = Pin(14, machine.Pin.OUT)
control = Pin(27, machine.Pin.OUT)

servo_pin = Pin(18)
servo = machine.PWM(servo_pin, freq=50)
servo.duty(30)

DEVICE_CODE = "f9027c57-4d37-4f9a-bc38-0e20209edb22"
WEATHER_ANALYSIS_TOPIC = b'hidrobots/command/weather_analysis/request'
IRRIGATION_TOPIC = b'hidrobots/command/irrigation/request'

# Apgo todos los led turn off/ enciendo solo el LED que pase como parametro
def turnOnLed(led):
  coldLed.off()
  normalLed.off()
  warmLed.off()
  control.off()
  led.on()

def handleWeatherAnalysis(msg):
  try:
    message = ujson.dumps({
        "deviceCode": DEVICE_CODE,
        "temperature": sensor.temperature(),
        "humidity": sensor.humidity()
    })
    
    client.publish("hidrobots/weather_analysis/responses", message)

    print("Data sent")
  except Exception as e:
    print("Error:", e)

def handleIrrigation(msg):
  if (msg.get("activateIrrigation")):
    servo.duty(90)
  else:
    servo.duty(30)

# callback for the messages from mqtt broker
def on_mqtt_message(topic, msg):
  print("Incoming message:", msg)
  msg = ujson.loads(msg)
  deviceCodeRequested = msg.get("deviceCode")
  
  if deviceCodeRequested!=DEVICE_CODE: 
    return

  if topic == WEATHER_ANALYSIS_TOPIC:
    handleWeatherAnalysis(msg)
  elif topic == IRRIGATION_TOPIC:
    handleIrrigation(msg)

print("Connecting to WiFi...", end="")
wifi = network.WLAN(network.STA_IF)
wifi.active(True)
wifi.connect("Wokwi-GUEST", "")
while not wifi.isconnected():
  time.sleep(0.5)
  print(".", end="")
print("Done")

print("Connecting to MQTT...")
client = MQTTClient("","mqtt-dashboard.com")
client.set_callback(on_mqtt_message)
client.connect()
client.subscribe(WEATHER_ANALYSIS_TOPIC)
client.subscribe(IRRIGATION_TOPIC)
print("ESP32 Connected!")

def wait_message_from_mqtt(name):
  while True:
    client.wait_msg() # enabling the listening mode for the messages..
_thread.start_new_thread(wait_message_from_mqtt, (1,))
 
sensor = dht.DHT22(Pin(15))
lastMessage_t = None
lastMessage_h = None
while True:
  sensor.measure() 
  message = ujson.dumps({
    "temp": sensor.temperature(),
    "humidity": sensor.humidity(),
  })
  if (lastMessage_t != sensor.temperature() or lastMessage_h != sensor.humidity()):
    lastMessage_t = sensor.temperature()
    lastMessage_h = sensor.humidity()
    client.publish("topics/diego-defsan-receive", message)
    print(message)
    if(sensor.temperature() > 41):
      turnOnLed(warmLed)
    elif(sensor.temperature() > 15 and sensor.temperature() < 40):
      turnOnLed(normalLed)
    elif(sensor.temperature() < 15):
      turnOnLed(coldLed)
  time.sleep(1)
