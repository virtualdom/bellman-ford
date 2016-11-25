# Bellman-Ford Algorithm
A small simulation of the Bellman-Ford Algorithm written using Java threads by Kevin Greenwald, Dominic Joseph, and Sumeet Vandakudari.

## Installation
Simply clone the directory and compile using `javac`.

```
git clone git@github.com:virtualdom/bellman-ford.git
cd bellman-ford
javac Driver.java algorithms/*.java
```

## Usage
To make a network topology of `n` processes, create a file named `connectivity.txt` whose first line is the format `n,x` for a number of processes `n` and the id of the root `x` (processes are numbered consecutively starting at 1) separated by a single comma. Each line after the first line represents the connectivity weight matrix as `n` rows by `n` columns. The value at row `i` and column `j` represents the weight of the directed link from process `i` to process `j` or `-1` to represent no connection (our implementation only supports bidirectional links, so a symmetric matrix is required). A sample `connectivity.txt` is already provided as the following

```
5,1
-1 -1  1  7 -1
-1 -1  3  5 -1
 1  3 -1  2 -1
 7  5  2 -1  1
-1 -1 -1  1 -1

```

To execute the Bellman-Ford with the topology specified in `connectivity.txt`, simply run the following

```
java Driver
```
