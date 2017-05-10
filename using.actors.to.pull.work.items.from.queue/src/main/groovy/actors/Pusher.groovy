package actors

import groovyx.gpars.actor.DefaultActor

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by kevinorr on 01/05/2017.
 */
class Pusher extends DefaultActor {

    def maxWorkers
    def batchSize

    def queue = []
    def ids = []
    def work = []
    def workers = [:]

    def finshedProcessing = false


    void afterStart() {
        
    }

    void afterStop() {
        println("work size = ${work.size()} and queue size = ${queue.size()}")
    }

    void act() {
        loop {            
            react { msg ->
                switch (msg) {
                    case String:
                        queue << msg
                        break

                    case Report:
                        println("${msg.what}")
                        break

                    case Finished:
                        finshedProcessing = true
                        break

                    case Error:
                       break

                    case ShutDownWorker:
                         break

                    case WorkerRequest:
                     break
                }
            }
            canStop()
        }
    }

    def canStop() {
    }
 
    def checkWorkerThreshold() {
        
    }
}


final class WorkerRequest {String id}
final class NotReady {long sleepIntervalMs}
final class Empty {long sleepIntervalMs}
final class Report {def what}
final class ShutDownWorker {String id}
final class Error {def error}
final class Finished {}
