package com.example.umasurakod.ping;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private EditText groupChat;
    private ScrollView scroller;
    private TextView groupTexts;
    private ImageButton sendButton;

    private FirebaseAuth mAuth;
    private String currentGroupName, currentUserId, currentUserName, currentDate, currentTime;
    private DatabaseReference userRef, groupNameRef, groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");
        groupNameRef =  FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);



        InitializeFields();

        getUserInfo();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChatToDatabase();

                groupChat.setText("");

                scroller.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {
        mToolBar = (Toolbar)findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(currentGroupName);
        groupChat = (EditText)findViewById(R.id.group_message);
        scroller = (ScrollView)findViewById(R.id.group_chat_scrollView);
        groupTexts = (TextView)findViewById(R.id.group_chat_text);
        sendButton = (ImageButton)findViewById(R.id.send_message_button);
    }

    private void getUserInfo() {

        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = (String) dataSnapshot.child("name").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void saveChatToDatabase() {
        String message = groupChat.getText().toString();
        String messageKey = groupNameRef.push().getKey();

        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this, "Please enter something...", Toast.LENGTH_SHORT).show();
        }
        else{
            Date ccalForDate = Calendar.getInstance().getTime();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
            currentDate = currentDateFormat.format(ccalForDate);

            Date ccalForTime = Calendar.getInstance().getTime();
            DateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(ccalForTime);

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            groupMessageKeyRef = groupNameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap = new HashMap<>();
                    messageInfoMap.put("name",currentUserName);
                    messageInfoMap.put("message", message);
                    messageInfoMap.put("date", currentDate);
                    messageInfoMap.put("time", currentTime);

                    groupMessageKeyRef.updateChildren(messageInfoMap);

        }
    }
    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String  chatUserName= (String) ((DataSnapshot)iterator.next()).getValue();
            String  chatTime= (String) ((DataSnapshot)iterator.next()).getValue();

            groupTexts.append(chatUserName +": \n"+chatMessage+"\n"+chatTime+"  "+chatDate+"\n\n\n");

            scroller.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }
}
