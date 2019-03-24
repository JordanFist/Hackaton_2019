package com.google.location.nearby.apps.rockpaperscissors;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SimpleArrayMap;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate.Status;
import com.google.android.gms.nearby.connection.Strategy;

/** Activity controlling the Rock Paper Scissors game */
public class MainActivity extends AppCompatActivity {


//    private final SimpleArrayMap<Long, Payload> incomingFilePayloads = new SimpleArrayMap<>();
//    private final SimpleArrayMap<Long, Payload> completedFilePayloads = new SimpleArrayMap<>();
//    private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();



    public ArrayList<AbstractPointOfInterest> ListPoint = new ArrayList<>();
    private static final String TAG = "RockPaperScissors";
    static BufferedReader reader;
    static BufferedWriter writer;
    private Payload receivedPayload;
    private File receivedFile;
    private Context context;
    private File file;
    private Uri uri;

    // Our handle to Nearby Connections
    private ConnectionsClient connectionsClient;

    // Our randomly generated name
    private final String codeName = CodenameGenerator.generate();

    private String opponentEndpointId;
    private String opponentName;

    private Button findOpponentButton;
    private Button disconnectButton;

    private TextView opponentText;
    private TextView statusText;

    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private static final Strategy STRATEGY = Strategy.P2P_STAR;

    private ArrayList<AbstractPointOfInterest> FileToList(File f) {
      ArrayList<AbstractPointOfInterest> tmp = new ArrayList<AbstractPointOfInterest>();
        try { // be sure that the file exist
            FileReader fstream = new FileReader(f); //true tells to append data.
            reader = new BufferedReader(fstream);
        } catch (FileNotFoundException e) {
            try {
                f.createNewFile();
                FileReader fstream = new FileReader(f); //true tells to append data.
                reader = new BufferedReader(fstream);
            } catch (IOException e1) {
                System.out.println("error");
            }
        }

        try {
            String text = reader.readLine();
            while (text != null) {
                String[] split = text.split("\t", -1);
                String[] split2 = split[0].split(",", -1);
                Point p = new Point(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]));
                switch (split[1]) {
                    case "SafeArea":
                        SafeArea area = new SafeArea(p, split[1], Integer.parseInt(split[3]));
                        tmp.add(area);
                        break;
                    case "WaterSpot":
                        WaterSpot water = new WaterSpot(p, split[1], Integer.parseInt(split[3]));
                        tmp.add(water);
                        break;
                }
                // read next line
                text = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    private void ListToFile() {
        for (AbstractPointOfInterest var : ListPoint) {
            String str = "";
            str += String.valueOf(var.getlocalisation().x) + "," + String.valueOf(var.getlocalisation().y) + "\t";
            str += var.gettype() + "\t";
            str += var.getDate() + "\t";
            str += var.parameters() + "\n";
            writeToFile(str, context);
        }
    }

    private void updateMyList(ArrayList<AbstractPointOfInterest> hisZones) {
        ArrayList<AbstractPointOfInterest> newList = new ArrayList<AbstractPointOfInterest>();
        int done = 0;
        int updated = 0;
        for (AbstractPointOfInterest var1 : ListPoint){
            for (AbstractPointOfInterest var2 : hisZones){
                if (var1.getlocalisation().equals(var2.getlocalisation())){
                    done = 1;
                    if (var1.getDate() < var2.getDate()){
                        updated = 1;
                        newList.add(var2);
                    } else {
                        newList.add(var1);
                    }
                }
            }
            if (done == 0) {
                newList.add(var1);
            }
            done = 0;
        }
        if (updated == 1) {
            file.delete();
            try {
                file.createNewFile();
            }
            catch (IOException e){
            }
            ListToFile();
        }
        int dont = 0;
        for (AbstractPointOfInterest var1 : hisZones){
            for (AbstractPointOfInterest var2 : newList) {
                if(var1.equals(var2)){
                   dont = 1;
                }
            }
            if (dont == 0){
                newList.add(var1);
            }
            dont=0;
        }

        ListPoint = newList;

        TextView firstPoint = findViewById(R.id.firstPoint);
        firstPoint.setText(ListPoint.get(0).gettype());

        TextView secondPoint = findViewById(R.id.secondPoint);
        secondPoint.setText(ListPoint.get(1).gettype());
    }

//    private long addPayloadFilename(String payloadFilenameMessage) {
//        String[] parts = payloadFilenameMessage.split(":");
//        long payloadId = Long.parseLong(parts[0]);
//        String filename = parts[1];
//        filePayloadFilenames.put(payloadId, filename);
//        return payloadId;
//    }

//    private void processFilePayload(long payloadId) {
//        // BYTES and FILE could be received in any order, so we call when either the BYTES or the FILE
//        // payload is completely received. The file payload is considered complete only when both have
//        // been received.
//        Payload filePayload = completedFilePayloads.get(payloadId);
//        String filename = filePayloadFilenames.get(payloadId);
//        if (filePayload != null && filename != null) {
//            completedFilePayloads.remove(payloadId);
//            filePayloadFilenames.remove(payloadId);
//
//            // Get the received file (which will be in the Downloads folder)
//            File payloadFile = filePayload.asFile().asJavaFile();
//
//            // Rename the file.
//            payloadFile.renameTo(new File(payloadFile.getParentFile(), filename));
//        }
//    }


    // Callbacks for receiving payloads
    private final PayloadCallback payloadCallback =
        new PayloadCallback() {
            @Override
            public void onPayloadReceived(String endpointId, Payload payload) {
//                try {
//                    Payload p2 = payload;
                  receivedFile =  payload.asFile().asJavaFile();
//                    ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r");
//                    receivedPayload = Payload.fromFile(pfd);
//                  ParcelFileDescriptor pfd = payload.asStream().asParcelFileDescriptor();
//                  receivedPayload = Payload.fromFile(pfd);
//                }catch (FileNotFoundException e){}
            }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
//            File payloadFile = receivedPayload.asFile().asJavaFile();
            ArrayList<AbstractPointOfInterest> hisZones = FileToList(receivedFile);
            updateMyList(hisZones);
        }
    };

    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    Log.i(TAG, "onEndpointFound: endpoint found, connecting");
                    connectionsClient.requestConnection(codeName, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {
                }
            };

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("zones.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback() {
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    opponentName = connectionInfo.getEndpointName();
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");

                        connectionsClient.stopDiscovery();
                        connectionsClient.stopAdvertising();

                        opponentEndpointId = endpointId;
                        setOpponentName(opponentName);
                        setStatusText(getString(R.string.status_connected));
                        setButtonState(true);
                        sendZones(opponentEndpointId);
                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                    resetConnection();
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        findOpponentButton = findViewById(R.id.find_opponent);
        disconnectButton = findViewById(R.id.disconnect);

        opponentText = findViewById(R.id.opponent_name);
        statusText = findViewById(R.id.status);

        TextView nameView = findViewById(R.id.name);
        nameView.setText(getString(R.string.codename, codeName));

        context = getApplicationContext();
        String filePath = context.getFilesDir() + "/" + "zones.txt";
        System.out.println( context.getFilesDir());
        System.out.println( "content:/" +context.getFilesDir());
        uri = Uri.parse("content:/" + context.getFilesDir());

        file = new File(filePath);
        if(!file.exists()){
            try {
                file.createNewFile();
            }
            catch (IOException e){
            }
        }

        writeToFile("1,5\tSafeArea\t100\t1\n", context);
        reader = null;
        writer = null;

        ListPoint = FileToList(file);

        TextView firstPoint = findViewById(R.id.firstPoint);
        firstPoint.setText(ListPoint.get(0).gettype());

        TextView secondPoint = findViewById(R.id.secondPoint);
        secondPoint.setText("undefined");

        connectionsClient = Nearby.getConnectionsClient(this);

        resetConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    @Override
    protected void onStop() {
        connectionsClient.stopAllEndpoints();
        resetConnection();
        super.onStop();
    }

    /**
     * Returns true if the app was granted all the permissions. Otherwise, returns false.
     */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Handles user acceptance (or denial) of our permission request.
     */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }
        recreate();
    }

    /**
     * Finds an opponent to play the game with using Nearby Connections.
     */
    public void findOpponent(View view) {
        startAdvertising();
        startDiscovery();
        setStatusText(getString(R.string.status_searching));
        findOpponentButton.setEnabled(false);
    }

    /**
     * Disconnects from the opponent and reset the UI.
     */
    public void disconnect(View view) {
        connectionsClient.disconnectFromEndpoint(opponentEndpointId);
        resetConnection();
    }

    /**
     * Starts looking for other players using Nearby Connections.
     */
    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startDiscovery(
                getPackageName(), endpointDiscoveryCallback,
                new DiscoveryOptions.Builder().setStrategy(STRATEGY).build());
    }

    /**
     * Broadcasts our presence using Nearby Connections so other players can find us.
     */
    private void startAdvertising() {
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startAdvertising(
                codeName, getPackageName(), connectionLifecycleCallback,
                new AdvertisingOptions.Builder().setStrategy(STRATEGY).build());
    }

    /**
     * Wipes all game state and updates the UI accordingly.
     */
    private void resetConnection() {
        opponentEndpointId = null;
        opponentName = null;

        setOpponentName(getString(R.string.no_opponent));
        setStatusText(getString(R.string.status_disconnected));
        setButtonState(false);
    }

    /**
     * Sends the user's selection of rock, paper, or scissors to the opponent.
     */
    private void sendZones(String opponentEndpointId) {
        Payload p;
        try {
            //    myChoice = choice;
            //    connectionsClient.sendPayload(
            //        opponentEndpointId, Payload.fromBytes(choice.name().getBytes(UTF_8)));
            p = Payload.fromFile(file);
            Payload.File f = p.asFile();
            File fjava = f.asJavaFile();
            connectionsClient.sendPayload(
                    opponentEndpointId, p);
        } catch (FileNotFoundException e) {
            System.out.print("File not Found !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        setStatusText(getString(R.string.game_choice, "Zones sent"));
        // No changing your mind!
    }

    /**
     * Enables/disables buttons depending on the connection status.
     */
    private void setButtonState(boolean connected) {
        findOpponentButton.setEnabled(true);
        findOpponentButton.setVisibility(connected ? View.GONE : View.VISIBLE);
        disconnectButton.setVisibility(connected ? View.VISIBLE : View.GONE);
    }

    /**
     * Shows a status message to the user.
     */
    private void setStatusText(String text) {
        statusText.setText(text);
    }

    /**
     * Updates the opponent name on the UI.
     */
    private void setOpponentName(String opponentName) {
        opponentText.setText(getString(R.string.opponent_name, opponentName));
    }
}
