# Bellman Ford Algorithm
A small simulation of the Bellman Ford Algorithm written using Java threads by Kevin Greenwald,
Dominic Joseph, and Sumeet Vandakudari.

## Installation
Simply clone the directory and compile using `javac`.
```

git clone git@github.com:virtualdom/hs-algorithm.git
cd hs-algorithm
javac hs/HSAlgorithm.java HSDriver.java
```

##Usage
To run the Bellman Ford Algorithm with `n` processes, create a file that contains The first line of the format
n,x (The file has already been created in the repository as `connectivity.txt`). The number of processes `n` and 
the id of the root `x` should be comma seperated values. And each line after the first line has the connectivity info 
as n lines(n rows) which each row containing connectivity info for one node. To use the file with our implementation 
named `connectivity.txt`, execute the following.
````

java Driver connectivity.txt 
