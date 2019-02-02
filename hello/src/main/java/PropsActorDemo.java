package com.alex.akka.demo;

import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import akka.event.Logging;
import akka.japi.Creator;

public class PropsActorDemo {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef actorRef = system.actorOf(PropsActor.createProps(), "props_actor");
        
        for (int i = 0; i < 10; i++) {
        	actorRef.tell("Hello Akka", ActorRef.noSender());
        }
    }
}

class PropsActor extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(this.getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            log.info(message.toString());
        } else {
            // 匹配不到相应的消息类型时，推荐使用unhandled进行处理
            unhandled(message);
        }
    }


    public static Props createProps() {
        return Props.create(new Creator<PropsActor>() {
            public PropsActor create() throws Exception {
                return new PropsActor();
            }
        });
    }
}