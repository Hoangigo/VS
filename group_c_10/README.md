# Smart Home Simulation 
### SS22 - group 10

## Description

### #2 - UDP-Sockets

The Gateway class gets the addresses and ports for each container running an instance of the Sensor class
as arguments.
It then initializes multiple instances of the SensorHandler class, allowing to both send and receive messages.
Afterwards a Timer is started that pulls the sensor data and, if successful, prints it to the console.


### #3 - TCP-Sockets and HTTP

### #4 - RPC

### #5 - MoM

### #6 - Redundancy

## Running the simulation

1. start docker</br>
2. open a terminal</br>
3. navigate to the root of this project</br>
4. run...
~~~
./init.bat -run
~~~
...to start the simulation normally.
~~~
./init.bat -stop
~~~
...to stop the simulation.
~~~
./init.bat -test
~~~
...to run the tests.
~~~
./init.bat -reset
~~~
...to do a hard-reset.
