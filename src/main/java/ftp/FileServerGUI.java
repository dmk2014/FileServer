package ftp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class FileServerGUI {
    JFrame frame;
    JButton startServerButton, stopServerButton;
    JTextField hostnameField, portNumberField;
    FileServer server;
    TextArea serverOutput;

    //Constructor - build the GUI with required components
    public FileServerGUI() {
        frame = new JFrame();
        frame.setTitle("FileServer");
        frame.setIconImage(new ImageIcon(getClass().getResource("servericon.png")).getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(500, 270);
        frame.setResizable(false);
        frame.setLayout(new FlowLayout());

        JLabel hostnameLabel = new JLabel("Enter hostname: ");
        JLabel portNumberLabel = new JLabel("Enter port: ");
        hostnameField = new JTextField(10);
        hostnameField.setText("localhost");
        portNumberField = new JTextField(10);
        portNumberField.setText("8080");

        startServerButton = new JButton("Start FileServer");
        startServerButton.addActionListener(new StartServerEventHandler());

        stopServerButton = new JButton("Stop FileServer");
        stopServerButton.addActionListener(new StopServerEventHandler());
        stopServerButton.setEnabled(false);

        serverOutput = new TextArea();
        serverOutput.setSize(100,100);
        serverOutput.setEditable(false);
        this.redirectSystemStreams(); //Ensure System.out is redirected to the text area

        frame.add(hostnameLabel);
        frame.add(hostnameField);
        frame.add(portNumberLabel);
        frame.add(portNumberField);
        frame.add(startServerButton);
        frame.add(stopServerButton);
        frame.add(serverOutput);

        frame.setVisible(true);
    }

    //Event handlers for button clicks
    private class StartServerEventHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            server = new FileServer();
            String hostname = hostnameField.getText().trim();
            String portNum = portNumberField.getText().trim();

            if(validateInput(hostname,portNum)) {
                if(server.start(hostname, Integer.parseInt(portNum))) {
                    startServerButton.setEnabled(false);
                    stopServerButton.setEnabled(true);
                }
            }
        }

        //Validate the user specified hostname and port
        public boolean validateInput(String hostname, String port){
            boolean hostnameValid = true;
            boolean portNumValid = true;

            for (char c : hostname.toCharArray()) {
                if (!Character.isLetter(c) && !Character.isDigit(c) & c != '.') {
                    hostnameValid = false;
                    break;
                }
            }

            for (char c : port.toCharArray()){
                if(!Character.isDigit(c)){
                    portNumValid = false;
                    break;
                }
            }

            if(!hostnameValid || hostname.length() == 0) {
                JOptionPane.showMessageDialog(frame, "Invalid hostname", "Validation Error", JOptionPane.ERROR_MESSAGE);
                hostnameField.requestFocus();
                return false;
            }
            else if(!portNumValid || port.length() == 0){
                JOptionPane.showMessageDialog(frame, "Invalid port number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                portNumberField.requestFocus();
                return false;
            }

            return true;
        }
    }

    private class StopServerEventHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            server.stop();
            startServerButton.setEnabled(true);
            stopServerButton.setEnabled(false);
        }
    }

    public static void main(String[] args) {
        FileServerGUI gui = new FileServerGUI();
    }

    //The following two methods redirect System.out calls to the TextArea component on the FileServer GUI
    //Based on code from the below URL
    //http://unserializableone.blogspot.ie/2009/01/redirecting-systemout-and-systemerr-to.html
    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverOutput.append(text);
            }
        });
    }

    private void redirectSystemStreams() {
        //Create a new output stream and redirect System.out to use it
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
}