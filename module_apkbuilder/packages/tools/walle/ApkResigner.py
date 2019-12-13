#!/usr/bin/python  
#-*-coding:utf-8-*-

import os
import sys
import config
import platform
import shutil

#获取脚本文件的当前路径
def curFileDir():
     #获取脚本路径
     path = sys.path[0]
     #判断为脚本文件还是py2exe编译后的文件，
     #如果是脚本文件，则返回的是脚本的目录，
     #如果是编译后的文件，则返回的是编译后的文件路径
     if os.path.isdir(path):
         return path
     elif os.path.isfile(path):
         return os.path.dirname(path)

#判断当前系统
def isWindows():
  sysstr = platform.system()
  if("Windows" in sysstr):
    return 1
  else:
    return 0

#兼容不同系统的路径分隔符
def getBackslash():
	if(isWindows() == 1):
		return "\\"
	else:
		return "/"


# 清空临时资源
def cleanTempResource():
  try:
    os.remove(zipalignedApkPath)
    os.remove(signedApkPath)
    pass
  except Exception:
    pass
 
 # 清空渠道信息
def cleanChannelsFiles():
  try:
    os.makedirs(channelsOutputFilePath)
    pass
  except Exception:
    pass

# 创建Channels输出文件夹
def createChannelsDir():
  try:
    os.makedirs(channelsOutputFilePath)
    pass
  except Exception:
    pass

    
#当前脚本文件所在目录
parentPath = curFileDir() + getBackslash()

#config
libPath = parentPath + "lib" + getBackslash()
checkAndroidV2SignaturePath = libPath + "CheckAndroidV2Signature.jar"
walleChannelWritterPath = libPath + "walle-cli-all.jar"
#文件名
protectedSourceApkName = sys.argv[1]
#文件路径
protectedSourceApkDirPath = sys.argv[2]
#输出位置
channelsOutputFilePath = sys.argv[3]
#sdk build tool 路径 必须大于25+ 例如:/Users/xieyifan/Library/Android/sdk/build-tools/26.0.2"
buildToolsPath =  sys.argv[4] + getBackslash()
#签名文件相关
keystorePath = sys.argv[5]
keyAlias = sys.argv[6]
keystorePassword = sys.argv[7]
keyPassword = sys.argv[8]
#渠道配置
channelFilePath = parentPath + sys.argv[9]

print ("\n"+"加固好的包 protectedSourceApkName:"+protectedSourceApkName +"\n")
print ("\n"+"渠道包输出位置 channelsOutputFilePath: "+channelsOutputFilePath+"\n")
print ("\n"+"buildToolsPath: "+buildToolsPath+"\n")
print ("\n"+"签名文件 keystorePath: "+keystorePath+"\n")
print ("\n"+"签名 keyAlias: "+keyAlias+"\n")
print ("\n"+"签名 keystorePassword: "+keystorePassword+"\n")
print ("\n"+"签名 keyPassword: "+keyPassword+"\n")
print ("\n"+"渠道配置文件 channelFilePath: "+channelFilePath+"\n")

protectedSourceApkPath = protectedSourceApkDirPath + getBackslash() + protectedSourceApkName


zipalignedApkPath = protectedSourceApkPath[0 : -4] + "_aligned.apk"
signedApkPath = zipalignedApkPath[0 : -4] + "_signed.apk"

# 创建Channels输出文件夹
createChannelsDir()

#清空Channels输出文件夹
cleanChannelsFiles()

#对齐
zipalignShell = buildToolsPath + "zipalign -v 4 " + protectedSourceApkPath + " " + zipalignedApkPath
os.system(zipalignShell)
#签名
signShell = buildToolsPath + "apksigner sign --ks "+ keystorePath + " --ks-key-alias " + keyAlias + " --ks-pass pass:" + keystorePassword + " --key-pass pass:" + keyPassword + " --out " + signedApkPath + " " + zipalignedApkPath
os.system(signShell)
print(signShell)

#检查V2签名是否正确
checkV2Shell = "java -jar " + checkAndroidV2SignaturePath + " " + signedApkPath;
os.system(checkV2Shell)

#写入渠道
if len(config.extraChannelFilePath) > 0:
  writeChannelShell = "java -jar " + walleChannelWritterPath + " batch2 -f " + config.extraChannelFilePath + " " + signedApkPath + " " + channelsOutputFilePath
else:
  writeChannelShell = "java -jar " + walleChannelWritterPath + " batch -f " + channelFilePath + " " + signedApkPath + " " + channelsOutputFilePath

os.system(writeChannelShell)

cleanTempResource()

print ("\n**** =============================TASK FINISHED=================================== ****\n")
print ("\n↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓   Please check channels in the path   ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓\n")
print ("\n"+channelsOutputFilePath+"\n")
print ("\n**** =============================TASK FINISHED=================================== ****\n")


