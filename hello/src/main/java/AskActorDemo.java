package com.alex.akka.demo;

import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import akka.event.Logging;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import scala.concurrent.Future;

public class AskActorDemo {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(Props.create(AskActor.class), "ask_actor");

        Timeout timeout = new Timeout(Duration.create(2, "seconds"));
        Future<Object> f = Patterns.ask(actorRef, "Akka ask", timeout);
        System.out.println("asking...");
        f.onSuccess(new OnSuccess<Object>(){
            @Override
            public void onSuccess(Object result){
                System.out.println("received message:"+result);
            }

        }, system.dispatcher());
        
        System.out.println("continue..");
    }
}

class AskActor extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(this.getContext().system(), this);

    @Override
    public void onReceive(Object msg) throws Exception{
        System.out.println("Sender is "+getSender());
        getSender().tell("hello "+msg,getSelf());
    }
}