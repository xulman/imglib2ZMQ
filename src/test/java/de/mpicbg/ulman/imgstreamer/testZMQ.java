package de.mpicbg.ulman.imgstreamer;

import java.io.*;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

public class testZMQ
{
	public static void main(String... args)
	{
		System.out.println("-------------------------------------------------");
		testNonBufferedReadOut();

		System.out.println("-------------------------------------------------");
		testBufferedReadOut();
	}

	public static void testNonBufferedReadOut()
	{
		try {
			ZMQ.Context zmqContext = ZMQ.context(1);
			ZMQ.Socket zmqSocket = zmqContext.socket(ZMQ.PAIR);
			zmqSocket.connect("tcp://localhost:3456");

			byte[] testSendArray = new byte[] { 97,120,98,99,100,97,98,99,100,97,98,99 };
			zmqSocket.send(testSendArray);

			final ZeroMQInputStream zis = new ZeroMQInputStream(3456, 5);
			int nextVal = 0;
			do {
				nextVal = zis.read();
				System.out.println("got now this value: "+nextVal);

				if (nextVal == 120)
				{
					//modify the buffer to recognize it...
					for (int i=0; i < testSendArray.length; ++i) testSendArray[i]+=4;
					zmqSocket.send(testSendArray);
				}
			} while (nextVal > -1);

			zmqSocket.close();
			zis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void testBufferedReadOut()
	{
		try {
			ZMQ.Context zmqContext = ZMQ.context(1);
			ZMQ.Socket zmqSocket = zmqContext.socket(ZMQ.PAIR);
			zmqSocket.connect("tcp://localhost:3456");

			byte[] testSendArray = new byte[] { 97,120,98,99,100,97,98,99,100,97,98,99 };
			zmqSocket.send(testSendArray);

			byte[] testRecvArray = new byte[5];

			final ZeroMQInputStream zis = new ZeroMQInputStream(3456, 10);
			int nextVal = 0;
			do {
				int itemsRead = zis.read(testRecvArray);
				nextVal = itemsRead;
				System.out.println("itemsRead="+itemsRead);

				for (int q=0; q < itemsRead; ++q)
				{
					nextVal = testRecvArray[q];
					System.out.println("got now this value: "+nextVal);

					if (nextVal == 120)
					{
						//modify the buffer to recognize it...
						for (int i=0; i < testSendArray.length; ++i) testSendArray[i]+=4;
						zmqSocket.send(testSendArray);
					}
				}
			} while (nextVal > -1);

			zmqSocket.close();
			zis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}