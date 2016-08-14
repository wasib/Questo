package com.tdevelopers.questo.Objects;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.facebook.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tdevelopers.questo.MyApplication;
import com.tdevelopers.questo.Pushes.GcmServerSideSender;
import com.tdevelopers.questo.Pushes.LoggingService;
import com.tdevelopers.questo.Pushes.Message;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyData {

    final public static String apiname = "private";
    public static int type = 0;
    public static Context context = MyApplication.getInstance().getApplicationContext();
    public static HashSet<String> tags = new HashSet<>();
public  static  AVLoadingIndicatorView avl;
    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(init);


        while (matcher.find()) {
            String word = matcher.group();
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString().trim();
    }
public static void  setLoader(AVLoadingIndicatorView avl)

{
    MyData.avl=avl;
}

    public static AVLoadingIndicatorView getLoader() {
        return avl;
    }

    public static void pushNotification(String title, String body, final String to, final String link, String userid) {
        context = MyApplication.getInstance().getApplicationContext();

        if (context != null ) {
            final Message.Builder messageBuilder = new Message.Builder();
            messageBuilder.notificationTitle(title + "");
            messageBuilder.notificationBody(Profile.getCurrentProfile().getName() + " " + body);
            //Date date = new Date(System.currentTimeMillis());


            Date date = new Date(System.currentTimeMillis());

            messageBuilder.addData("created_at", (date.getTime() + ""));
            messageBuilder.addData("userid", (Profile.getCurrentProfile().getId()));
            messageBuilder.addData("username", (Profile.getCurrentProfile().getName()));
            messageBuilder.addData("link", link);

            DatabaseReference n = FirebaseDatabase.getInstance().getReference("notifications").child(userid + "").push();
            n.child("userid").setValue(Profile.getCurrentProfile().getId());
            n.child("title").setValue(title);
            n.child("message").setValue(Profile.getCurrentProfile().getName() + " " + body);
            n.child("created_at").setValue(date.getTime());
            n.child("username").setValue(Profile.getCurrentProfile().getName());
            n.child("link").setValue(link);
            n.setPriority(date.getTime() * -1L);
            final LoggingService.Logger mLogger = new LoggingService.Logger(context);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {

                    GcmServerSideSender sender = new GcmServerSideSender(apiname, mLogger);


                    try {
                        sender.sendHttpJsonDownstreamMessage(to, messageBuilder.build());

                    } catch (final IOException e) {
                        return e.getMessage();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (result != null) {
                    }
                }
            }.execute();

        }
    }

    public static boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        context = MyApplication.getInstance().getApplicationContext();
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            return haveConnectedWifi || haveConnectedMobile;
        }
        return true;
    }

    public static int getType() {
        return type;
    }

    public static void setType(int type) {
        MyData.type = type;
    }

    public static HashSet<String> getTags() {
        return tags;
    }

    public static void setTags(HashSet<String> tags) {
        MyData.tags = tags;
    }

}
