package com.Garbage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.android.volley.VolleyLog.TAG;

public class Login_Fragment extends Fragment implements OnClickListener {
    private static View view;

    private static EditText emailid, password;
    private static Button loginButton;
    private static TextView forgotPassword, ADMIN;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    private FirebaseAuth mAuth;
    private Context mContext;
    private FirebaseUser user = null;
    public Login_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        view = inflater.inflate(R.layout.login_layout, container, false);
        initViews();
        setListeners();
        mContext=getContext();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        emailid = (EditText) view.findViewById(R.id.login_emailid);
        password = (EditText) view.findViewById(R.id.login_password);
        emailid.setText("");
        password.setText("");
        show_hide_password = (CheckBox) view
                .findViewById(R.id.show_hide_password);
        show_hide_password.setChecked(false);
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    // Initiate Views
    private void initViews() {
        fragmentManager = getActivity().getSupportFragmentManager();

        emailid = (EditText) view.findViewById(R.id.login_emailid);
        password = (EditText) view.findViewById(R.id.login_password);
        loginButton = (Button) view.findViewById(R.id.loginBtn);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        ADMIN = (TextView) view.findViewById(R.id.AdminLogin);
        show_hide_password = (CheckBox) view
                .findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews
        @SuppressLint("ResourceType") XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            ADMIN.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        ADMIN.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:

                checkValidation();
                //

                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;
            case R.id.AdminLogin:

                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new Admin(),
                                Utils.Admin).commit();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        emailid = view.findViewById(R.id.login_emailid);
        password = view.findViewById(R.id.login_password);
        String getEmailId = emailid.getText().toString();

        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.isEmpty()||getPassword.isEmpty()) {
            new CustomToast().Show_Toast(getActivity(), view,
                    "Enter both credentials.");

        }
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");
        // Else do login and do your stuff
        else
            loginAgmin(emailid.getText().toString(), password.getText().toString());

    }

    private void loginAgmin(final String emailid, final String password) {
        mAuth.signInWithEmailAndPassword(emailid, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SIGIN_USER_RESULT", "signInWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            updateUser(user,emailid);
                            //Intent nav=new Intent(getActivity(),Navigation.class);
                            //startActivity(nav);
                        } else {
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthInvalidUserException invalidEmail){
                                Log.d(TAG, "onComplete: invalid_email");
                                signUpAdmin(emailid,password);
                            }catch (FirebaseAuthInvalidCredentialsException wrongPassword){
                                Log.d(TAG, "onComplete: wrong_password");
                                Log.w("SIGIN_USER_RESULT", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }catch (Exception e){
                                Log.d(TAG, "onComplete: " + e.getMessage());
                            }
                        }
                    }
                });
    }

    private void signUpAdmin(final String emailid, String password) {
        mAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("CREATE_USER_RESULT:  ", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUser(user,emailid);
                            updateUI(user);
                        } else {
                            Log.w("CREATE_USER_RESULT", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUser(final FirebaseUser user,String emailid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference postRef = database.getReference().child("User");

        postRef.orderByChild("email").equalTo(emailid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        Log.i(TAG,ds.getKey());
                        postRef.child(ds.getKey()).child("isVerified").setValue(true);
                        postRef.child(ds.getKey()).child("id").setValue(user.getUid());
                        Toast.makeText(getActivity(), "Succes.!", Toast.LENGTH_SHORT).show();
                        Intent nav=new Intent(getActivity(),Navigation.class);
                        startActivity(nav);
                    }
                } else {
                    Toast.makeText(getActivity(), "Invalid UserName or Password.!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG,"EXCEPTION", databaseError.toException());
            }
        });
    }


    private void updateUI(FirebaseUser currentUser) {
        user = currentUser;
    }
}
