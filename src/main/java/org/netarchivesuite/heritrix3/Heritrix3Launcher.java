package org.netarchivesuite.heritrix3;

public class Heritrix3Launcher {

    // export FOREGROUND=true;bash ./bin/heritrix -b 192.168.1.101 -p 6443 -a h3server:h3server -s h3server.jks,h3server,h3server

	protected Heritrix3Launcher() {
	}

	public static Heritrix3Launcher getInstance() {
		return new Heritrix3Launcher();
	}

}
