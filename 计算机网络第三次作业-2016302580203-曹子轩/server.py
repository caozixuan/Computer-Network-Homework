import socket
import threading
import time
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(('127.0.1',8888))
s.listen(5)
print("waiting for connection")

def tcp_link(sock,addr):
    print("accept new conection from %s:%s"%addr)
    sock.send(b'Please enter your information:username password')
    while True:
        data = sock.recv(1024)
        time.sleep(2)
        if not data or data.decode("utf-8")=='exit':
            break
        s = data.decode("utf-8")
        ss = s.split(" ")
        username = ss[0]
        password = ss[1]
        if username=="root" and password=="123456":
            sock.send(b"login success!")
        else:
            sock.send(b"login fail!")
    sock.close()
    print('connection from %s:%s closed' % addr)

while True:
    sock,addr = s.accept()
    t = threading.Thread(target = tcp_link,args = (sock,addr))
    t.start()

