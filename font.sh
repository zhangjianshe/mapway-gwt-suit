#!/bin/bash
# 从iconfont下载的字体文件导入系统中
ZIP_FILE=$1
if [ -z "$ZIP_FILE" ]; then
  FONT_ZIP_LOCATION="/home/satway/Downloads/download.zip"
fi
export FONT_ZIP_LOCATION=$ZIP_FILE
java -jar  ./mapway-gwt-common/target/mapway-gwt-common-1.0.0.jar
echo "import font success"
exit 0