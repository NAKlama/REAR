#!/usr/bin/python3

import socketserver
import threading

STORE_DIR = "/home/examScreenshot/"
PORT_NUM  = 31338


class Connection:
  def __init__(self, socket):
    self.s          = socket
    self.buffer     = bytearray(1025)
    self.bufferData = 0

  def sendStr(self, s):
    self.s.sendall(bytes(s, 'utf-8'))

  def recvLine(self):
    out = ""
    gotName = False
    while(b'\n' not in self.buffer):
      bBuffer = bytearray(1025)
      count = self.s.recv_into(bBuffer, 1024)
      # print("RAW>", bBuffer)
      # print("count>", count)
      if count > 0:
        for i in range(0, count):
          if not gotName:
            # print("bBuffer[{}] = {}".format(i, bBuffer[i]))
            if bBuffer[i] == 10: # 10 = \n
              # print("Buffer>", self.buffer)
              out = str(self.buffer[:i], 'utf-8')
              # print("Out>", out)
              gotName = True
              self.buffer = bytearray(count - (i+1))
            else:
              self.buffer[i] = bBuffer[i]
          else:
            self.buffer[self.bufferData] = bBuffer[i]
            self.bufferData += 1
    return out

  def storeAll(self, filename):
    total = self.bufferData
    with open(filename, 'wb') as f:
      f.write(self.buffer)
      if self.bufferData > 0:
        f.write(self.buffer[:self.bufferData])
      count = -1
      while count != 0:
        bBuffer = bytearray(1025)
        count = self.s.recv_into(bBuffer, 1024)
        total += count
        # print("count>", count)
        if count > 0:
          f.write(bBuffer[:count])
    print("size =", total)


class ClientThread(socketserver.BaseRequestHandler):
  def handle(self):
    print("Connected to {}".format(self.client_address[0]))
    c = Connection(self.request)
    # c.sendStr("Connected\n")
    filename = c.recvLine()
    print("Writing to file {}".format(filename))
    c.storeAll(STORE_DIR + filename)
    print("Done")
    self.request.close()

print("Started server")
#server = socketserver.TCPServer(("127.0.0.1", PORT_NUM), ClientThread)
server = socketserver.TCPServer(("0.0.0.0", PORT_NUM), ClientThread)
server.serve_forever()

