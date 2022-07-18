#!/bin/bash

gnome-terminal --window-with-profile=noclose -- bash start-cyclicbarrier &
gnome-terminal --window-with-profile=noclose -- bash start-gregistry &
sleep 3

gnome-terminal --window-with-profile=noclose -- java -ea -cp "jars/*:bin" -Djava.security.manager \
     -Djava.security.policy=dcvm.policy \
     cvm.distributed.CVM1 jvm1 config.xml &

gnome-terminal --window-with-profile=noclose -- java -ea -cp "jars/*:bin" -Djava.security.manager \
    -Djava.security.policy=dcvm.policy \
    cvm.distributed.CVM4 jvm4 config.xml &
    
gnome-terminal --window-with-profile=noclose -- java -ea -cp "jars/*:bin" -Djava.security.manager \
    -Djava.security.policy=dcvm.policy \
    cvm.distributed.CVM2 jvm2 config.xml &

gnome-terminal --window-with-profile=noclose -- java -ea -cp "jars/*:bin" -Djava.security.manager \
     -Djava.security.policy=dcvm.policy \
     cvm.distributed.CVM3 jvm3 config.xml &



