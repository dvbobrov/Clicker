package com.dbobrov.clicker.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class StartActivity extends Activity implements View.OnClickListener {
    private Button connectBtn;
    private EditText ipFld, portFld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        connectBtn = (Button) findViewById(R.id.ok);
        portFld = (EditText) findViewById(R.id.port);
        ipFld = (EditText) findViewById(R.id.ip_address);

        connectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == connectBtn) {
            String ip = ipFld.getText().toString();
            if (!validateIp(ip)) {
                Toast.makeText(this, R.string.incorrect_ip, Toast.LENGTH_LONG).show();
                return;
            }

            String portStr = portFld.getText().toString();
            int port = -1;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ignore) {
            }

            if (port < 0 || port > 65535) {
                Toast.makeText(this, "Port must be a number between 1 and 65535", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.EXTRA_KEY_IP, ip);
            intent.putExtra(Constants.EXTRA_KEY_PORT, port);
            startActivity(intent);
        }
    }

    private boolean validateIp(String ip) {
        String[] segments = ip.split("\\.");
        if (segments.length != 4) return false;
        try {
            for (String s : segments) {
                int n = Integer.parseInt(s);
                if (n < 0 || n > 255) return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
