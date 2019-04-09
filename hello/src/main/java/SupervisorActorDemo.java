package com.alex.akka.demo;

import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.LoggingAdapter;
import akka.event.Logging;
import akka.japi.Function;
import akka.actor.*;

import scala.concurrent.duration.Duration;
import scala.Option;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 2.10 监督与容错处理
 * Akka 提供了两种监督策略：分别是One-For-One策略和All-For-One策略，前者是默认策略，表示当一个Actor出现异常时只对这个Actor做处理，后者
 * 表示对所有Actor都做处理
 */

class SupervisorActor extends UntypedActor {
    private SupervisorStrategy strategy = new OneForOneStrategy(3, Duration.create("1 minute"),
            new Function<Throwable, SupervisorStrategy.Directive>() {
                public SupervisorStrategy.Directive apply(Throwable t) {
                    if (t instanceof IOException) {
                        System.out.println("===============IOException==================");
                        return SupervisorStrategy.resume();
                    } else if (t instanceof IndexOutOfBoundsException) {
                        System.out.println("===============IndexOutOfBoundsException==================");
                        return SupervisorStrategy.restart();
                    } else if (t instanceof SQLException) {
                        System.out.println("=============SQLException======================");
                        return SupervisorStrategy.stop();
                    } else {
                        System.out.println("================escalate=========================");
                        return SupervisorStrategy.escalate();
                    }

                }

            });


    @Override
    public void preStart() throws Exception {
        // 创建子Actor(受监控的子Actor)
        ActorRef workerActor = getContext().actorOf(Props.create(WorkerActor2.class), "workerActor2");
        // 监控生命周期
        getContext().watch(workerActor);
    }


    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Terminated) {
            Terminated ter = (Terminated) message;
            System.out.println(ter.getActor() + "已经终止");
        } else {
            System.out.println("stateCount=" + message);
        }
    }
}

class WorkerActor2 extends UntypedActor {
    private int stateCount = 1;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        System.out.println("Worker actor preStart");
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        System.out.println("Worker actor postStop");
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        System.out.println("worker actor preRestart begin " + this.stateCount);
        super.preRestart(reason, message);
        System.out.println("worker actor preRestart end " + this.stateCount);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        System.out.println("worker actor postRestart begin " + this.stateCount);
        super.postRestart(reason);
        System.out.println("worker actor postRestart end " + this.stateCount);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        // 模拟计算任务
        this.stateCount++;
        if (message instanceof Exception) {
            throw (Exception) message;
        } else if ("getvalue".equals(message)) {
            getSender().tell(stateCount, getSelf());
        } else {
            unhandled(message);
        }
    }
}

public class SupervisorActorDemo {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef workerActor = system.actorOf(Props.create(WorkerActor2.class), "workerActor2");
        workerActor.tell(new IOException(), ActorRef.noSender());
        System.out.println("===================================================");
        workerActor.tell("getvalue", ActorRef.noSender());
        system.terminate();
    }
}

