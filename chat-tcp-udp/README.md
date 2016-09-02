# Chat TCP/UDP

Client/Server implementation of a simple chat. Messages between a client and the server are sent via TCP, whereas the messages between 'two clients' are sent via UDP. We need to emphasize that in this case, there isn't such thing as a message between two clients. The one receiving the message is acting as a server, so it is also in a client/server architecture.

First, the client needs to sign-up with an email on the server. Then, the options for them are:

- List all users from server
- Request information about a user
- Send message to a user

When listing all users, a client has access to the email of everyone that is currently logged in. With that information, they can request for more information about an specific client, such as their IP and port. The next step would be sending that person a message.

### Testing

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
