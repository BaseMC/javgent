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
