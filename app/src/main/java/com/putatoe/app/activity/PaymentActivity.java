package com.putatoe.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.putatoe.app.R;
import com.putatoe.app.pojo.Product;
import com.putatoe.app.pojo.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

import io.paperdb.Paper;

public class PaymentActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private TextView transactionResponseView;
    private ImageView paymentStatusImage;
    private ProgressBar progressBar;
    private User user;
    private String token;
    private String service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        relativeLayout = (RelativeLayout) findViewById(R.id.payment_root_layout);
        transactionResponseView = (TextView) findViewById(R.id.transaction_response_view);
        paymentStatusImage = (ImageView) findViewById(R.id.payment_status_image);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Paper.init(PaymentActivity.this);


        new Handler().post(new Runnable() {
            @Override
            public void run() {

                user = Paper.book().read("user");
                Log.e("User Object",user.getId());
                user.setEmail("support@putatoe.com");
                Intent i = getIntent();
                if(i!=null && i.getExtras()!=null){

                    token = Paper.book().read("token");
                    Product orderedProduct = (Product) i.getSerializableExtra("product");
                    service = i.getExtras().getString("service");
                    String orderID = UUID.randomUUID().toString();
                    String transactionAmount = orderedProduct.getPrice();

                    try {
                        getPaytmCheckSumHash(orderID,transactionAmount,user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private void getPaytmCheckSumHash(final String orderID,final String transactionAmount, final User user) throws JSONException {

        Log.e("Paytm Method :","Coming Here");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Generating Payment Details");
        progressDialog.show();
        String  jsonData = "{\"orderID\":"+orderID+",\"transactionAmount\":\""+transactionAmount+"\",\"userDetail\":{\"id\":"+user.getId()+",\"username\":\""+user.getUsername()+"\",\"email\":\""+user.getEmail()+"\"}}";
        JSONObject jsonObject = new JSONObject(jsonData);
        AndroidNetworking.post(getString(R.string.domain_name)+getString(R.string.paytm_checksum_url))
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            Log.e("Paytm Hash :",response.getString("value"));
                            createPaytmOrder(orderID,transactionAmount,user,response.getString("value"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("Paytm Error",error.getErrorDetail());

                    }
                });

    }

    private void createPaytmOrder(String orderID, String transactionAmount, User user, String paytmCheckSumHash){

        HashMap<String, String> paramMap = new HashMap<String,String>();
        paramMap.put( "MID" , "fTTkZD94600414717681");
        paramMap.put( "ORDER_ID" , orderID);
        paramMap.put( "CUST_ID" , user.getId());
        paramMap.put( "MOBILE_NO" , user.getUsername());
        paramMap.put( "EMAIL" , user.getEmail());
        paramMap.put( "CHANNEL_ID" , "WAP");
        paramMap.put( "TXN_AMOUNT" , transactionAmount);
        paramMap.put( "WEBSITE" , "WEBSTAGING");
        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put( "CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orderID);
        paramMap.put( "CHECKSUMHASH" , paytmCheckSumHash);
        PaytmOrder Order = new PaytmOrder(paramMap);
        Log.e("Param Map",paramMap.toString());

        PaytmPGService Service = PaytmPGService.getStagingService();
        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
            /*Call Backs*/
            public void someUIErrorOccurred(String inErrorMessage) {
                Snackbar snackbar = Snackbar
                        .make(relativeLayout,"someUIErrorOccurred"+inErrorMessage.toString(), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            public void onTransactionResponse(Bundle inResponse) {

                progressBar.setVisibility(View.VISIBLE);
                Log.e("Transaction Response",inResponse.toString());
                String transactionStatus = inResponse.getString("STATUS");
                String checkSumHash = inResponse.getString("CHECKSUMHASH");
                if(transactionStatus.contentEquals("TXN_SUCCESS")){
                    handlePaymentResponse("success");
                }else{
                    handlePaymentResponse("error");
                }
                saveTransactionDetails(inResponse,"online");
            }

            public void networkNotAvailable() {
                Snackbar snackbar = Snackbar
                        .make(relativeLayout,"Network Unavailable", Snackbar.LENGTH_SHORT);
                snackbar.show();
                handlePaymentResponse("error");

            }
            public void clientAuthenticationFailed(String inErrorMessage) {
                Snackbar snackbar = Snackbar
                        .make(relativeLayout,"Client Authentication Failure", Snackbar.LENGTH_SHORT);
                snackbar.show();
                handlePaymentResponse("error");
            }
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                Snackbar snackbar = Snackbar
                        .make(relativeLayout,"Error Loading the Payment Page", Snackbar.LENGTH_SHORT);
                snackbar.show();
                handlePaymentResponse("error");
            }
            public void onBackPressedCancelTransaction() {
                handlePaymentResponse("error");
            }
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Snackbar snackbar = Snackbar
                        .make(relativeLayout,inErrorMessage.toString(), Snackbar.LENGTH_SHORT);
                snackbar.show();
                handlePaymentResponse("error");
            }
        });

    }

    private void saveTransactionDetails(Bundle inResponse,String paymentType){

        Log.e("Transaction Response",inResponse.toString());
        String transactionFinalCheckSumHash = inResponse.getString("CHECKSUMHASH");
        String bankName = inResponse.getString("BANKNAME");
        String orderID = inResponse.getString("ORDERID");
        String transactionAmount = inResponse.getString("TXNAMOUNT");
        String transactionTimeStamp = inResponse.getString("TXNDATE");
        String transactionID = inResponse.getString("TXNID");
        String paymentMode = inResponse.getString("PAYMENTMODE");
        String bankTransactionID = inResponse.getString("BANKTXNID");
        String currency = inResponse.getString("CURRENCY");
        String gatewayName = inResponse.getString("GATEWAYNAME");
        String transactionStatus = inResponse.getString("RESPMSG");


        JSONObject finalTransactionObject = new JSONObject();
        try {

            finalTransactionObject.put("transactionFinalCheckSumHash", transactionFinalCheckSumHash);
            finalTransactionObject.put("bankName", bankName);
            finalTransactionObject.put("orderID", orderID);
            finalTransactionObject.put("transactionAmount", transactionAmount);
            finalTransactionObject.put("transactionTimeStamp", transactionTimeStamp);
            finalTransactionObject.put("transactionID", transactionID);
            finalTransactionObject.put("paymentMode", paymentMode);
            finalTransactionObject.put("bankTransactionID", bankTransactionID);
            finalTransactionObject.put("currency", currency);
            finalTransactionObject.put("gatewayName", gatewayName);
            finalTransactionObject.put("transactionStatus", transactionStatus);
            finalTransactionObject.put("paymentType",paymentType);

            JSONObject userDetail = new JSONObject();
            userDetail.put("id",user.getId());
            finalTransactionObject.put("user",userDetail);

        }catch(Exception e){
            e.printStackTrace();
        }

        Log.e("JSON Response",finalTransactionObject.toString());
        AndroidNetworking.post(getString(R.string.domain_name)+getString(R.string.create_booking))
                .addJSONObjectBody(finalTransactionObject)
                .addHeaders("Authorization",token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Successful Transaction",response.toString());
                        sendEmail("subratpn@gmail.com","New Booking for "+service,
                                "<h3>New Booking From "+user.getUsername()+"</h3>" +
                                        "For "+service);
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("Error Transaction",error.getErrorBody());
                    }
                });

    }

    private void handlePaymentResponse(String paymentResponse){

        progressBar.setVisibility(View.VISIBLE);

        if(paymentResponse.contentEquals("success")){
            paymentStatusImage.setImageResource(R.drawable.success);
            transactionResponseView.setText("Payment Successful");
        }else {
            paymentStatusImage.setImageResource(R.drawable.error);
            transactionResponseView.setText("Payment Failed");
        }

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(PaymentActivity.this, BasicHomeActivity.class));
                finish();
            }
        }, 3000);

    }


    private void sendEmail(String receiver,String subject,String content){

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("subject", subject);
            jsonObject.put("receiver", receiver);
            jsonObject.put("content",getCustomerContent());

        }catch(Exception e){}

        AndroidNetworking.post(getString(R.string.domain_name)+getString(R.string.send_email))
                .addJSONObjectBody(jsonObject)
                .addHeaders("Authorization",token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Successful Email Sent",response.toString());
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e("Error Sending Email",error.toString());
                    }
                });


    }


    private String getCustomerContent(){
        String emailContent = "<link href=\"https://fonts.googleapis.com/css?family=Nunito&display=swap\" rel=\"stylesheet\">\n" +
                "<body style=\"font-family: 'Nunito', sans-serif; color:#1A237E\">\n" +
                "\t\n" +
                "\t<div>\n" +
                "\t\t<h4>Hi Team,</h4>\n" +
                "\t\t<p>Congrats..!! There is a new booking from 8861778546</p>\n" +
                "\t\t<p>Please find the booking details below</p>\n" +
                "\t\t<h5>Service : Electrician</h5>\n" +
                "\t\t<h5>Description : Booked/Day</h5>\n" +
                "\t\t<h5>Amount : Rs.500</h5>\n" +
                "\t\t<h5>Date & Time : 02.05.2019 4PM</h5>\n" +
                "\t\t<p>Regards,</p>\n" +
                "\t\t<p>Team Putatoe</p>\n" +
                "\t</div>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "</body>";
        return emailContent;
    }
}
