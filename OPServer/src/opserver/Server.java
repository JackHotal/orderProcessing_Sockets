package opserver;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {
    public final static int mPORT = 1574;
    protected ServerSocket mListenSocket;

    public Server()
    {
        try{
            mListenSocket = new ServerSocket(mPORT);
            System.out.println("Server: listening on port " + mPORT);
        }
        catch(IOException ie){ie.printStackTrace();}

    }

    public static void main(String[] args)
    {
        Server s = new Server();
        try{
            while(true)
            {
                Socket clientSocket = s.mListenSocket.accept();
                ConversationManager c = new ConversationManager(clientSocket);
                c.start();
            }

        }
        catch(IOException ie){ie.printStackTrace();}

    }

}
