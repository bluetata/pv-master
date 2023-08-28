#!/bin/bash

# 检查是否设置了JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
    echo "Error: JAVA_HOME is not set."
    exit 1
fi

# 检查是否能找到javac命令
if ! command -v javac &> /dev/null; then
    echo "Error: javac command not found."
    exit 1
fi

# 编译Java文件
"$JAVA_HOME/bin/javac" PVMaster.java

# 检查编译是否成功
if [ $? -eq 0 ]; then
    echo "Java文件编译成功"
    # 执行Java程序并将输出保存到output.log文件，同时将Java程序放在后台运行
    nohup "$JAVA_HOME/bin/java" PVMaster > ./output.log 2>&1 & echo $! > pid.log
    echo "Java程序已在后台运行，输出保存到output.log"
else
    echo "Java文件编译失败"
fi

