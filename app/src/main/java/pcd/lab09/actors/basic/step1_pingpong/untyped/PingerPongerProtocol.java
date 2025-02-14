package pcd.lab09.actors.basic.step1_pingpong.untyped;

import akka.actor.ActorRef;

public interface PingerPongerProtocol {

	class PongMsg implements PingerPongerProtocol {
		public final long count;
		public final ActorRef ponger;
		public PongMsg(long count, ActorRef ponger) {
			this.count = count;
			this.ponger = ponger;
		}
	}	

	class PingMsg implements PingerPongerProtocol {
		public final long count;
		public final ActorRef pinger;
		public PingMsg(long count, ActorRef pinger) {
			this.count = count;
			this.pinger = pinger;
		}
	}	

	class BootMsg implements PingerPongerProtocol {
		public final ActorRef ponger;
		public BootMsg(ActorRef ponger) {
			this.ponger = ponger;
		}                                                                                                                           
	}
}
