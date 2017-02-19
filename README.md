# Groovy - code snippets while learning the Groovy Language...

[Join Tables in Grails](#JoinTable)  

[SSL and HTTPS](#HTTPS)

[Chatty Websockets](#websockets)

[Vert-2](#Vertx)

[DZone article on Java 8 Streams](#DZone.article.on.Java.8.Streams)



<a name="JoinTable"/>
### Join Tables in Grails  

Here's a way to [create a join table between two domains but with added state](has.many.join.table.name).
Grails/Gorm doesn't naturally let you do this - convention over configuration or something - but it's not
that tricky to extend.



<a name="HTTPS"/>
### SSL and HTTPS 

I wanted a simple way to ping a server over [HTTPS](SSLChecker) and see if the server could be trusted.


<a name="websockets"/>
### Chatty Websockets 

I wanted to play around with [websockets](chatty)  so this is the start of a very simple websocket app using the [Spark micro framework for Java 8.](http://sparkjava.com/)


<a name="Vertx"/>
### Vertx-2

I hit this [Vertx-2](http://vertx.io/vertx2/docs.html) problem when trying to [send a message over the event bus from
inside a worker verticle](using.actor.inside.vertx2.worker.verticle) some time back.


<a name="DZone.article.on.Java.8.Streams"/>
### DZone article on Java 8 Streams

After reading a good [DZone article on how to use Java 8 Streams](https://dzone.com/articles/a-java-8-streams-cookbook?edition=268944&utm_source=Daily%20Digest&utm_medium=email&utm_campaign=dd%202017-02-14), I wanted to see if I could [translate into Groovy.](Java.8.streams.DZone.article)
