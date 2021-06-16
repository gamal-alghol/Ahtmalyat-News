package com.karkor.ahtmalyat.Fragment;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karkor.ahtmalyat.Adabter.MsgAdabter;
import com.karkor.ahtmalyat.Model.Msg;
import com.karkor.ahtmalyat.Model.User;
import com.karkor.ahtmalyat.R;
import com.karkor.ahtmalyat.Service.UploadService;
import com.karkor.ahtmalyat.View.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;


public class Chat extends Fragment {
    ImageButton btnSend, gallary, camera, imageButtonMic,audio;
    public  static  ImageButton btnImage;
    EditText editTextMsg;
    RecyclerView recyclerView;
    ArrayList<Msg> msgArrayList;
    ProgressBar bar,barUploadAudio;;
    public  static  ProgressBar barUpload;
    public  static   TextView txtProgressUpload,txtProgressUploadAudio;
    Msg msgDeleteOrReplay;
    CardView pupUp;
    StorageReference storageReference;
    String mediaPath, pathRecord, recordUri;
    private static final int REQUEST_PICK_AUDIO = 102;
    private int currentFormat = 0;
    private MediaRecorder recorder;
    //  private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private static final String AUDIO_RECORDER_FOLDER = "Ahtymalyat record";
    int REQUEST_PICK_PHOTO = 100;
    private static final int MY_CAMERA_PERMISSION_CODE = 101;
    LinearLayoutManager layoutManager;
    int limitMsgs = 8;
    MsgAdabter messagesAdapter;
    SwipeRefreshLayout refreshLayout;
    View rootView;
    Uri mediaUri;
    PopupWindow pw;
    public static TextView txtReplay, senderReplay;
    public static LinearLayout msgReplay;
    boolean isFirst = true;
    public static Bitmap bmpScale;
    File output = null;
    ;
    public static String imagePath;
    ItemTouchHelper.SimpleCallback simpleCallback;
    public static Boolean  notfay = false;

    public Chat() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.rootView = view;
        super.onViewCreated(view, savedInstanceState);

        btnSend = view.findViewById(R.id.send);
        editTextMsg = view.findViewById(R.id.edit_text_msg);
        recyclerView = view.findViewById(R.id.list_chat);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        btnImage = view.findViewById(R.id.btn_get_image);
        bar = view.findViewById(R.id.progressBar3);
        barUpload=view.findViewById(R.id.upLoad_file);
        barUploadAudio=view.findViewById(R.id.upLoad_file_audio);
        txtProgressUploadAudio=view.findViewById(R.id.txt_progress_upload_audio);
        txtProgressUpload=view.findViewById(R.id.txt_progress_upload);
        msgReplay = view.findViewById(R.id.msg_replay);
        txtReplay = view.findViewById(R.id.txt_replay);
        senderReplay = view.findViewById(R.id.sender_replay);
     //   View v = LayoutInflater.from(getContext()).inflate(R.layout.pup_up_menu, null, false);
        gallary = view.findViewById(R.id.gallary);
        audio=view.findViewById(R.id.audio);
        camera = view.findViewById(R.id.camera);
        imageButtonMic = view.findViewById(R.id.imageButton_mic);
        pupUp=view.findViewById(R.id.card_view_pupUp);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
       // pw = new PopupWindow(v, 580, 210, true);

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio "), REQUEST_PICK_AUDIO);
            }
        });
        imageButtonMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    if (ContextCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) rootView.getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                    if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((Activity) rootView.getContext(), new String[]{Manifest.permission.RECORD_AUDIO}, 10);

                    } else {
                        zoomIn();
                        startRecording();
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    zoomOut();
                    stopRecording();
                }
                return true;
            }
        });
    }

    private void zoomIn() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageButtonMic,
                "scaleX", 3f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageButtonMic,
                "scaleY", 3f);
        scaleDownX.setDuration(1000);
        scaleDownY.setDuration(1000);
        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.start();
    }

    private void zoomOut() {
        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(
                imageButtonMic, "scaleX", 1f);
        ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(
                imageButtonMic, "scaleY", 1f);
        scaleDownX2.setDuration(1000);
        scaleDownY2.setDuration(1000);
        AnimatorSet scaleDown2 = new AnimatorSet();
        scaleDown2.play(scaleDownX2).with(scaleDownY2);
        scaleDown2.start();
    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
            Log.d("ttt", file.getName());

        }
        pathRecord = System.currentTimeMillis() + ".mp3";
        recordUri = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3";
        return (recordUri);
    }

    private void startRecording() {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(getFilename());
        isFirst = false;
        try {
            recorder.prepare();
            recorder.start();
            Toast.makeText(rootView.getContext(), "بدأ التسجيل", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(rootView.getContext(), "حدث خطا في التسجيل", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }

        //


    }


    private void stopRecording() {

        if (isFirst != true) {
            if (recorder != null) {
                try {
                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                    recorder = null;
                    Toast.makeText(rootView.getContext(), "انتهاء التسجيل", Toast.LENGTH_LONG).show();
                    Uri uriAudio = Uri.fromFile(new File(recordUri).getAbsoluteFile());
                   storageReference= FirebaseStorage.getInstance().getReference().child("chatAudio");
                        storageReference.child(pathRecord).putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("ttt",storageReference.getDownloadUrl()+"");
                                  storageReference.child(pathRecord).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Uri> task) {
                                         if(task.isSuccessful()){
                                             imageButtonMic.setVisibility(View.VISIBLE);
                                             txtProgressUploadAudio.setVisibility(View.INVISIBLE);
                                             barUploadAudio.setProgress(0);
                                             SendMsgs(pathRecord, String.valueOf(task.getResult()));

                                         }

                                      }
                                  }).addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Log.d("ttt",e.getMessage());
                                      }
                                  }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("ttt","123456");

                                }
                            });

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            txtProgressUploadAudio.setVisibility(View.VISIBLE);
                            imageButtonMic.setVisibility(View.INVISIBLE);
                            txtProgressUploadAudio.setText( String.valueOf((int)((100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount()))+"%");
                            barUploadAudio.setProgress((int)((100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount()));
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(rootView.getContext(), "حدث خطا في التسجيل", Toast.LENGTH_LONG).show();
                    Log.d("ttt", e.getMessage() + "       2321");
                }


            }
        }
    }

    private void SendTextMsg() {
        notfay = true;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextMsg.getText().toString().equals("") && editTextMsg.getText().toString() != null) {
                    SendMsgs(editTextMsg.getText().toString(),"");
                    editTextMsg.setText("");
                    txtReplay.setText("");
                    txtReplay.setVisibility(View.GONE);
                }

            }
        });
    }
/*
    private void upload(String nameFile) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaPath = System.currentTimeMillis() + "." + getFileExtension(mediaUri);
                storageReference = FirebaseStorage.getInstance().getReference().child(nameFile)
                        .child(mediaPath);

                storageReference.putFile(mediaUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                SendMsgs(mediaPath, String.valueOf(uri));
                                btnImage.setVisibility(View.VISIBLE);
                                txtProgressUpload.setVisibility(View.INVISIBLE);
                                barUpload.setProgress(0);
                                Toast.makeText(rootView.getContext(), "تم الارسال", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }}).
                        addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                barUpload.setProgress(0);
                                btnImage.setVisibility(View.VISIBLE);
                                txtProgressUpload.setVisibility(View.INVISIBLE);
                                Log.d("ttt", e.getMessage());
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        txtProgressUpload.setVisibility(View.VISIBLE);
                        btnImage.setVisibility(View.INVISIBLE);
                        txtProgressUpload.setText( String.valueOf((int)((100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount()))+"%");
                        barUpload.setProgress((int)((100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount()));
                    }
                });
            }
        }).start();

    }*/

    private void SendMsgs(String msg,String uri) {

        Msg message = new Msg(FirebaseAuth.getInstance().getCurrentUser().getUid(),

                msg,
                txtReplay.getText().toString(),
                senderReplay.getText().toString(),uri, getCurrentDate());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("chat").push().setValue(message);
        msgReplay.setVisibility(View.GONE);
        txtReplay.setText("");
        senderReplay.setText("");
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    notfay = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMessages();
        openGallaryOrCamera();
        openGllary();
        openCamera();
        SendTextMsg();
        try {
            msgArrayList = new ArrayList<>();
            layoutManager = new LinearLayoutManager(rootView.getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

  messagesAdapter = new MsgAdabter(msgArrayList, rootView.getContext());
            recyclerView.setAdapter(messagesAdapter);

        } catch (IndexOutOfBoundsException v) {
            Log.d("ttt", v.getMessage());
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setEnabled(false);
                msgArrayList.clear();
                messagesAdapter.notifyDataSetChanged();
                getMessages();


            }
        });

        simpleCallback = new ItemTouchHelper.SimpleCallback(5, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_reply_24).addSwipeRightActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX / 2, dY, actionState, isCurrentlyActive);
            }


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                msgDeleteOrReplay= msgArrayList.get(viewHolder.getAdapterPosition());
                if (direction == ItemTouchHelper.RIGHT) {
                    if(msgDeleteOrReplay.getSender().equalsIgnoreCase(FirebaseAuth.getInstance().getUid())){
                        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("chat");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for( DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                    if(msgDeleteOrReplay.getMessage().equalsIgnoreCase(dataSnapshot1.getValue(Msg.class).getMessage())){
                                        reference.child( dataSnapshot1.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                for (int i=0;i<msgArrayList.size();i++){
                                                    if(msgArrayList.get(i).getMessage().equalsIgnoreCase(msgDeleteOrReplay.getMessage())){
                                                        msgArrayList.remove(i);
                                                        messagesAdapter.notifyDataSetChanged();
                                                        break;
                                                    }
                                                }
                                                Toast.makeText(getContext(), "تم حذف الرسالة" ,Toast.LENGTH_SHORT).show();
                                                messagesAdapter.notifyDataSetChanged();

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        Toast.makeText(getContext(), "لا ايمكن حذف الرسالة شخص اخر" ,Toast.LENGTH_SHORT).show();
                        messagesAdapter.notifyDataSetChanged();
                    }
                }
                if (direction == ItemTouchHelper.LEFT){
                    msgReplay.setVisibility(View.VISIBLE);

                    txtReplay.setText(msgDeleteOrReplay.getMessage());
                    FirebaseDatabase.getInstance().getReference("users").child(msgDeleteOrReplay.getSender()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final User user = dataSnapshot.getValue(User.class);
                            senderReplay.setText(user.getName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    messagesAdapter.notifyDataSetChanged();
                }



            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    private void openCamera() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) rootView.getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                if (ContextCompat.checkSelfPermission(getView().getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) rootView.getContext(), new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    ContentValues contentValues=new ContentValues();
                    contentValues.put(MediaStore.Images.Media.TITLE,"صورة جديدة");
                    mediaUri= Home.cc.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,mediaUri);
                    startActivityForResult(intent, MY_CAMERA_PERMISSION_CODE);
                }
            }




        });
    }

    private void openGllary() {
        gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });
    }

    private void openGallaryOrCamera() {
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pupUp.getVisibility()==View.VISIBLE){
                    pupUp.setVisibility(View.GONE);

                } else if(pupUp.getVisibility()==View.GONE){
                    pupUp.setVisibility(View.VISIBLE);

                }
         //       pw.showAtLocation(rootView.findViewById(R.id.chat_menu), Gravity.BOTTOM, -150, 260);
            }
        });
    }

    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(rootView.getContext().getContentResolver().getType(imageUri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pupUp.setVisibility(View.GONE);

        if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mediaUri = data.getData();
            mediaPath = System.currentTimeMillis() + "." + getFileExtension(mediaUri);
            Toast.makeText(rootView.getContext(), "تحميل الملف...", Toast.LENGTH_SHORT).show();

            if (mediaUri != null) {
                if (mediaUri.toString().contains("image")) {
                    Intent intent=new Intent(   getActivity(), UploadService.class);
                    intent.putExtra("mediaPath",mediaPath);
                    intent.putExtra("mediaUri",mediaUri.toString());
                    intent.putExtra("fileStorage","chatImage");
                    Log.d("ttt",intent.toString());
                    getActivity().startService(intent);
                } else if (mediaUri.toString().contains("video")) {
                    Intent intent=new Intent(   getActivity(), UploadService.class);
                    intent.putExtra("mediaPath",mediaPath);
                    intent.putExtra("mediaUri",mediaUri.toString());
                    intent.putExtra("fileStorage","chatVideo");

                    getActivity().startService(intent);
                   // upload("chatVideo");

                }
            }
        }else if(requestCode == REQUEST_PICK_AUDIO && resultCode == RESULT_OK && data != null && data.getData() != null){
            mediaUri = data.getData();
            mediaPath = System.currentTimeMillis() + "." + getFileExtension(mediaUri);
            Intent intent=new Intent(getActivity(), UploadService.class);
            Log.d("ttt",intent.toString());
            intent.putExtra("mediaPath",mediaPath);
            intent.putExtra("mediaUri",mediaUri.toString());
            intent.putExtra("fileStorage","chatAudio");

            getActivity().startService(intent);
          // upload("chatAudio");
        }
        else if (requestCode == MY_CAMERA_PERMISSION_CODE & resultCode == RESULT_OK) {

            mediaPath = System.currentTimeMillis() + "." + getFileExtension(mediaUri);
            Intent intent=new Intent( getActivity(), UploadService.class);
            intent.putExtra("mediaPath",mediaPath);
            intent.putExtra("mediaUri",mediaUri.toString());
            intent.putExtra("fileStorage","chatImage");
            getActivity().startService(intent);
           // upload("chatImage");


        }
    }


    private void getMessages() {
        FirebaseDatabase.getInstance().getReference("chat").orderByChild("time")
                .limitToLast(limitMsgs).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        limitMsgs = limitMsgs + 1;
                        Msg message = dataSnapshot.getValue(Msg.class);
                        msgArrayList.add(message);
                        messagesAdapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                        bar.setVisibility(View.GONE);
                        recyclerView.scrollToPosition(msgArrayList.size() - 1);
                        recyclerView.setEnabled(true);
                    }
                }).run();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}