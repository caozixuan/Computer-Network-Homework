import socket
import sys
s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
s.connect(("127.0.01",8888))
print(s.recv(1024).decode("utf-8"))
while True:
    data = input()
    if data=='exit':
        break
    s.send(bytes(data, encoding = "utf8"))
    print(s.recv(1024).decode("utf-8"))
s.send(b'exit')
s.close()