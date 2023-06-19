package com.kurata.booking.ui.payment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.kurata.booking.R;
import com.kurata.booking.data.model.Booking;
import com.kurata.booking.data.model.Room;
import com.kurata.booking.databinding.ActivityPaymentMethodBinding;
import com.kurata.booking.zalopay.Api.CreateOrder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class Payment_method extends AppCompatActivity {

    private ActivityPaymentMethodBinding binding;
    private Date checkin;
    private long remainingtime;
    private boolean check = false;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(554, Environment.SANDBOX);


        Bundle args = getIntent().getBundleExtra("BUNDLE");
        Booking object = (Booking) args.getSerializable("model");
        Room room = (Room) args.getSerializable("room");
        Log.d("tsetrtsd", object.getGuestbirth().toString());

        new CountDownTimer(1000000, 1000) {

            public void onTick(long millisUntilFinished) {
               // binding.timePm.setText("00 :" + millisUntilFinished / 1000);
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                binding.timePm.setText("TIME : " + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
                remainingtime=millisUntilFinished;
            }

            public void onFinish() {


            }
        }.start();


        binding.txtzalo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                check =true;
                CreateOrder orderApi = new CreateOrder();

                try {
                    JSONObject data = orderApi.createOrder(room.getPrice());
                    Log.d("Amount", room.getPrice());
                    String code = data.getString("returncode");
                    Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                    if (code.equals("1")) {
//                        lblZpTransToken.setText("zptranstoken");
//                        txtToken.setText(data.getString("zptranstoken"));
//                        IsDone();
                        token =data.getString("zptranstoken");


                        ZaloPaySDK.getInstance().payOrder(Payment_method.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                                Intent i = new Intent(getApplicationContext(), Payment_success.class);
                                i.putExtra("checkin", binding.txtcheckin.getText());
                                i.putExtra("time", remainingtime);
                                startActivity(i);
                                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(Payment_method.this)
                                            .setTitle("Payment Success")
                                            .setMessage(String.format("TransactionId: %s - TransToken: %s", transactionId, transToken))
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .setNegativeButton("Cancel", null).show();
                                }

                            });
                            //IsLoading();
                            }

                            @Override
                            public void onPaymentCanceled(String zpTransToken, String appTransID) {
                                new AlertDialog.Builder(Payment_method.this)
                                        .setTitle("User Cancel Payment")
                                        .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).show();
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                                new AlertDialog.Builder(Payment_method.this)
                                        .setTitle("Payment Fail")
                                        .setMessage(String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).show();
                            }
                        });
                        Toast.makeText(Payment_method.this, "susscess zalopay", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        checkin = new Date();
        checkin.setTime( getIntent().getLongExtra("checkin",-1));
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        binding.txtdayofbirth.setText(object.getGuestbirth().toString());
        binding.txtname.setText(object.getGuestname());
        binding.txtcheckin.setText(format.format(checkin.getTime()));
        binding.txtemail.setText(object.getGuestemail());
        binding.txtPrice.setText(room.getPrice());

        binding.back.setOnClickListener(v-> onBackPressed());

        binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(check){
//                    ZaloPaySDK.getInstance().payOrder(Payment_method.this, token, "demozpdk://app", new PayOrderListener() {
//                        @Override
//                        public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
//                            Intent i = new Intent(getApplicationContext(), Payment.class);
//                            i.putExtra("checkin", binding.txtcheckin.getText());
//                            i.putExtra("time", remainingtime);
//                            startActivity(i);
//                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    new AlertDialog.Builder(Payment_method.this)
//                                            .setTitle("Payment Success")
//                                            .setMessage(String.format("TransactionId: %s - TransToken: %s", transactionId, transToken))
//                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                }
//                                            })
//                                            .setNegativeButton("Cancel", null).show();
//                                }
//
//                            });
//                            IsLoading();
//                        }
//
//                        @Override
//                        public void onPaymentCanceled(String zpTransToken, String appTransID) {
//                            new AlertDialog.Builder(Payment_method.this)
//                                    .setTitle("User Cancel Payment")
//                                    .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                        }
//                                    })
//                                    .setNegativeButton("Cancel", null).show();
//                        }
//
//                        @Override
//                        public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
//                            new AlertDialog.Builder(Payment_method.this)
//                                    .setTitle("Payment Fail")
//                                    .setMessage(String.format("ZaloPayErrorCode: %s \nTransToken: %s", zaloPayError.toString(), zpTransToken))
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                        }
//                                    })
//                                    .setNegativeButton("Cancel", null).show();
//                        }
//                    });
//
//        }
//                else{
                    Intent i = new Intent(getApplicationContext(), Payment_success.class);
                    Bundle bundle = new Bundle();
                    Booking model = new Booking();
                    model.setGuestname(object.getGuestname());
                    model.setGuestemail(object.getGuestemail());
                    model.setGuestbirth(object.getGuestbirth());
                    model.setCheckout(object.getCheckout());
                    model.setCheckin(object.getCheckin());
                    model.setRoom_id(room.getId());
                    model.setSpecicalrequest(object.getSpecicalrequest());

                    bundle.putSerializable("model", model);
                    bundle.putSerializable("room", room);
                    i.putExtra("BUNDLE",bundle);
                    i.putExtra("time", remainingtime);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
//                }

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}