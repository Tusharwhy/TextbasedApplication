import java.net.*;
import java.io.*;
public class Server {

    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;


    public Server(){//constructor

        try{

            server =new ServerSocket(7777);
            System.out.println("server ready for connection");
            System.out.println("waiting ...");
            socket = server.accept();

            // we get inputstream from socket >> which is then used by inputstream reader(data in bytes will be changed to character) >> after that bufferreader will handle it.
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // for sending data.          note-> java streams are uni-directional
            out = new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void startReading(){// we need to do reading and writing simultaneously so multithreading is implemented
        Runnable r1 = ()->{
            System.out.println("reader started");

            // as we need to read simultaneously while loop is used..
            try {
                while (true) {

                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("client left the chat.");
                        socket.close();
                        break;
                    }
                    System.out.println("client : " + msg);

                }
            }
            catch(Exception e){
                //e.printStackTrace();
                System.out.println("connection closed");
            }
        };
        new Thread(r1).start();

    }
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
            }catch (Exception e){
                //e.printStackTrace();
                System.out.println("connection closed");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("server testing..");

        new Server();
    }
}

