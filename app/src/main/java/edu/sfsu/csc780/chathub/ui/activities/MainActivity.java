/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sfsu.csc780.chathub.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.sfsu.csc780.chathub.R;
import edu.sfsu.csc780.chathub.model.APIKeys;
import edu.sfsu.csc780.chathub.model.ChatMessage;
import edu.sfsu.csc780.chathub.model.ChathubSinchClientListener;
import edu.sfsu.csc780.chathub.ui.utils.AudioUtil;
import edu.sfsu.csc780.chathub.ui.utils.ChannelUtil;
import edu.sfsu.csc780.chathub.ui.utils.MessageUtil;
import edu.sfsu.csc780.chathub.ui.utils.DesignUtils;
import edu.sfsu.csc780.chathub.ui.utils.MapLoader;
import edu.sfsu.csc780.chathub.ui.utils.LocationUtils;
import edu.sfsu.csc780.chathub.ui.fragments.ImageDialogFragment;
import edu.sfsu.csc780.chathub.ui.utils.UserUtil;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        MessageUtil.MessageLoadListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_PICK_IMAGE = 1;
    public static final int REQUEST_PREFERENCES = 2;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private static final int REQUEST_NEW_CHANNEL = 4;
    private static final int REQUEST_SEARCH = 5;
    public static final int MSG_LENGTH_LIMIT = 64;
    private static final double MAX_LINEAR_DIMENSION = 500.0;
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;

    private FloatingActionButton mSendButton;
    private RecyclerView mMessageRecyclerView;
    private RecyclerView mNavRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private NavigationView mNavigationView;
    private RelativeLayout mChannelAdd;

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseRecyclerAdapter<ChatMessage, MessageUtil.MessageViewHolder>
            mFirebaseAdapter;

    private SinchClient mSinchClient;
    private Call call;
    private boolean canCall = true;

    private Toolbar mToolBar;
    private ImageButton mImageButton;
    private ImageButton mPhotoButton;
    private DrawerLayout mDrawerLayout;
    private int mSavedTheme;
    private String mCurrentChannel;
    private ImageButton mLocationButton;
    private View.OnClickListener mImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageView photoView =  (ImageView) v.findViewById(R.id.messageImageView);
            // Only show the larger view in dialog if there's a image for the message
            if (photoView.getVisibility() == View.VISIBLE) {
                Bitmap bitmap = ((GlideBitmapDrawable) photoView.getDrawable()).getBitmap();
                showPhotoDialog( ImageDialogFragment.newInstance(bitmap));
            }
        }
    };

    private View.OnClickListener mChannelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Change channel here
            TextView channel = (TextView) view.findViewById(R.id.channelNameText);
            String channelName = "";
            if(channel == null) {
                channel = (TextView) view.findViewById(R.id.username);
                channelName = channel.getText().toString()+"="+mSharedPreferences.getString("username", "anonymous");
            } else {
                channelName = channel.getText().toString();
            }
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putString("currentChannel", channelName);
            edit.apply();
            setChannelPage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DesignUtils.applyColorfulTheme(this);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        //Initialize Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mUser.getDisplayName();
            if (mUser.getPhotoUrl() != null) {
                mPhotoUrl = mUser.getPhotoUrl().toString();
            }
        }

        AudioUtil.startAudioListener(this);

        mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext())
                .applicationKey(APIKeys.SINCH_API_KEY)
                .applicationSecret(APIKeys.SINCH_APP_SECRET)
                .environmentHost("sandbox.sinch.com")
                .userId(mSharedPreferences.getString("username", "anonymous"))
                .build();
        mSinchClient.setSupportCalling(true);

        mCurrentChannel = mSharedPreferences.getString("currentChannel", "general");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mChannelAdd = (RelativeLayout) findViewById(R.id.channelAdd);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        mFirebaseAdapter = MessageUtil.getFirebaseAdapter(this,
                this,  /* MessageLoadListener */
                mLinearLayoutManager,
                mMessageRecyclerView,
                mImageClickListener);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MSG_LENGTH_LIMIT)});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (FloatingActionButton) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send messages on click.
                mMessageRecyclerView.scrollToPosition(0);
                ChatMessage chatMessage = new
                        ChatMessage(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl, mCurrentChannel);
                MessageUtil.send(chatMessage, MainActivity.this);
                mMessageEditText.setText("");
            }
        });


        mImageButton = (ImageButton) findViewById(R.id.shareImageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        mPhotoButton = (ImageButton) findViewById(R.id.cameraButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePhotoIntent();
            }
        });

        mLocationButton = (ImageButton) findViewById(R.id.locationButton);
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMap();
            }
        });

        mChannelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChannelSearchActivity.class);
                startActivityForResult(intent, REQUEST_NEW_CHANNEL);
            }
        });

        SearchView jumpSearchView = (SearchView) findViewById(R.id.jumpSearch);
        jumpSearchView.setIconified(false);
        jumpSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Not implemented", Toast.LENGTH_SHORT).show();
            }
        });

        mNavRecyclerView = (RecyclerView) findViewById(R.id.navRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mNavRecyclerView.setLayoutManager(linearLayoutManager);
        mNavRecyclerView.setAdapter(ChannelUtil.getFirebaseAdapterForUserChannelList(this, mChannelClickListener));

        RecyclerView userListRecyclerView = (RecyclerView) findViewById(R.id.userListRecyclerView);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        userListRecyclerView.setLayoutManager(linearLayoutManager2);
        userListRecyclerView.setAdapter(UserUtil.getFirebaseAdapterForUserList(mChannelClickListener));

        Button voiceCallButton = (Button) findViewById(R.id.voiceCall);
        voiceCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallClient callClient = mSinchClient.getCallClient();
                if(canCall) {
                    call = callClient.callConference("General");
                    call.addCallListener(new CallListener() {
                        @Override
                        public void onCallProgressing(Call call) {
                            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                            Log.d("Call", "Call progressing");
                        }

                        @Override
                        public void onCallEstablished(Call call) {
                            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                            Log.d("Call", "Calling now");
                        }

                        @Override
                        public void onCallEnded(Call call) {
                            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
                            Log.d("Call", "Stopped calling");
                        }

                        @Override
                        public void onShouldSendPushNotification(Call call, List<PushPair> list) {
                            Log.d("Call", "Push");
                        }
                    });
                }
            }
        });

        Button endCallButton = (Button) findViewById(R.id.endCall);
        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(call != null) {
                    call.hangup();
                    call = null;
                }
            }
        });
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure the implicit intent can be handled
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void setChannelPage() {
        mCurrentChannel = mSharedPreferences.getString("currentChannel", "general");
        mFirebaseAdapter = MessageUtil.getFirebaseAdapter(MainActivity.this,
                MainActivity.this,  /* MessageLoadListener */
                mLinearLayoutManager,
                mMessageRecyclerView,
                mImageClickListener);
        mMessageRecyclerView.swapAdapter(mFirebaseAdapter, false);

        mNavRecyclerView.swapAdapter(ChannelUtil.getFirebaseAdapterForUserChannelList(this, mChannelClickListener), false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
        mSinchClient.stopListeningOnActiveConnection();
        mSinchClient.terminate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSinchClient.addSinchClientListener(new ChathubSinchClientListener());
        mSinchClient.start();
        LocationUtils.startLocationUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        boolean isGranted = (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        if (isGranted && requestCode == LocationUtils.REQUEST_CODE) {
            LocationUtils.startLocationUpdates(this);
        } else if(!isGranted || requestCode != AudioUtil.REQUEST_CODE) {
            canCall = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.preferences_menu:
                mSavedTheme = DesignUtils.getPreferredTheme(this);
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivityForResult(i, REQUEST_PREFERENCES);
                return true;
            case R.id.search:
                Intent intent = new Intent(this, MessageSearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadComplete() {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void pickImage() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened"
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        intent.setType("image/*");

        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: request=" + requestCode + ", result=" + resultCode);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // Process selected image here
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (data != null) {
                Uri uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());

                // Resize if too big for messaging
                Bitmap bitmap = getBitmapForUri(uri);
                Bitmap resizedBitmap = scaleImage(bitmap);
                if (bitmap != resizedBitmap) {
                    uri = savePhotoImage(resizedBitmap);
                }

                createImageMessage(uri);
            } else {
                Log.e(TAG, "Cannot get image for uploading");
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            if (data != null && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Log.d(TAG, "imageBitmap size:" + imageBitmap.getByteCount());
                createImageMessage(savePhotoImage(imageBitmap));
            } else {
                Log.e(TAG, "Cannot get photo URI after taking photo");
            }
        } else if (requestCode == REQUEST_PREFERENCES) {
            if (DesignUtils.getPreferredTheme(this) != mSavedTheme) {
                DesignUtils.applyColorfulTheme(this);
                this.recreate();
            }
        } else if (requestCode == REQUEST_NEW_CHANNEL && resultCode == Activity.RESULT_OK) {
            setChannelPage();
        }
    }

    private void createImageMessage(Uri uri) {
        if (uri == null) {
            Log.e(TAG, "Could not create image message with null uri");
            return;
        }

        final StorageReference imageReference = MessageUtil.getImageStorageReference(mUser, uri);
        UploadTask uploadTask = imageReference.putFile(uri);

        // Register observers to listen for when task is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Failed to upload image message");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ChatMessage chatMessage = new
                        ChatMessage(mMessageEditText.getText().toString(),
                        mUsername,
                        mPhotoUrl, mCurrentChannel, imageReference.toString());
                MessageUtil.send(chatMessage, MainActivity.this);
                mMessageEditText.setText("");
            }
        });
    }

    private Bitmap getBitmapForUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap scaleImage(Bitmap bitmap) {
        int originalHeight = bitmap.getHeight();
        int originalWidth = bitmap.getWidth();
        double scaleFactor =  MAX_LINEAR_DIMENSION / (double)(originalHeight + originalWidth);

        // We only want to scale down images, not scale upwards
        if (scaleFactor < 1.0) {
            int targetWidth = (int) Math.round(originalWidth * scaleFactor);
            int targetHeight = (int) Math.round(originalHeight * scaleFactor);
            return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
        } else {
            return bitmap;
        }
    }

    private Uri savePhotoImage(Bitmap imageBitmap) {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile == null) {
            Log.d(TAG, "Error creating media file");
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(photoFile);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return Uri.fromFile(photoFile);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String imageFileNamePrefix = "chathub-" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(
                imageFileNamePrefix,    /* prefix */
                ".jpg",                 /* suffix */
                storageDir              /* directory */
        );
        return imageFile;
    }


    private void loadMap() {
        Loader<Bitmap> loader = getSupportLoaderManager().initLoader(0, null, new LoaderManager
                .LoaderCallbacks<Bitmap>() {
            @Override
            public Loader<Bitmap> onCreateLoader(final int id, final Bundle args) {
                return new MapLoader(MainActivity.this);
            }

            @Override
            public void onLoadFinished(final Loader<Bitmap> loader, final Bitmap result) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                mLocationButton.setEnabled(true);

                if (result == null) return;
                // Resize if too big for messaging
                Bitmap resizedBitmap = scaleImage(result);
                Uri uri = null;
                if (result != resizedBitmap) {
                    uri = savePhotoImage(resizedBitmap);
                } else {
                    uri = savePhotoImage(result);
                }
                createImageMessage(uri);

            }

            @Override
            public void onLoaderReset(final Loader<Bitmap> loader) {
            }

        });

        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mLocationButton.setEnabled(false);
        loader.forceLoad();
    }

    void showPhotoDialog(DialogFragment dialogFragment) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) { ft.remove(prev); }
        ft.addToBackStack(null);

        dialogFragment.show(ft, "dialog");
    }
}
