Tutorial instructions
---------------------

Write test cases for all the "production" code. Suggested order:
1) Test staticmocking ServiceRegistrator
2) Test partialmocking ProviderServiceImpl
3) Test domainmocking SampleServiceImpl


Command line: 
* mvn test -P tutorial

Eclipse users: 
* mvn eclipse:eclipse
* Manaully add the folder src/tutorial/java to the build path


This tutorial contains 3 different source folders:
main     - the "production" code which should be tested
solution - test cases that tests all the "production" code
tutorial - skeleton test cases which you should use to create your test cases


Notice that the sub package "withoutpowermock" contains refactored "production" 
code and traditional test cases that doesn't use PowerMock.


Big notice: The code here is only intended as a tutorial for how to use various
PowerMock techniques and not a recommended way of coding! 
