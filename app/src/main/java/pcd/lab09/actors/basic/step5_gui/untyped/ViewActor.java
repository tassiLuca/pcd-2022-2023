package pcd.lab09.actors.basic.step5_gui.untyped;

import akka.actor.AbstractActor;

/**
 * This example is based on the previous Akka API
 */
public class ViewActor extends AbstractActor {
	@Override
	public Receive createReceive() {
		return receiveBuilder().match(PressedMsg.class, msg -> System.out.println("Pressed!")).build();
	}
}
