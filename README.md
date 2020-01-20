# javgent
Deobfuscates jars (.class-files) with mappings 

Mainly written as utility for [Aves](https://github.com/BaseMC/Aves) → Java-Aves-Agent

Uses [ASM](https://asm.ow2.io/) to make obfuscated java files readable (at least it tries it's best :innocent:).

## Requirements
* Java 11+ <br/>Download it via 
  * [AdoptOpenJRE/JDK](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot) (just follow the instructions)
  * [OpenJDK](https://jdk.java.net/) (has to be manually installed; requires some experience) 
  
## Download
* https://github.com/BaseMC/javgent/releases

## Usage 
### Important Parameters

|Argument|Meaning|Example|
|--------|-------|-------|
|``-s <arg>`` <br/>``--srcfile <arg>``|Required<br/> Obfusctaed java Source File (.jar)|``-s obfuscated.jar``|
|``-o <arg>`` <br/>``--outputfile <arg>``|Required<br/> Deobfuscated (output) file (.jar)|``-o deofuscated.jar``|
|``-m <arg>`` <br/>``--mapping <arg>``|Required  (Mutually exclusive to ``patchfiledir``, but one is required)<br/> Mappingfile|``-m mappings.txt``|
|``-p <arg>`` <br/>``--patchfiledir <arg>``|Required (Mutually exclusive to ``mapping``, but one is required)<br/> Directory with PatchFiles (.json)|``-p patchfileFolder``|

For the rest call ``--help``

### Example
- MappingFile
```Shell
-s
"client.jar"
-o
"deobf_client.jar"
-m
"mappings_client.txt"
-ec
net/minecraft/gametest/
```
- patchfile directory / json files in a directory
```Shell
-s
"client.jar"
-o
"deobf_client.jar"
-p
"patchfilesdir_client"
-ec
net/minecraft/gametest/
```


## How it works
### Input
The program gets the required files:
  * the obfuscated and packed .jar
  * the mappings
    * a single mapping file - provided in proguards txt format<br/>
  Example: mapping.txt<br/>
```
# comment
test.package.abc -> cve:
    20:21:void youJustLostTheGame() -> a
    24:24:double getTime() -> b
test.def -> cvf:
    org.apache.logging.log4j.Logger LOGGER -> a
    int source -> b
    java.util.concurrent.atomic.AtomicBoolean initialized -> c
    int streamingBufferSize -> d
    31:37:int create() -> a
    22:42:void <init>(int) -> <init>
    45:61:void destroy() -> b
    64:65:void play() -> c
    int getPixelWidth() -> d
    68:71:int getState() -> j
    17:17:void <clinit>() -> <clinit>
```
  * json files in a directory<br/>
     Example: zx.json<br/>
     
```JS
{
  "Name": "abc.SoundEvent",
  "ObfName": "zx",
  "Fields": [
    {
      "Type": "abc.ResourceLocation",
      "Name": "location",
      "ObfName": "a"
    }
  ],
  "Methods": [
    {
      "Name": "<init>",
      "ObfName": "<init>",
      "ReturnType": "void",
      "Parameters": [
        {
          "Type": "abc.ResourceLocation"
        }
      ]
    },
    {
      "Name": "getLocation",
      "ObfName": "a",
      "ReturnType": "abc.ResourceLocation",
      "Parameters": []
    }
  ]
}
```
### Processing
* The jar is extracted
* The extracted/obfuscated .class(es) are visited, the obfuscated parts are replaced by deobfuscated ones and rewritten
* A new jar is packed from the fixed .class(es)

### Output
A deobfuscated jar is created

## Build
If you don't want to use the [official releases](https://github.com/BaseMC/javgent/releases) you can also build the project by yourself.

### Requirements
<i>Recommend:</i> use an IDE (e.g. IDEA or Eclipse) that already contains the following requirements
* Java 11 JDK (see [above](#requirements))
* latest Maven (3+) 
  * download → https://maven.apache.org/download.cgi 
  * install it → https://maven.apache.org/install.html

### Build an executable jar
* Open a new commandline / shell in the repository-root
* ``mvn clean install``
  * executable (standalone; with all dependencies) jar should be at ``target/javgent-standalone.jar``
  
## Tools for developing
* [IntelliJ IDEA](https://www.jetbrains.com/de-de/idea/download/)
* [SonarLint](https://www.sonarlint.org/intellij/)

## Licenses
* source code itself: [LICENSE](LICENSE)
* used libarys that must be mentioned: [LICENSES.txt](LICENSES.txt)
