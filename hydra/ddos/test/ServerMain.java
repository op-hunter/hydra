package hydra.ddos.test;

import hydra.ddos.HydraServer;

public class ServerMain {

	public static void main(String[] args) {
		HydraServer hs = new HydraServer();
		Thread serverThread = new Thread(hs);
		serverThread.run();

	}

}
