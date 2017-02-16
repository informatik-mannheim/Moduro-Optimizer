# Moduro OptimizationAutomation 

This programm is used to optimize the input parameters of Moduro Compucell3D Simulations.
A CMAES-Optimizing Algorithm will optimize simulation parameters, by  running CompuCell3D and analyze the output for 
each simulation.

#### Generated content
This tool will generate a folder for each simulation which contains:
 - logfiles,
 - cc3d-executables for running CC3D-simulations
 - json data, that holds simulation parameters. 
 
##### Running CC3D Simulations based on JSON data
A python module within the Moduro-CC3D project is implemented, which is able to load the generated JSON-files instead of execute the parameters
set in the model-configurations.
It will create the simulation context and then run the cc3d simulation. In case of this programm, the runScript.bat will be
executed automatically during the optimization process.

 ##### Running simulations manually
You can also run this by passing the generated compucell3d file (*.cc3d) to the runScript.bat.
At this moment Compucell is unable to share the path of the currently executed file, if you run it in GUI Mode.
You have to use the runScript.bat, if you want to use JSON based simulations. It's way better performing, anyway.

##### Fitness for each CC3D Simulation
If the simulation is finshed for any reason (cancelled, aborted, or finished), the uroFunction, as a kind of internal function,
will calculate the output of each simulation run.

This workflow will repeat, until the Compucell3D Simulation Manager process
has been stopped manually. 


### Requirements

- Compucell 3D 3.7.4 (Other versions might also work, but it has been tested and developed under 3.7.4)
- Windows 10 (It has been tested under Windows 10 only. I'm sure it will work with Linux and MaxOS as well)
- A local copy of the Moduro-CC3D project, which can be found here 
https://github.com/informatik-mannheim/Moduro-CC3D (22.01.2017)
- Java 8

### Dependencies
(Notice: This does not handle the dependencies used within the source code.)
The simulation managers creates a json file, which will be parsed by a pythonscript named RunJsonCc3D.py
of the Project Moduro-CC3D. The project can be found here: https://github.com/informatik-mannheim/Moduro-CC3D (22.01.2017). 
 
Any changes to this script can affect the simulation manager. If you 

# How to run

### Prepare using the simulation manager
- We will not execute the gui version of compucell3d but the runScript.bat / runScript.sh. They will be located under
your default CompuCell3D installation directory.

- The Moduro Compucell3d Simulation Manager requires a working directory. It will create a directory for each simulation.
Files for each simulation will be stored under: 
```
"{my/path/to/the/wokingDir}/{MyModelName}/{Timestamp}". 
```
You will only have to create the working directory. The folders for each model and the dirs for each simulation will be
created by the Simulation Manager. 

Also Compucell3D Simulation Manager will store a log file for each run in the current simulation directory.

### Parameters
You need to pass the following parameters when starting this programm
```
-c Absolute path to the runScript.bat file
-p Absolute path to the initial parameterDump file (can also be a model json data file from the simulation manager working dir)
-w Absolute path to the cc3D working directory
-t Absolute path to the moduro-simulation-manager working dir (this has to exist and will not be created by the programm)
-s Absolute path to the Moduro-CC3D Simulation python file: "RunJsonCc3D.py"
```
### Example values for parameters
```
-c "C:\Program Files (x86)\CompuCell3D\runScript.bat"
-p "C:\Users\MYUSER\CC3DWorkspace\WORKING_SpaSdbCdiInDa_cc3d_01_14_2017_18_06_47\ParameterDump.dat"
-w "C:\Users\MYUSER\CC3DWorkspace"
-t "C:\Users\MYUSER\Documents\moduro-automation-working-dir"
-s "C:\Users\MYUSER\PycharmProjects\Moduro-CC3D\Simulation\RunJsonCc3D.py"
```

###  How to execute 
The simulation manager will be executed in the shell. 
This example shows how you can start the compiled jar using the shell.
In this case the name of the compiled jar file is
```
moduro-automation-cc3d-simulation-manager-1.0.0-SNAPSHOT-jar-with-dependencies.jar
```

####example execution command
```
java -jar moduro-automation-cc3d-simulation-manager-1.0.0-SNAPSHOT-jar-with-dependencies.jar -c "C:\Program Files (x86)\CompuCell3D\runScript.bat" -p "C:\Users\MYUSER\CC3DWorkspace\WORKING_SpaSdbCdiInDa_cc3d_01_14_2017_18_06_47\ParameterDump.dat" -w "C:\Users\MYUSER\CC3DWorkspace" -t "C:\Users\MYUSER\Documents\moduro-automation-working-dir" -s "C:\Users\MYUSER\PycharmProjects\Moduro-CC3D\Simulation\RunJsonCc3D.py" -a mock
```

  
 