import speech_recognition as sr

r = sr.Recognizer()

audio = "uploads\identify.wav"

with sr.AudioFile(audio) as source:
    #print('Say Something!')
    audio = r.record(source)
    #print('Done!')

try:
    text=r.recognize_google(audio)
    print(text)
    
except Exception as e:
    print(e)
