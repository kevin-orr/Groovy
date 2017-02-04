# Groovy

##### A small collection of various Groovy code snippets while learning the Groovy Language...
[Join Tables in Grails](#JoinTable)  
[SSL and HTTPS](#HTTPS)  
[Vert-2](#Vertx)



<a name="JoinTable"/>
### Join Tables in Grails  

Here's a way to [create a join table between two domains but with added state](has.many.join.table.name).
Grails/Gorm doesn't naturally let you do this - convention over configuration or something - but it's not
that tricky to extend.



<a name="HTTPS"/>
### SSL and HTTPS 

I wanted a simple way to ping a server over HTTPS and see if the server could be trusted.


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

