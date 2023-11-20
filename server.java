import java.net.*;

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

import java.awt.BorderLayout;
import java.awt.Font;

import java.io.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class server extends JFrame {

    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("TalkSync : Server");
    private JTextPane chatArea = new JTextPane();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    // private JText

    public server() {
        createGUI();

        try {
            server = new ServerSocket(777);
            System.out.println("Server is ready to connect");
            System.out.println("Waiting for connection");

            socket = server.accept();
            
            JOptionPane.showMessageDialog(null, "Waiting for Client to Connect !! ");
            
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
        startReading();
        handleEvents();
    }

    public void createGUI() {
        this.setTitle("TalkSync : ServerIntoStatus");
        this.setSize(500, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea.setEditable(false);

        heading.setFont(font);
        messageInput.setFont(font);

        this.setLayout(new BorderLayout());
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(heading, BorderLayout.NORTH);
        this.add(chatArea, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
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

    public void handleEvents() {
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
                    appendToChatArea("Server: " + cont, false);
                    out.println(cont);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }

        });
    }

    public void startReading() {
        // thread read karke deta rahega
        Runnable r1 = () -> {
            while (true) {
                try {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client Terminated the chat");
                        messageInput.setEnabled(false);
                        this.dispose();
                        socket.close();
                        break;
                    }
                    appendToChatArea("Client: " + msg, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        new server();
    }
}