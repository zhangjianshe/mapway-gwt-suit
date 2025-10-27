#!/bin/bash
##  deploy to https://repo.cangling.cn
##  you need define cangling repo server
#       <server>
#          <id>cangling</id>
#          <username>cangling</username>
#          <password>Cangling2025*</password>
#       </server>
#    <mirror>
#      <id>internal-repository</id>
#      <name>Maven Repository Manager running on repo.cangling.cn</name>
#      <url>https://repo.cangling.cn/repository/maven-public/</url>
#      <mirrorOf>*</mirrorOf>
#    </mirror>
mvn  clean compile package install deploy -P cangling