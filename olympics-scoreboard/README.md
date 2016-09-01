# 2016 Olympics Medals Scoreboard

Simple example for client/server applications in Java. The server provides information about the scoreboard of all countries and by country.

First, compile the code with the following commands:

```
javac Server.java
javac Client.java
```

### Server

To run the server, open a terminal window and enter the following:

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

Now, in another terminal window, run the following:

```
java Client localhost X
```

### Disclamer

All of the code and comments are in English but the data is in Brazilian Portuguese.
