/**
 * Created by kevinorr on 18/02/2017.
 */


import com.google.gson.Gson
import groovy.util.logging.Slf4j
import groovyx.gpars.GParsPool
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket

import java.util.concurrent.ConcurrentLinkedQueue

import static spark.Spark.get
import static spark.Spark.webSocket;

@Slf4j
@WebSocket
class Chatty {

    static Gson gson = new Gson()

    static void main(String...args) {

        /** set up the websocket stuff */
        webSocket("/chatty", Chatty.class);

        /** let's create a very simple RESTful endpoints to manage the app */

        /** endpoint for admin to push something to everyone that's listening */
        get('/chatty/admin', { request, response ->
            def message = request.queryParams("push");
            broadcast(message)

            response.status(200)
            response.type('text')
            response.body("Pushed $message to clients...")
            'done'
        })
    }

    // lets store all those clients that are listening - mayeb we want to broadcast a message or something
    // if we cluster then we should move to a distributed cache
    static final sessions = new ConcurrentLinkedQueue<>()

    /** push a message out to everyone listening */
    static void broadcast(msg) {
        GParsPool.withPool {
            sessions?.eachParallel { session ->
                session.getRemote().sendString(msg)
            }
        }
    }

    @OnWebSocketConnect
    void connected(Session session) {
        sessions.add(session)
    }

    @OnWebSocketClose
    void closed(Session session, int statusCode, String reason) {
        sessions.remove(session)
    }

    @OnWebSocketMessage
    void message(Session session, String message) throws IOException {
        log.info("\n\nGot: $message")
        def what = Base64.encoder.encode(message.bytes)
        session.getRemote().sendString(new String(what)) 
    }

}
