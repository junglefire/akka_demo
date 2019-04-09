package com.alex.akka.demo;

import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Procedure;

// 2.7 Actor行为切换
class BecomeActor extends UntypedActor
{
    Procedure<Object> procedure = new Procedure<Object>() {
        public void apply(Object message) throws Exception {
            System.out.println("become: " + message);
        }
    };

    @Override
    public void onReceive(Object message) throws Exception {
        System.out.println("Receive Message: " + message);

        getContext().become(procedure);
        System.out.println("---------------------------------------");
    }
}

public class BecomeActorDemo {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(BecomeActor.class), "becomeActor");
        ref.tell("hello",ActorRef.noSender());
        ref.tell("hi",ActorRef.noSender());
        ref.tell("hi",ActorRef.noSender());
        ref.tell("hi",ActorRef.noSender());
        system.terminate();
    }
}