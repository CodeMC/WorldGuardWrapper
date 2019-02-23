# WorldGuardWrapper
[![Build Status](https://ci.codemc.org/buildStatus/icon?job=CodeMC/WorldEditWrapper)](https://ci.codemc.org/view/Author/job/CodeMC/job/WorldEditWrapper/)
![Maven](https://img.shields.io/maven-metadata/v/https/repo.codemc.org/repository/maven-public/org/codemc/worldguardwrapper/worldguardwrapper/maven-metadata.xml.svg)
[![Javadocs](https://img.shields.io/badge/docs-javadocs-brightgreen.svg)](https://ci.codemc.org/view/Author/job/CodeMC/job/WorldEditWrapper/javadoc/)
[![Discord](https://img.shields.io/badge/chat-discord-blue.svg)](https://discord.gg/cnKwdsg)

A wrapper for the WorldGuard API that allows plugins to support both v6 and v7 APIs.

## Maven dependency
How to include WorldEditWrapper into your maven project:

```xml
    <repositories>
        <repository>
            <id>codemc-repo</id>
            <url>https://repo.codemc.org/repository/maven-public/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.codemc.worldguardwrapper</groupId>
            <artifactId>worldguardwrapper</artifactId>
            <version>1.1.4-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

Remember to include/relocate the library into your final jar, example:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <relocations>
                        <relocation>
                            <pattern>org.codemc.worldguardwrapper</pattern>
                            <shadedPattern>YOUR.PLUGIN.PACKAGE.libs.worldguardwrapper</shadedPattern>
                        </relocation>
                    </relocations>
                </configuration>
            </plugin>
        </plugins>
    </build>
```
