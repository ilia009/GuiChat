import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class ClientGui extends JFrame implements Runnable {
    protected Socket socket;
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    protected JTextArea outTextArea;
    protected JTextField inTextField;
    protected boolean isOn;


    public ClientGui(String title, Socket s, DataInputStream dis, DataOutputStream dos) {
        super(title);
        socket = s;
        inputStream = dis;
        outputStream = dos;

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, outTextArea = new JTextArea());
        outTextArea.setEditable(false);
        cp.add(BorderLayout.SOUTH, inTextField = new JTextField());

        inTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    outputStream.writeUTF(inTextField.getText());
                    outputStream.flush();
                }catch (IOException ee){
                    ee.printStackTrace();
                    isOn = false;
                }
                inTextField.setText("");
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                isOn = false;
                try{
                    outputStream.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
                try {
                    socket.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                    }
                }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setVisible(true);
        inTextField.requestFocus();
        (new Thread(this)).start();

    }

    public static void main(String[] args) throws IOException {

        /*
        if(args.length !=2){
        throw new RuntimeException("Syntax: ClientGui <host> <port>);
        }
         */
        String args0 = "localhost";
        String args1 = "8082";

        Socket socket = new Socket(args0, Integer.parseInt(args1));
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            new ClientGui("Chat" + args0 + ":" + args1, socket, dis, dos); //ATTENTION ON THIS
        } catch (IOException ex) {
            ex.printStackTrace();
            try {
                if (dos != null) dos.close();
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException ex3) {
                ex3.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        isOn = true;
        try{
            while (isOn){
                String line = inputStream.readUTF();
                outTextArea.append(line + "\n");
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }finally {
            inTextField.setVisible(false);
            validate();
        }
    }
}