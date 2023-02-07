# MovieTicketBookingSystem
#Assignment 1 (Java RMI)

#Assignment 2 (CORBA)
1. Generate Stubs and Skeleton. (Go to src/)
  ```idlj –fall MovieTicket.idl```
  
3. Generate a JAR file from the compiled stub and skeleton. (Go to src/)
  ```jar cvf movieticketapp.jar MovieTicketApp/*.class```
  
2. Compile all java classes (Go to src/)
  ```javac Client/*.java Server/*.java Constant/*.java Log/*.java MovieTicketApp/*.java Shared/data/*.java Shared/Database/*.java```

2. Steps to run application
   Open terminal and run: ```orbd -ORBInitialPort 1050 -ORBInitialHost localhost```
   Go to src/ then run: ```java Client.Client -ORBInitialPort 1050 -ORBInitialHost localhost```
   Go to src/ then run: ```java Server.ServerInstance -ORBInitialPort 1050 -ORBInitialHost localhost```
