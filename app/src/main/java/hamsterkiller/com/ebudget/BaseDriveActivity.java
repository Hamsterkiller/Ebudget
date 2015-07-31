package hamsterkiller.com.ebudget;

import android.app.Activity;
import android.util.Log;
import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class BaseDriveActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    // constants
    private static final String TAG = "BaseDriveActivity";
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_DUMP_MADE = 2;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;
    // object of EbudgetDBmanager
    final EbudgetDBManager dbmngr = EbudgetDBManager.getInstance(this);
    // Google API client link
    GoogleApiClient mGoogleApiClient;
    // Link to file that will be exported
    private DriveFile dumpFileLink;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drive);
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    // Google API methods for Drive API. Possibly I misuused this method in some sort. NEED TO
    // RESEARCH ABOUT this method and how it is used.
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "API client connected.");
        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("ebudgetDump")
                        .setMimeType("text/csv")
                        .setStarred(true).build();

                // Create an empty file on root folder.
                /*Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        //.createFile(mGoogleApiClient, changeSet, null /* DriveContents *///)
                        //.setResultCallback(fileCallback);
                /*DriveId id = Drive.DriveApi.getAppFolder(mGoogleApiClient).getDriveId();
                dumpFileLink = Drive.DriveApi.getFile(mGoogleApiClient, id);
                editDriveFile(changeSet);*/
            }
        }.start();

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }

    }


    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }


    private boolean editDriveFile(MetadataChangeSet metadataChangeSet){
        // array of templates for auto-completing
        try {
            dbmngr.open();
        } catch (SQLException e) {

            e.printStackTrace();
        }

        String dumpContents = dbmngr.backUpDB();
        try {
            DriveContentsResult driveContentsResult = dumpFileLink.open(
                    mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
                    if (!driveContentsResult.getStatus().isSuccess()) {
                        return false;
                    }

            DriveContents driveContents = driveContentsResult.getDriveContents();
            OutputStream outputStream = driveContents.getOutputStream();
            outputStream.write(dumpContents.getBytes());

            com.google.android.gms.common.api.Status status =
                    driveContents.commit(mGoogleApiClient, metadataChangeSet).await();
            return status.getStatus().isSuccess();
        } catch (IOException e) {
            Log.e(TAG, "IOException while appending to the output stream", e);
        }
        return false;
    }


    /**
     * Create a new dump of Ebudget Database and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Loading new contents.");
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {

                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        // If the operation was not successful, we cannot do anything
                        // and must
                        // fail.
                        if (!result.getStatus().isSuccess()) {
                            Log.i(TAG, "Failed to create new contents.");
                            return;
                        }

                        // Create the initial metadata - MIME type and title.
                        // Note that the user will be able to change the title later.
                        MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().
                                setMimeType("text/csv").setTitle("ebudgetDBDump.csv").build();


                        // Create an intent for the file chooser, and start it.
                        IntentSender intentSender = Drive.DriveApi
                                .newCreateFileActivityBuilder()
                                .setInitialMetadata(metadataChangeSet)
                                .setInitialDriveContents(result.getDriveContents())
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(
                                    intentSender, 1, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "Failed to launch file chooser.");
                        }
                    }
                });



    }
    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
