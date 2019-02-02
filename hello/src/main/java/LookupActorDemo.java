package com.alex.akka.demo;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;


class TargetActorf extends UntypedActor{
    @Override
    public void onReceive(Object message) throws Throwable {
        System.out.println("target received : "+message);
    }
}

class LookupActor extends UntypedActor{
    private ActorRef target =null;
    //编写构造块每实例化一次就会运行一次
    {
        target = getContext().actorOf(Props.create(TargetActorf.class),"targetActor");
    }
    @Override
    public void onReceive(Object message) throws Exception{
        if(message instanceof String){
            if("find".equals(message)){
                ActorSelection as =getContext().actorSelection("targetActor");
                as.tell(new Identify("A001"),getSelf());
            }
        }
        else if (message instanceof ActorIdentity){
                ActorIdentity ai =(ActorIdentity) message;
                if(ai.correlationId().equals("A001")){
                    ActorRef ref =ai.getRef();
                    if(ref!=null){
                        System.out.println("ActorIdentity: "+ai.correlationId()+" "+ref);
                        ref.tell("hello i am target",getSelf());
                    }
                }
                else{
                    unhandled(message);
                }
            }
        }
    }


public class LookupActorDemo extends UntypedActor{
    private LoggingAdapter log = Logging.getLogger(this.getContext().system(),this);
    public static void main(String args[]){
        ActorSystem system = ActorSystem.create("sys");
        ActorRef sender= system.actorOf(Props.create(LookupActorDemo.class),"sender");
        ActorRef lookup= system.actorOf(Props.create(LookupActor.class),"lookup");
        lookup.tell("find",sender);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof String){
            log.info(message.toString());
        }else{
            unhandled(message);
        }
    }
}