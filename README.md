# Groovy

#### A small collection of various Groovy code snippets while learning the Groovy Language...
[Join Tables in Grails](#JoinTable)  

[SSL and HTTPS](#HTTPS)

[Chatty - playing with websockets using the Spark Java 8 micro framework](#websockets)

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
### Chatty - websockets 

I wanted to play around with using websockets so this is the start of a very simple websocket app using the [Spark micro framework for Java 8.](http://sparkjava.com/)


<a name="Vertx"/>
### Vertx-2

I hit this [Vertx-2](http://vertx.io/vertx2/docs.html) problem when trying to [send a message over the event bus from
inside a worker verticle](using.actor.inside.vertx2.worker.verticle) some time back.

I had a requirement where I'd listen for a message using a worker verticle, do some processing when message came in but
then have to wait for a response from another service registerd on the event bus; then once I received that message, do
some stuff and finally continue on with the rest of the worker verticle processing.

It turned out, that while inside the worker verticle (and so off the main event loop) that I was only able to fire a
message to other verticles that made up the vertx module. This seemed very strange to me but I suppose the body of the
verticle essentially represents the response to a message sent to that verticle and so probably makes some kind of
sense (did I mention I'm not a vertx expert?).
Anyway, to get over this bump (or as an guy I knew used to say, 'frog on a log') as quickly as possible I decided to
use [GPars Actor framework](http://www.gpars.org/webapp/guide/index.html#_user_guide_to_actors).

I did look at [RX.Java](https://github.com/ReactiveX/RxJava) but for what I wanted to do it seemed overkill.

So using this "_technique_" I was able to make the blocking call to another service (other than those known to the vertx mod)
registered on the event bus. There's got to be better ways of doing this and I think [Vertx3](http://vertx.io) has a way
around this.

<a name="DZone.article.on.Java.8.Streams"/>
### DZone article on Java 8 Streams

After reading a good [DZone article on how to use Java 8 Streams](https://dzone.com/articles/a-java-8-streams-cookbook?edition=268944&utm_source=Daily%20Digest&utm_medium=email&utm_campaign=dd%202017-02-14), I wanted to see if I could [translate into Groovy.](Java.8.streams.DZone.article)
