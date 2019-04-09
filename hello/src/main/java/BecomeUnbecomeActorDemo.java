package com.alex.akka.demo;

import akka.actor.UntypedActor;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Procedure;

// 2.7 Actor行为切换
class SimpleDemoActor extends UntypedActor {
    Procedure<Object> level1 = new Procedure<Object>() {
        public void apply(Object message) throws Exception {
            if (message instanceof String) {
                if (message.equals("end")) {
                    getContext().unbecome();
                }
            } else {
                Emp emp = (Emp) message;
                double result = emp.getSalary() * 1.8;
                System.out.println("员工" + emp.getName() + "的奖金为: " + result);
            }

            if(message.equals("become3")){
                getContext().become(level3, false);
            }

        }
    };

    Procedure<Object> level2 = new Procedure<Object>() {
        public void apply(Object message) throws Exception {
            if (message instanceof String) {
                if (message.equals("end")) {
                    getContext().unbecome();
                }
            } else {
                Emp emp = (Emp) message;
                double result = emp.getSalary() * 1.5;
                System.out.println("员工" + emp.getName() + "的奖金为: " + result);
            }

        }
    };

    Procedure<Object> level3 = new Procedure<Object>() {
        public void apply(Object message) throws Exception {
            if (message instanceof String) {
                if (message.equals("end")) {
                    getContext().unbecome();
                }
            } else {
                Emp emp = (Emp) message;
                double result = emp.getSalary() * 1.2;
                System.out.println("员工" + emp.getName() + "的奖金为: " + result);
            }

        }
    };

    @Override
    public void onReceive(Object message) throws Exception {
        String level = (String) message;
        if (level.equals("1")) {
            getContext().become(level1);
        } else if (level.equals("2")) {
            getContext().become(level2);
        }
    }
}

class Emp {
    private String name = "";
    private double salary = 0.0;

    public Emp(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}

public class BecomeUnbecomeActorDemo {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("sys");
        ActorRef ref = system.actorOf(Props.create(SimpleDemoActor.class), "simpleDemoActor");
        ref.tell("1", ActorRef.noSender());
        ref.tell(new Emp("张三", 10000), ActorRef.noSender());
        ref.tell(new Emp("李四", 20000), ActorRef.noSender());
        ref.tell("end", ActorRef.noSender());
        ref.tell("2", ActorRef.noSender());
        ref.tell(new Emp("王五", 10000), ActorRef.noSender());
        ref.tell(new Emp("赵六", 20000), ActorRef.noSender());
        ref.tell("become3", ActorRef.noSender());
        ref.tell(new Emp("鬼脚七", 200000), ActorRef.noSender());
        ref.tell("end", ActorRef.noSender());
        system.terminate();

    }
}