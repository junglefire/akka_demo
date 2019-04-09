package com.alex.akka.demo1;

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

class WatchActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    ActorRef child = null;

    @Override
    public void preStart() throws Exception {
        child = getContext().actorOf(Props.create(WorkerActor.class), "workerActor");
        getContext().watch(child);
    }

    @Override
    public void postStop() throws Exception {
        log.info("WatchActor postStop");
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof String){
            if(message.equals("stopChild")){
                getContext().stop(child);
            }
        }
        else if(message instanceof Terminated){
            Terminated t= (Terminated) message;
            log.info("监控到" + t.getActor() + "停止了");
        }
        else {
            unhandled(message);
        }
    }
}

public class WatchActorDemo {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");

        ActorRef ar = system.actorOf(Props.create(WorkerActor.class), "workerActor1");
        ar.tell("stopChild",ActorRef.noSender()); 
        system.terminate();
    }
}