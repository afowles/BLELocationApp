/**
* Server.java
* 
* @version   $Id: 1.8
*
* Revisions:
*
*      Initial revision
*
**/
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
* Server class that maintains a server time that can be changed with correct username and password.  
*
* @author Bryan Passino
*/

public class Server {
	
	private ServerSocket socket;
	private int port = 5000;
	
	public Server(){
		try {
			socket = new ServerSocket(port);
			String ip = getCurrentIp();
			System.out.println("Server Address: " + ip);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		new HandleTCP().start();
	}
	
	public static void main(String[] args){
		new Server();
	}
	
	public String getCurrentIp() throws UnknownHostException {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces
                        .nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while(nias.hasMoreElements()) {
                    InetAddress ia= (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress() 
                     && !ia.isLoopbackAddress()
                     && ia instanceof Inet4Address) {
                        return ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("unable to get current IP ");
        }
        return null;
    }
	
	class HandleTCP extends Thread{
		
		/**
		* Accept a socket connection and starts an inner thread to handle the connection.
		*
		*@param		none	
		*
		*@return	void		
		*/
		public void run(){
			while(true){
				try {
					System.out.println("listening");

					Socket connectedSocket = socket.accept();
					System.out.println("accepted");
					new Thread(new TCP_Connection(connectedSocket)).start();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}//end while
		}
		
		/**
		* Thread to handle individual TCP connection
		*
		*@param		n/a	
		*
		*@return	n/a		
		*/
		class TCP_Connection implements Runnable{
			private Socket connectedSocket;
			
			/**
			* Constructor
			*
			*@param	 	Socket connectedSocket	
			*
			*@return	n/a		
			*/
			public TCP_Connection(Socket connectedSocket){
				this.connectedSocket = connectedSocket;
			}
			
			/**
			* Accepts an incoming request to return time or to set time. The response is sent back along 
			* with hop info at the end of return message.
			*
			*@param		none	
			*
			*@return	void		
			*/
			public void run(){
				String clientIp = "";
				try{ 
					InetAddress inetAddress = connectedSocket.getInetAddress();
					clientIp = inetAddress.getHostAddress();
					System.out.println("Connected to " + clientIp);
					//create data input stream
					DataInputStream fromClient = new DataInputStream(connectedSocket.getInputStream());
					
					//create data output stream
					DataOutputStream toClient = new DataOutputStream(connectedSocket.getOutputStream());
					
					while(true){
						
						String request = fromClient.readUTF();
						System.out.println(request);
						toClient.writeUTF("Stuff from Server");
					}
				}
				catch(IOException ex){
					System.err.println(ex);
				}//end catch 
				
			}
		}
	}//end inner class
	
}