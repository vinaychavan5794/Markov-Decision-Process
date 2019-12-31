# Markov-Decision-Process

Instruction to run the code:

-> The mdp_input.ini file has information about grid size(rows and columns),terminal states,rewards,discount,epsilon,wall locations and probability of a move.

-> The program reads from the ini file for getting the input such as wall locations,termial states etc.

->The agent's starting location is [0,0] (Top Leftmost location of the Grid World).

->Before compiling the program ,place the ini file at any location on your system. Once done, edit the path to the ini file in  ValueIteration.java and ModifiedPolicyIteration.java files at line number 25 and 22 respectively.

-> Include the ini4j-0.5.4.jar in the program classpath.

->Compile the java files by using command "javac ValueIteration.java","javac ModifiedPolicyIteration.java".After compiling run the program by using command "java ModifiedPolicyIteration" and "java ValueIteration".
->Another way to run the program is to put the files in the default package in Eclipse IDE and click run.