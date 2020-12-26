# javgent [![Build](https://img.shields.io/github/workflow/status/BaseMC/javgent/Master%20CI)](https://github.com/BaseMC/javgent/actions?query=workflow%3A%22Master+CI%22) [![Latest Version](https://img.shields.io/github/v/release/BaseMC/javgent)](https://github.com/BaseMC/javgent/releases)
Deobfuscates jars (.class-files) with mappings 

Mainly written as utility for [Aves](https://github.com/BaseMC/Aves) → Java-Aves-Agent

Uses [ASM](https://asm.ow2.io/) to make obfuscated java files readable (at least it tries it's best :innocent:).

### [Download](https://github.com/BaseMC/javgent/releases)
#### Requirements
* Java 11+ <br/>Download it via [AdoptOpenJDK](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot) (just follow the instructions)

### [Usage](docs/Usage.md)
  
### [Developing](docs/Developing.md) [![Build Develop](https://img.shields.io/github/workflow/status/BaseMC/javgent/Check%20Build/develop?label=build%20develop)](https://github.com/BaseMC/javgent/actions?query=workflow%3A%22Check+Build%22+branch%3Adevelop)

### [Building](docs/Building.md)

### Dependencies and Licenses
* [LICENSE](LICENSE) of the source code itself
* Dependencies and licenses of all nested libraries
  * View [online](https://basemc.github.io/javgent/dependencies/)
  * Checkout the source code and run ``mvn project-info-reports:dependencies``
