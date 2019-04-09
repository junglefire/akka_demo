package com.alex.akka.demo;

import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import akka.event.Logging;
import akka.actor.*;

// 2.9 停止Actor
class WorkerActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(),this);

    @Override
    public void onReceive(Object message) throws Exception {
        log.info("Receive Message: " + message);
    }

    @Override
    public void postStop() throws Exception {
        log.info("Worker postStop");
    }
}

public class WorkerActorDemo {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");

        ActorRef ar = system.actorOf(Props.create(WorkerActor.class), "workerActor1");
        system.stop(ar);

        ActorRef ar2 = system.actorOf(Props.create(WorkerActor.class), "workerActor2");
        ar2.tell(PoisonPill.getInstance(), ActorRef.noSender());

        ActorRef ar3 = system.actorOf(Props.create(WorkerActor.class), "workerActor3");
        ar3.tell(Kill.getInstance(), ActorRef.noSender());
        system.terminate();
    }
}