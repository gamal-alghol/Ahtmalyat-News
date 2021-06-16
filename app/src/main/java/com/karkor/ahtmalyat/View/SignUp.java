package com.karkor.ahtmalyat.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karkor.ahtmalyat.Model.User;
import com.karkor.ahtmalyat.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUp extends AppCompatActivity {
    private Button registrationBtn, confirmregistrationBtn;
    private ProgressBar progressBar;
    private  TextView verification, txtPassword;
    private   EditText email, password, name, phoone;
    private  DatabaseReference reference;
    private  ImageButton addImage;
        private   SimpleDraweeView imageUser;
    private   FirebaseUser user;
    private   String  imagePath, imagePathIfnull;
    private Boolean nameIsExists = false;
    private StorageTask<UploadTask.TaskSnapshot> storageReference;
    private int REQUEST_PICK_PHOTO = 100;
    private Uri imageUri= Uri.parse("");
    private SweetAlertDialog pDialogLoding;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());

        setContentView(R.layout.activity_sign_up);
        registrationBtn = findViewById(R.id.registration_btn);
        name = findViewById(R.id.title);
        email = findViewById(R.id.editTextEmailAddress);
       // phoone = findViewById(R.id.wahtsup_edit);
        password = findViewById(R.id.password_edit);
        imageUser = findViewById(R.id.image_view_profile_person);
        addImage = findViewById(R.id.add_image_btn);
        verification = findViewById(R.id.txt_verification);
        confirmregistrationBtn = findViewById(R.id.confirm_regstraion);
        progressBar = findViewById(R.id.progressBar);
       // txtPassword = findViewById(R.id.password_txt);
        imageUser.setImageResource(R.drawable.logo);
        pDialogLoding = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoding.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialogLoding.setTitleText(getResources().getString(R.string.loading_wait));
        pDialogLoding.setCancelable(false);
        Intent intent = getIntent();
        if (intent.getStringExtra("activity").equalsIgnoreCase("signup")) {
            Log.d("ttt","signup");
            Registration();
            selectImage();
            //imageUser.setImageResource(R.drawable.logo);
        } else if (intent.getStringExtra("activity").equalsIgnoreCase("account")) {
Log.d("ttt","account");
            registrationBtn.setVisibility(View.GONE);
            verification.setVisibility(View.GONE);
            password.setText("123123");
            password.setEnabled(false);
//            txtPassword.setEnabled(false);
            confirmregistrationBtn.setVisibility(View.VISIBLE);
            confirmregistrationBtn.setText(R.string.edit);
            email.setEnabled(false);
            getInfoUser();
            selectImage();
            btnEdit();
        }

    }

    private void btnEdit() {
        confirmregistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (/*!phoone.getText().toString().isEmpty() && */!name.getText().toString().isEmpty()) {
                    uploadImage();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", name.getText().toString());
                    hashMap.put("mobileNum", phoone.getText().toString());
                    if(imagePath == null){
                        hashMap.put("imageUrl", imagePathIfnull);

                    }else{
                        hashMap.put("imageUrl", imagePath);

                    }
                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        }
                    });
                 //   FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).child("mobileNum").setValue(phoone.getText());

                }

            }
        });
    }

    private void getInfoUser() {
        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                email.setText(user.getEmail());
//                phoone.setText(user.getMobileNum());
                imagePathIfnull=user.getImageUrl();
                FirebaseStorage.getInstance().getReference().child("usersImages/" + user.getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUser.setImageURI(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("ttt",exception.getMessage());
                        Toast.makeText(getApplicationContext(), "فشل تحميل بعض الصور الشخصية", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
/*
    private void confirmRegistration() {
        progressBar.setVisibility(View.GONE);
        confirmregistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                user.reload();
                if (user.isEmailVerified()) {
                    addUserOnDataBase();
                    getRegistrationToken();
                    registrationTopic();
                }
                else
                {
                    // email is not verified, so just prompt the message to the user and restart this activity.
                    // NOTE: don't forget to log out the user.
                    Toast.makeText(getApplicationContext(),"لم يتم تاكيد التسجيل,حاول مرة أخوى",Toast.LENGTH_SHORT).show();


                    //restart this activity

                }

            }
        });
        }*/

    private void registrationTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void selectImage() {
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
selectPhoto();
            }
        });
    }

    public void selectPhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }
    private String getFileExtension(Uri imageUri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(imageUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                imageUser.setImageURI(imageUri);
                //Log.d("ttt",imageUri.toString());
            }
        }
    }
    private void Registration() {

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( inputValid(name) && inputValid(email)/*&&!phoone.getText().toString().isEmpty()*/ && inputValid(password)){
             if(imageUri!=null && !imageUri.toString().trim().isEmpty()) {
                    checkNameIsExisting();
             } else {
                 Toast.makeText(getApplicationContext(),"يرجى اضافة صورة شخصية",Toast.LENGTH_SHORT).show();
             }
                }




            }
        });
    }
    private boolean inputValid(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(),"يرجى اضافة"+editText.getHint(),Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkNameIsExisting() {
        Log.d("ttt","agianssssssss");
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    progressBar.setVisibility(View.VISIBLE);
                    User user=snapshot.getValue(User.class);
                    if(user.getName().equalsIgnoreCase(name.getText().toString())){
                        Log.d("ttt","wwww00");
Toast.makeText(getApplicationContext(),"الاسم قيد الاستخدام بالفعل من قبل حساب آخر",Toast.LENGTH_SHORT).show();
/*
                        pDialogLoding=new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE);
                        pDialogLoding.setTitleText("الاسم قيد الاستخدام بالفعل من قبل حساب آخر");
                        pDialogLoding .show();*/
                        nameIsExists=true;
                        break;
                    }
                }
                if (nameIsExists==false)
                {
                   // addImage.setEnabled(false);
                    //name.setEnabled    (false);
                    //password.setEnabled(false);
//                    phoone.setEnabled(false);
                 //   email.setEnabled(false);
                    AuthUser();
                }
                nameIsExists=false;
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public  void AuthUser(){
        pDialogLoding.show();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialogLoding.dismiss();

if (e.getMessage().contains("badly formatted")) {
    new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("البريد الإلكتروني منسق بشكل سيئ")
            .show();
} else if (e.getMessage().contains("least 6 characters")) {
                            new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("يجب أن تتكون كلمة المرور من 6 أحرف على الأقل")
                                    .show();
                        }else if(e.getMessage().contains("another account")){
    new SweetAlertDialog(SignUp.this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("البريد الإلكتروني قيد الاستخدام بالفعل من قبل حساب آخر")
            .show();
}
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                    reference = FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                getRegistrationToken();
                registrationTopic();
                addUserOnDataBase();
       // sendVerification();
            }
        });
}
/*
private  void sendVerification(){

      user = FirebaseAuth.getInstance().getCurrentUser();

    user.sendEmailVerification()
            .addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    // Re-enable button
                    //
                    verification.setVisibility(View.VISIBLE);
registrationBtn.setVisibility(View.INVISIBLE);
confirmregistrationBtn.setVisibility(View.VISIBLE);
                 //   confirmRegistration();
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {

                    }
                }
            });
}
*/
    private void addUserOnDataBase(){
        uploadImage();
        HashMap<String,String> user=new HashMap<>();
        user.put("email",email.getText().toString());
        user.put("name",name.getText().toString());
        user.put("password",password.getText().toString());
//        user.put("mobileNum",phoone.getText().toString());
        user.put("enable","true");
        user.put("topic","all");
        user.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.put("imageUrl",imagePath);
        user.put("token",FirebaseInstanceId.getInstance().getToken());
        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> tasks) {

                if (tasks.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    pDialogLoding.cancel();
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(SignUp.this, SweetAlertDialog.SUCCESS_TYPE);
                       sweetAlertDialog .setCancelable(false);
                    sweetAlertDialog.setTitleText("تم التسجيل بنجاح")
                            .setConfirmButton("موافق", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    startActivity(new Intent(getApplicationContext(),Home.class));
                                    finish();
sweetAlertDialog.dismiss();
                                }
                            }).show();
                }   }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoding.dismiss();
                Log.d("ttt",e.getMessage());
                Toast.makeText(getApplicationContext(),R.string.wrong_in_save_data,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        if (imageUri != null) {
            imagePath = System.currentTimeMillis() + "." + getFileExtension(imageUri);
            storageReference = FirebaseStorage.getInstance().getReference().child("usersImages").
                    child(imagePath).putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "تم تحميل الصورة", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void getRegistrationToken () {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            Log.d("ttt", token);
                 }
                 }
                });
}
}