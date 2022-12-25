import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client extends JFrame {

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    // Declaring components
    private JLabel heading  = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN,20);

//// Constructor
    public Client(){
        try{
            System.out.println("Sending request...");
            socket = new Socket("127.0.0.1",7777);
            System.out.println("Connection built!");


            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
           // startWriting();

        }catch (Exception e){
           // e.printStackTrace();
        }
    }

    private void handleEvents(){

        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                //10 is the keycode for enter
                if(e.getKeyCode() == 10) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me : "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }

            }
        });
    }

    private void createGUI(){
        // gui code
        this.setTitle("Client texter");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setIcon(new ImageIcon("chaticon.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        messageArea.setEditable(false);    // so you wont be able to edit text messages after you've send them.
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // frame layout
        this.setLayout(new BorderLayout());
        // adding components to frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);



        this.setVisible(true);
    }

    //// Start reading method
    public void startReading(){// we need to do reading and writing simultaneously so multithreading is implemented
        Runnable r1 = ()->{
            System.out.println("reader started");

            // as we need to read simultaneously while loop is used..
            try {
                while (true) {
                        String msg = br.readLine();
                        if (msg.equals("exit")) {
                            System.out.println("Server left the chat.");
                            JOptionPane.showMessageDialog(this,"Server terminated the chat");
                            messageInput.setEnabled(false);
                            socket.close();
                            break;
                        }
                        //System.out.println("Server : " + msg);
                    messageArea.append("Server : " + msg+"\n");

                }
            }catch (Exception e){
                //e.printStackTrace();
                System.out.println("connection closed");
            }
        };
        new Thread(r1).start();

    }

    //// Strart writing method
    public void startWriting(){
        Runnable r2 = ()->{
            System.out.println("writer started..");

            try {

                while (!socket.isClosed()) {

                        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
                        String content = keyboard.readLine();
                        out.println(content);
                        out.flush();   // to forcefully send the data.

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                }
                System.out.println("connection closed");
            } catch (Exception e){
                e.printStackTrace();

            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("Client starting..");
        new Client();
    }
}
