I had a requirement once where I'd listen for a message using a worker verticle, do some processing when message came 
in but then wait for a response from another service registerd on the vertx event bus; then once I received that 
message, do some other stuff and finally continue on with the rest of the worker verticle's processing.

It turned out, that while inside the worker verticle (and so off the main event loop) that I was only able to fire 
a message to the other verticles that made up the vertx module. 
This seemed strange to me but I suppose the body of the verticle essentially represents the response to a message sent 
to that verticle and so probably makes some kind of sense (did I mention I'm not a vertx expert?). Anyway, to get over 
this bump (or as an guy I knew used to say, 'frog on a log') as quickly as possible, I decided to use GPars Actor framework.

I did look at RX.Java but for what I wanted to do it seemed overkill.

So using this "technique" I was able to make the blocking call to another service (other than those known to the vertx mod) 
registered on the event bus. There's got to be better ways of doing this and I think Vertx3 has a way around this.
