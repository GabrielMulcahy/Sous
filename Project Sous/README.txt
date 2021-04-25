Instruction for running the project:

A Sous.jar file has already been provided as compiling the project from the source code is a complex process.
A database filled with a test set of ingredients and values has also been provided.

Set the environment variable GOOGLE_APPLICATION_CREDENTIALS to the file path of the JSON file that contains the project's service account key. 
This variable only applies to your current shell session, so if you open a new session, set the variable again.
The name of the file is 'Sous-190225173323.json'
Replace [PATH] with the location of the json file.

Windows:
set GOOGLE_APPLICATION_CREDENTIALS=[PATH]

Linux or MacOS:
export GOOGLE_APPLICATION_CREDENTIALS="[PATH]"

Once the envrionment variable has been set, navigate to the directory containing Sous.jar from the terminal and enter

java -jar Sous.jar




Building a new jar file from source:

Install the Google Speech libraries by following the instructions at the following links
https://cloud.google.com/text-to-speech/docs/quickstart-client-libraries#client-libraries-install-java
https://cloud.google.com/speech-to-text/docs/quickstart-client-libraries

Note that a JSON file containing credentials for the project has already been provided, so the steps in creating this can be skipped.
A pom.xml file has also already been created defining the dependencies of the project.

To build the JAR file, first install Apache Maven
https://maven.apache.org/install.html

You will then need to follow the steps listed here
http://maven.apache.org/plugins/maven-assembly-plugin/usage.html
in order to create and run an executable JAR file using the Maven Assembly Plugin


