import java.io.*;
import java.net.Socket;
import java.util.*;

public class ChatHandler extends Thread {
    protected Socket socket;
    protected DataOutputStream outputStreamm;
    protected DataInputStream inputStream;
    protected boolean isOn;

    protected static List<ChatHandler> handlers = Collections.synchronizedList(new ArrayList<ChatHandler>());

    public ChatHandler(Socket s) throws IOException {
        socket = s;
        inputStream = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        outputStreamm = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
    }

    @Override
    public void run() {
        isOn = true;
        try {
            handlers.add(this);
            while (isOn) {
                String msg = inputStream.readUTF();
                broadcast(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            handlers.remove(this);
            try {
                outputStreamm.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected static void broadcast(String message) {
        synchronized (handlers) {
            Iterator<ChatHandler> it = handlers.iterator();
            while (it.hasNext()) {
                ChatHandler c = it.next();
                try {
                    synchronized (c.outputStreamm) {
                        c.outputStreamm.writeUTF(message);
                    }
                    c.outputStreamm.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    c.isOn = false;
                }
            }
        }
    }
}
