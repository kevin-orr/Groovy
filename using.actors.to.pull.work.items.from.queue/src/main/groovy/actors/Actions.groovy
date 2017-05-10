package actors


/**
 * Created by kevinorr on 10/05/2017.
 */
final class WorkerRequest {String id}
final class NotReady {long sleepIntervalMs}
final class Empty {long sleepIntervalMs}
final class Report {def what}
final class ShutDownWorker {String id}
final class Error {def error}
final class Finished {}
