# coding=utf-8

import os
import shutil
import struct
import sys

SUFFIX = '.apk'
PATH_CHANNELS = './channels.txt'
FOLDER_RELEASE = './release'
ZIP_SHORT = 2
SIGN = 'chris'

def checkFile(filePath):
    if (not os.path.exists(filePath)):
        return [False, 'File is NOT exists!!!']

    if (not os.path.isfile(filePath)):
        return [False, 'NOT a file!!!']

    if (not os.path.splitext(filePath)[1] == SUFFIX):
        return [False, 'NOT a apk!!!']

    return [True, 'Valid']


def copyFile(filePath):
    filename = os.path.basename(filePath)
    split = os.path.splitext(filename)

    os.mkdir(FOLDER_RELEASE)

    with open(PATH_CHANNELS, 'r') as f:
        contents = f.read()
    lines = contents.split('\n')

    for channel in lines:
        copyFilename = split[0] + '_' + channel + split[1]
        channelFile = FOLDER_RELEASE + '/%s' % copyFilename
        shutil.copy(filePath, channelFile)

        index = os.stat(channelFile).st_size
        index -= ZIP_SHORT

        with open(channelFile, 'r+b') as f:
            f.seek(index)
            f.write(struct.pack('<H', len(channel) + ZIP_SHORT + len(SIGN)))
            f.write(bytes(channel, encoding="utf8"))
            f.write(struct.pack('<H', len(channel)))
            f.write(bytes(SIGN, encoding="utf8"))


if __name__ == '__main__':
    try:
        filePath = sys.argv[1]
    except:
        print('Please input filepath.')
        exit()

    result = checkFile(filePath)
    if (result[0]):
        copyFile(filePath)
    else:
        print(result[1])
