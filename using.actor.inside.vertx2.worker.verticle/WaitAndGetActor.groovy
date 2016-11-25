package com.korr.actors

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import groovyx.gpars.actor.DynamicDispatchActor
import groovy.util.logging.Log4j
import org.vertx.groovy.core.eventbus.Message


/**
 *
 * I came across an issue when working with worker verticles in Vertx.
 * The problem was I couldn't fire a request out to an address on the event bus other than 
 * the ones known to the vertx module - essentially only the other verticels in the module.
 * This was nuts but I suppose kind of makes sense if you think of the verticle as the response
 * to an incoming message over the bus.
 * Anyways, I decided to use GPars actor framework as it was pretty lightweight and I did
 * have to go and learn RX.Java - now that I've got time I will :-)
 * 
 * So this was my solution - create an actor on the fly(great, so I don't have to worry about threading either)
 * and make it call itself which will force it to call the event bus passing message to address, then
 * once a response comes back it's popped into a blocking queue. I can then wait for a specified timeout on the
 * the poll() for queue.
 * If I timeout I can use defaults for reponse etc. and stop the actor and forget about the response - let
 * the event bus take care of that (it'll eventually dump the request after a while)
 * 
 *
 * This is a very basic Actor that can be used inside a worker verticle to fire a message 
 * to an address other than those the verticle is aware of.
 * 
 *
 * To create the actor just pass in the following parameters to constructor, for example:
 *    def paramsObj = [
 *                eventBus: eventBus
 *                address: "this.is.an.address.,registered.on.evet.bus",
 *                request: some_object_or_map_representing_a_request_to_send_address
 *             ]
 *
 *    def response = (new WaitAndGetActor(paramsObj))
 *                      .dispatch()
 *                      .waitThenProcessReply(5000, TimeUnit.MILLISECONDS) {
 *                          ...do something with vertx response
 *                      }
 *
 *
 * The first call will be to {@link WaitAndGetActor#dispatch() WaitAndGetActor.dispatch}.
 * The actor will dispatch a request to address over the event bus (as defined in paramsObj.)
 *
 * The next call should be one of either
 * {@link WaitAndGetActor#waitThenProcessReply(long, java.util.concurrent.TimeUnit) WaitAndGetActor.waitThenProcessReply}
 * or
 * {@link WaitAndGetActor#waitThenProcessReply(long, java.util.concurrent.TimeUnit, Closure) WaitAndGetActor.waitThenProcessReply}
 *
 * depending on whether you use the raw actor or have subtyped. 
 * If the actor is a subtype then it should override the
 * {@link WaitAndGetActor#process(Message) WaitAndGetActor.process}
 *
 * Both calls block for the specified time interval passed to method, before returning with either:
 *   - null response if it has timed out waiting for response or
 *   - an object representing the processed response.
 */
@Log4j
class WaitAndGetActor extends DynamicDispatchActor {

    /** constructor param: the vertx event bus used to dispatch requests to registered listeners */
    public def eventBus
    /** constructor param: the address to dispatch the request to */
    public def address
    /** constructor param: the request to dispatch to the vertx address */
    public def request

    /**
     * response will eventually be enqueued ready for retrieval.
     * This queue will only hold zero or one response (capacity=1) at any given time
     */
    protected BlockingQueue<org.vertx.groovy.core.eventbus.Message> eventResponseQueue = new ArrayBlockingQueue<>(1)

    /**
     * dispatches the message
     * @param eventBus reference to the vertx event bus
     * @return this 
     */
    public WaitAndGetActor dispatch() {
        // if the event bus is unavailable then we'll do nothing
        if(eventBus) {
            // for DynamicDispatchActor calling silentStart() instead of start() involves less overhead and is recommneded by gpars
            silentStart().send([address: address, request: request])
        }
        this
    }
    /**
     * Retrieves and removes the head of this queue and will wait for up to the specified wait time if 
     * necessary, for an element to become available.
     * Then finally processes the response.
     * Note, if the event bus is not unavailable the timeout interval is zeroed which means we get immediate reply
     * and so will not hang around until timeout occurs (as it would if event bus is down.)
     * @param timeout the wait time before giving up, in units of <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the <tt>timeout</tt> parameter
     * @return hopefully a processed response or null
     */
    public def waitThenProcessReply(long timeout, TimeUnit unit) {
        log.info("Polling eventResponseQueue->timeout:${eventBus?timeout:0} ${unit.toString()}")
        this.stop()
        process(eventResponseQueue.poll(eventBus?timeout:0, unit))
    }

    /**
     * Retrieves and removes the head of this queue and will wait for up to the specified wait time if 
     * necessary, for an element to become available.
     * Then finally processes the response via the passed in Closure.
     * Note, if the event bus is not unavailable the timeout interval is zeroed which means we get immediate reply
     * and so will not hang around until timeout occurs (as it would if event bus is down.)
     *
     * @param timeout the wait time before giving up, in units of <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the <tt>timeout</tt> parameter
     * @param processor a Closure which performs some transformation on the response message from event bus (which could be null)
     * @return hopefully a processed response
     */
    public def waitThenProcessReply(long timeout, TimeUnit unit, Closure processResponse) {
        log.info("Polling eventResponseQueue->timeout:${eventBus?timeout:0} ${unit.toString()}")
        this.stop()
        processResponse(eventResponseQueue.poll(eventBus ? timeout : 0, unit))
    }

    /**
     * capture all requests to this actor
     */
    protected void onMessage(final Map message) {
        log.info("Firing a request to address '${message.address}'")
        // dispatch the request...
        eventBus?.send(message.address, message.request) { msg ->
            log.info("Success - received response...now stick response in eventResponseQueue ready for work...")
            eventResponseQueue << msg
        }
    }

    /**
     * Subtypes should override this if they want to call
     * {@link WaitAndGetActor#waitThenProcessReply(long, java.util.concurrent.TimeUnit) WaitAndGetActor.waitThenProcessReply}
     * to do something with the response from the event bus
     * @return hopefully something that has been transformed
     */
    protected def process(final Message response){response}
}
