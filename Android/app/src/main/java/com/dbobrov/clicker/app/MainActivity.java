package com.dbobrov.clicker.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainActivity extends Activity implements View.OnClickListener, Runnable {
    private String ip;
    private int port;
    private Thread worker;
    private volatile boolean isWorking;
    private final BlockingQueue<Direction> queue = new LinkedBlockingQueue<Direction>();
    private volatile ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        ip = intent.getStringExtra(Constants.EXTRA_KEY_IP);
        if (ip == null) {
            finish();
            return;
        }

        port = intent.getIntExtra(Constants.EXTRA_KEY_PORT, -1);
        if (port == -1) {
            finish();
            return;
        }

        isWorking = true;

        worker = new Thread(this);
        worker.start();

        dialog = ProgressDialog.show(this, "Connecting", "Please wait...", true, false);
    }

    @Override
    public void onClick(View v) {
        Direction direction;
        switch (v.getId()) {
            case R.id.left:
                direction = Direction.LEFT;
                break;
            case R.id.right:
                direction = Direction.RIGHT;
                break;
            default:
                throw new RuntimeException("!!!");
        }
        queue.add(direction);
    }

    @Override
    protected void onDestroy() {
        isWorking = false;
        worker.interrupt();
        super.onDestroy();
    }

    @Override
    public void run() {
        Socket socket;
        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(3000);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot connect to " + ip + ":" + port, Toast.LENGTH_LONG).show();
            finish();
            return;
        } finally {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }


        try {
            OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());

            while (isWorking) {
                Direction direction;
                try {
                    direction = queue.take();
                } catch (InterruptedException e) {
                    return;
                }

                writer.write(String.valueOf(direction) + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        }
    }
}
