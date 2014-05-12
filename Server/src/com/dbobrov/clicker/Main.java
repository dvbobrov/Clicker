package com.dbobrov.clicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main extends JFrame {
    public Main(String s) throws HeadlessException {
        super(s);
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println("Usage: clicker-server [port] [client number]");
            return;
        }

        int port = -1;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignore) {
            }
            if (port < 1 || port > 65535) {
                System.err.println("Port must be a number between 1 and 65535");
                return;
            }
        } else {
            port = 5444;
        }

        int clientNumber = -1;

        if (args.length > 1) {
            try {
                clientNumber = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignore) {
            }
            if (clientNumber < 1) {
                System.err.println("Client number must be a positive number");
                return;
            }
        } else {
            clientNumber = 1;
        }

        final Server server;
        try {
            server = new Server(port, clientNumber);
        } catch (AWTException | IOException e) {
            e.printStackTrace();
            return;
        }

        JFrame window = new Main("Clicker Server");
        window.setLayout(new GridLayout(2, 1));

        JLabel info = new JLabel("Server started on port " + port);
        window.add(info);

        JLabel instructions = new JLabel("<html>1. Enable wireless network tethering on your mobile device<br/>" +
                "2. Connect your PC to that network<br/>" +
                "3. Get IP address of this PC in your network<br/>" +
                "4. Enter the IP into the text field in Android application<br/>" +
                "5. PROFIT!</html>");
        window.add(instructions);


        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                server.interrupt();
                System.exit(0);
            }
        });

        window.setSize(new Dimension(400, 200));
        window.setVisible(true);
    }
}
