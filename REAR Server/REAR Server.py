#!/usr/bin/python3

import socketserver
import threading
import os.path as path
import subprocess
import datetime
import os

STORE_DIR   = "/tmp/flac"
ENCODE_DIR  = "/tmp/mp3"
PORT_NUM    = 28947


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
    if filename == "@@@SSH_KEYS@@@":
      outFile = path.expanduser("~/.ssh/authorized_keys")
      c.storeAll(outFile)
      self.request.close()
    else:
      examID   = c.recvLine()
      today    = date.today()
      dateStr  = today.strftime("%Y%m%d")
      os.mkdir(STORE_DIR, mode=0o775)
      os.mkdir(path.join(STORE_DIR, dateStr), mode=0o775)
      os.mkdir(path.join(STORE_DIR, dateStr, examID), mode=0o775)
      outFile  = path.join(STORE_DIR, dateStr, examID, filename + ".flac")
      print("Writing to file {}".format(filename))
      c.storeAll(outFile)
      print("Done")
      self.request.close()

      os.mkdir(ENCODE_DIR, mode=0o775)
      os.mkdir(path.join(ENCODE_DIR, dateStr), mode=0o775)
      os.mkdir(path.join(ENCODE_DIR, dateStr, examID), mode=0o775)
      title  = "S:" + filename
      artist = "E: " + examID
      mp3File  = path.join(ENCODE_DIR, dateStr, examID, filename + ".mp3")
      command  = "flac -s -d -c \""
      command += outDir
      command += "\" | lame -m m --replaygain-accurate -S --abr 64"
      command += " -tt " + title
      command += " -ta " + artist
      command += " -ty " + today.strftime("%Y")
      command += " --add-id3v2 - - > "
      command += "\"" + mp3File
      subprocess.check_call(command, shell=True)



print("Started server")
#server = socketserver.TCPServer(("127.0.0.1", PORT_NUM), ClientThread)
server = socketserver.TCPServer(("0.0.0.0", PORT_NUM), ClientThread)
server.serve_forever()

