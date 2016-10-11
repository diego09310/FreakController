package com.diego09310.freakcontroller;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class PS_One extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ps_one);

        JoystickView joystickCross = (JoystickView) findViewById(R.id.joystickViewCross);
        joystickCross.setOnMoveListener(new JoystickView.OnMoveListener() {
            public void onMove(int angle, int strength) {
//                Log.d("Poll: ", String.valueOf(poll));
                if (strength > 25) {
                    if (angle <= 22 | angle >= 337) {
                        poll |= RIGHT;
                        // sendCommand("right");
                    } else if (angle <= 67) {
                        poll |= RIGHT|UP;
                    } else if (angle <= 112) {
                        poll |= UP;
                    } else if (angle <= 157) {
                        poll |= UP|LEFT;
                    } else if (angle <= 202) {
                        poll |= LEFT;
                    } else if (angle <= 247) {
                        poll |= LEFT|DOWN;
                    } else if (angle <= 292) {
                        poll |= DOWN;
                    } else if (angle <= 337) {
                        poll |= DOWN|RIGHT;
                    }
                }
            }
        }, 20);

        JoystickView joystickShapes = (JoystickView) findViewById(R.id.joystickViewShapes);
        joystickShapes.setOnMoveListener(new JoystickView.OnMoveListener() {
            public void onMove(int angle, int strength) {
                if (strength > 25) {
                    if (angle <= 45 | angle > 315) {
                        poll |= CIRCLE;
                        // sendCommand("right");
                    } else if (angle <= 135) {
                        poll |= TRIANGLE;
                    } else if (angle <= 225) {
                        poll |= SQUARE;
                    } else {
                        poll |= X;
                    }
                }
            }
        });

        Thread pThread = new Thread(new Pollster());
        pThread.start();
    }

    private Socket socket;
    private static final int SERVERPORT = 9000;
    private static final String SERVER_IP = "192.168.0.120";

    class ClientThread implements Runnable {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

                socket = new Socket(serverAddr, SERVERPORT);    // Toast when failed connection

                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                out.println(command);
            } catch (ConnectException e) {
                showToast("Failed to connect to server", Toast.LENGTH_SHORT);
            } /*catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/ catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int command;

        public void setCommand(int cmd) {
            this.command = cmd;
        }
    }

    public void sendKey(View v) {   // Change to Joystick
        String key = v.getTag().toString();
        switch (key) {
            case "START":
                poll |= START;
                break;
            case "SELECT":
                poll |= SELECT;
                break;
            case "L2":
                poll |= L2;
                break;
            case "R2":
                poll |= R2;
                break;
            case "L1":
                poll |= L1;
                break;
            case "R1":
                poll |= R1;
                break;
            default:
                break;
        }
    }

    public void sendPoll(int poll) {
        ClientThread client = new ClientThread();
        client.setCommand(poll);
        Thread cThread = new Thread(client);
        cThread.start();
    }

    private final int SELECT = 1 << 0;
    private final int START = 1 << 1;
    private final int UP = 1 << 2;
    private final int RIGHT = 1 << 3;
    private final int DOWN = 1 << 4;
    private final int LEFT = 1 << 5;
    private final int L2 = 1 << 6;
    private final int R2 = 1 << 7;
    private final int L1 = 1 << 8;
    private final int R1 = 1 << 9;
    private final int TRIANGLE = 1 << 10;
    private final int CIRCLE = 1 << 11;
    private final int X = 1 << 12;
    private final int SQUARE = 1 << 13;

    int poll = 0;
    boolean sendZero = false; // Indicates when to send a zero to the server to disable all keys
    class Pollster implements Runnable {
        public void run() {
            while (true) {
                if (poll > 0) {
                    sendPoll(poll);
                    poll = 0;
                    sendZero = true;
                } else if (sendZero) {
                    sendPoll(0);
                    sendZero = false;
                }
                // TODO: Automatically disconnect during inactivity - Probably not necessary
                sleep(60);
            }
        }
    }

    public void onAnalogCheckboxClick(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        JoystickView joystickCross = (JoystickView) findViewById(R.id.joystickViewCross);
        if (checked) {
            joystickCross.setBackground(null);
            joystickCross.setButtonColor(Color.parseColor("#506060"));
            joystickCross.setBorderColor(Color.parseColor("#444444"));
        } else {
            joystickCross.setBackgroundResource(R.drawable.ic_cross);
            joystickCross.setButtonColor(Color.parseColor("#00000000"));
            joystickCross.setBorderColor(Color.parseColor("#00000000"));
        }
    }

    private void sleep (int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    long startTime = 0;
    public void showToast(final String toast, final int toastLength) {
        // Avoid concatenate several toasts
        long difference = System.currentTimeMillis() - startTime;
        if (difference > 2500) {
            runOnUiThread(new Runnable() {
                public void run()
                {
                    Toast.makeText(PS_One.this, toast, toastLength).show();
                }
            });
            startTime = System.currentTimeMillis();
        }
    }
}