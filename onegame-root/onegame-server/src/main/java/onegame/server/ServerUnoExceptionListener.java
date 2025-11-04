package onegame.server;

import java.net.SocketException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListener;

import io.netty.channel.ChannelHandlerContext;

/**
 * Gestore delle eccezioni per il server Uno.
 */
public class ServerUnoExceptionListener implements ExceptionListener {

	private static Logger logger = LoggerFactory.getLogger(ServerUnoExceptionListener.class);

	@Override
	public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnectException(Exception e, SocketIOClient client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectException(Exception e, SocketIOClient client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPingException(Exception e, SocketIOClient client) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
		if (e instanceof SocketException && e.getMessage().contains("Connection reset")) {
			logger.info("Client disconnesso: {}", e.getMessage());
			return true;
		}
		return false;
	}

}
