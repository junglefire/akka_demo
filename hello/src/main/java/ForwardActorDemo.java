package com.alex.akka.demo;

import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;

class TargetActor extends UntypedActor{
    @Override
    public void onReceive(Object msg) throws Exception{
        System.out.println("Target received: "+msg+ "Sender is "+getSender());
    }
}

class ForwardActor extends UntypedActor{
    private ActorRef target =getContext().actorOf(Props.create(TargetActor.class),"tagetActor");
    
    @Override
    public void onReceive(Object msg) throws Exception {
        target.forward(msg,getContext());
    }
}

public class ForwardActorDemo extends UntypedActor {

    @Override
    public void onReceive(Object msg) throws Exception {
    }

    public static void main(String args[]){
        ActorSystem system =ActorSystem .create("sys");
        ActorRef sender = system.actorOf(Props.create(ForwardActorDemo.class), "sender");
        ActorRef forward_actor = system.actorOf(Props.create(ForwardActor.class), "tagetActor");
        forward_actor.tell("hello you have got message which was forwarded", sender);
    }
}