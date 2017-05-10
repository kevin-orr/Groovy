package actors

import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.actor.Actor


/**
 * Created by kevinorr on 01/05/2017.
 */
class Worker extends DefaultActor {
    final Actor pusher
    final int id
    final int batchSize
    long countdown

    Worker (final Actor pusherActor, final int id, final int batchSize) {
        this.pusher = pusherActor
        this.id = id
        this.batchSize = batchSize
    }

    void afterStart() {
        println("Worker<$id> has started")

    }

    void act() {
        loop {
            pusher << new WorkerRequest(id:id)
            react { msg ->
                switch (msg) {
                    case List:
                       break
                    case Empty:
                      break
                }
            }
        }
    }
}
