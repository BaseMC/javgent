## Build the project
If you don't want to use the [official releases](https://github.com/BaseMC/javgent/releases) you can also build the project by yourself.

### Requirements
<i>Recommend:</i> use an IDE (e.g. IDEA or Eclipse) that already contains the following requirements
* Java 21 JDK (see [Requirements](https://github.com/BaseMC/javgent#requirements))
* latest Maven (3+) 
  * [download](https://maven.apache.org/download.cgi)
  * [install it](https://maven.apache.org/install.html)

### Build an executable jar
* Open a new commandline / shell in the repository-root
* ``mvn clean install``
  * executable (standalone; with all dependencies) jar should be at ``target/javgent-standalone.jar``
