package com.pengbo.mhdzq.net;

import java.nio.channels.SocketChannel;

public class tagConnectInfo{
	public SocketChannel	socket;
	public int				index;
	public tagConnectInfo() {
		socket = null;
		index = 0;
	}
}

