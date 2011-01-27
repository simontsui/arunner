m2repo=~/.m2/repository

classpath=.
classpath=${classpath}:${m2repo}/junit/junit/4.8.1/junit-4.8.1.jar
classpath=${classpath}:${m2repo}/sf/arunner/arunner/0.0.1-SNAPSHOT/arunner-0.0.1-SNAPSHOT.jar
classpath=${classpath}:${m2repo}/org/aspectj/aspectjrt/1.6.10/aspectjrt-1.6.10.jar
classpath=${classpath}:${m2repo}/org/aspectj/aspectjweaver/1.6.10/aspectjweaver-1.6.10.jar
classpath=${classpath}:${m2repo}/org/aspectj/aspectjtools/1.6.10/aspectjtools-1.6.10.jar

echo "# claspath=${classpath}"

java -cp ${classpath} sf.arunner.ARunnerCLI $*
