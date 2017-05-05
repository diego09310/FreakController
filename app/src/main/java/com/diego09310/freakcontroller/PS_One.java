package com.diego09310.freakcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class PS_One extends OptionsMenuActivity {

    private static final  String MODE = "epsxe";
    private static int socketFail = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ps_one);

        JoystickView joystickCross = (JoystickView) findViewById(R.id.joystickViewCross);
        joystickCross.setOnMoveListener(new JoystickView.OnMoveListener() {
            public void onMove(int angle, int strength) {
                if (strength > 25) {
                    if (angle <= 22 | angle >= 337) {
                        poll |= RIGHT;
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

        Button buttonR1 = (Button) findViewById(R.id.r1_button);
        Button buttonL1 = (Button) findViewById(R.id.l1_button);
        Button buttonR2 = (Button) findViewById(R.id.r2_button);
        Button buttonL2 = (Button) findViewById(R.id.l2_button);
        buttonR1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        poll |= R1;
                        break;
                    case MotionEvent.ACTION_UP:
                        poll &= ~R1;
                        break;
                }
                return true;
            }
        });
        buttonL1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        poll |= L1;
                        break;
                    case MotionEvent.ACTION_UP:
                        poll &= ~L1;
                        break;
                }
                return true;
            }
        });
        buttonR2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        poll |= R2;
                        break;
                    case MotionEvent.ACTION_UP:
                        poll &= ~R2;
                        break;
                }
                return true;
            }
        });
        buttonL2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        poll |= L2;
                        break;
                    case MotionEvent.ACTION_UP:
                        poll &= ~L2;
                        break;
                }
                return true;
            }
        });

        Thread pThread = new Thread(new Pollster());
        pThread.start();
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
            default:
                break;
        }
    }

    class ClientThread implements Runnable {
        public ClientThread(Context ctx) {
            this.ctx = ctx;
        }

        public void run() {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
                String serverIp = sharedPreferences.getString(SettingsActivity.SERVER_IP, "");
                int port = Integer.parseInt(sharedPreferences.getString(SettingsActivity.PORT, ""));
                InetAddress serverAddr = InetAddress.getByName(serverIp);
                DatagramSocket clientSocket = new DatagramSocket();

                String str = MODE + ":" + command;
                DatagramPacket sendPacket = new DatagramPacket(str.getBytes(), str.length(), serverAddr, port);

                clientSocket.send(sendPacket);

                byte[] receiveData = new byte[20];
                DatagramPacket recvPacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.setSoTimeout(500);
                clientSocket.receive(recvPacket);
                // String recStr = new String(recvPacket.getData());

                socketFail = 0;
                clientSocket.close();

            } catch (SocketTimeoutException e) {
                if (socketFail == 5) {
                    socketFail = 0;
                    showToast("Failed to connect to server", Toast.LENGTH_SHORT);
                } else {
                    socketFail++;
                }
                Log.w("PS_One", "UDP Socket: SocketTimeoutException. No answer from server.");
            } catch (SocketException e) {
                if (socketFail == 5) {
                    socketFail = 0;
                    showToast("Error while connecting to server", Toast.LENGTH_SHORT);
                } else {
                    socketFail++;
                }
                Log.w("PS_One", "UDP Socket: SocketException. Bad connection to server.");
            } catch (Exception e) {
                Log.e("PS_One", "Unexpected Exception: " + e.getMessage());
            }
        }

        private int command;
        private Context ctx;

        void setCommand(int cmd) {
            this.command = cmd;
        }
    }

    public void sendPoll(int poll) {
        ClientThread client = new ClientThread(this);
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
                    poll &= R1 | L1 | R2 | L2;
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
            Log.w("PS_One", "Thread.sleep interrupted.");
        }
    }

    private long startTime = 0;
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