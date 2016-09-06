package hydra.ddos.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DDOSTest {

	public static void main(String[] args) {
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		try{
			socket = new Socket("127.0.0.1", 8833);
			in = new BufferedReader(new FileReader("dos_20160405_180"));
			out = new PrintWriter(socket.getOutputStream(),true);
			String buffer = null;
			buffer = in.readLine();
			while(buffer != null)
			{
				out.println(buffer);
				buffer = in.readLine();
			}
			in.close();
			out.close();
			socket.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
