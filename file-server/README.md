# File Server

Simple example for client/server applications in Java. A client requests a file from a server and the server sends it using sockets.

## Atention

You should separate the server code and client code in two different directories. Preferably, try and put some files on the server folder to test the application.

### Server

First, compile the code with the following command:

```
javac Server.java
```

Then, to run the server enter the following:

```
java Server
```

The server will respond with this message:

```
Server up and running!
Waiting for clients at port X
```

Where X is the number of the port used by the server. This will be used when running the Client code.

### Client

Open another terminal window and compile the code with the following command:

```
javac Client.java
```

Then, run the following:

```
java Client localhost X
```
