import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public ChatServer(int port) throws IOException{
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            while (true){
                Socket s = serverSocket.accept();
                System.out.println("Accepted from " + s.getInetAddress());
                ChatHandler handler = new ChatHandler(s);
                handler.start();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            serverSocket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        /*if(args.length !=1){
            throw new RuntimeException("Syntax: ChatServer <port>");
        }*/

        try
        {
            String args0 = "8082";
            new ChatServer(Integer.parseInt(args0));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
