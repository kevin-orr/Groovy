# Chatty

Prior to this app I'd never played around with or websockets.
The classic websocket 'hello world" example seems to be an online chat app. 
I wanted to play around with the websockets and had noticed that [Spark](http://sparkjava.com/) 
(which is a nice micro framework targeted for Java 8) had built in websocket support.

So I decided to start off by creating a very simple websocket server (thanks Spark) and a very 
simple Javascript client.
Spark makes it so simple to add routes to your app and so I'm starting to add an "admin" RESTful
api which, for example, can be used to push notifications to those clients that are listening.

This is a very simple app which if clustered would need to be re-architected perhaps to use a distributed
cache to hold current listening clients - but maybe we'll get there...
