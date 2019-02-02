package com.alex.akka.demo;

import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import akka.event.Logging;

/**
 * 创建和使用Actor
 */
public class UntypedActorDemo {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(ActorDemo.class), "actorDemo");
        
        for (int i = 0; i < 10; i++) {
        	actorRef.tell("Hello Akka", ActorRef.noSender());
        }
    }
}

class ActorDemo extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(this.getContext().system(), this);

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            log.info(message.toString());
        } else {
            unhandled(message);
        }
    }
}