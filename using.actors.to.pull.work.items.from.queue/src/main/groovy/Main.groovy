import actors.Finished
import actors.Pusher

import java.lang.Runtime
/**
 * Created by kevinorr on 01/05/2017.
 */
class Main {

	static def pusher

    static def stuff = (1..1024).flatten().join('')


    static void main(String...args) {

        pusher = new Pusher(maxWorkers: 10, batchSize:333)
        
        Runtime.getRuntime().addShutdownHook( {

            //some cleaning up code...
            if (pusher && pusher.isActive())  pusher.stop()
        })

        pusher.start()

        (1..1000000).each { number ->
            pusher.send stuff
        }

		(1..1000000).each { number ->
            pusher.send stuff
        }

        pusher.send new Finished()
        pusher.join()
    }
}