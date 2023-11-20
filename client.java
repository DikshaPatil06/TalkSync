import java.net.*;
import java.io.*;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.BorderLayout;

public class client extends JFrame {
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JTextPane chatArea = new JTextPane();
    private JLabel heading = new JLabel("TalkSync : Client");
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    public client() {
        try {

            System.out.println("Sending request to the server ");
            socket = new Socket("127.0.0.1", 777);
            System.out.println("Connection Established...");

            // The primary purpose of an OutputStream is to write data, typically in the
            // form of bytes, to a destination, such as a file, network connection, or other
            // output sink.
            // The primary purpose of an InputStream is to read data, typically in the form
            // of bytes, from a source, such as a file, network connection, or other input
            // source.
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // The socket.getOutputStream() method in Java returns an OutputStream object.
            // This object represents the output stream connected to the socket. You can use
            // this OutputStream to send data from your program to the other end of the
            // socket connection.
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            startReading();
            handleEvents();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String cont = messageInput.getText();
                    appendToChatArea("You: " + cont, false);
                    out.println(cont);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }

        });
    }

    private void appendToChatArea(String text, boolean leftAlign) {
        StyledDocument doc = chatArea.getStyledDocument();
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setAlignment(set, leftAlign ? StyleConstants.ALIGN_LEFT : StyleConstants.ALIGN_RIGHT);
        doc.setParagraphAttributes(doc.getLength(), 1, set, false);
        try {
            doc.insertString(doc.getLength(), text + "\n", set);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        this.setTitle("TalkSync : ClientInfoStatus");
        // Label for the window
        this.setSize(500, 700);
        // size of the window
        this.setLocationRelativeTo(null);
        // take the window into center
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // X cross button for ending the program

        chatArea.setEditable(false);

        // coding for component
        heading.setFont(font);
        messageInput.setFont(font);

        this.setLayout(new BorderLayout());

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        // adding the components to the frame
        this.add(heading, BorderLayout.NORTH);
        this.add(chatArea, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
        // set window to visible
    }

    public void startReading() {
        // thread read karke deta rahega
        Runnable r1 = () -> {
            while (true) {
                try {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                        messageInput.setEnabled(false);
                        this.dispose();
                        socket.close();
                        break;
                    }
                    appendToChatArea("Server: " + msg, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        new client();
    }
}
