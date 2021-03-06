package com.example.petrica.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.example.petrica.R;
import com.example.petrica.adapters.CommentAdapter;
import com.example.petrica.dao.ServerResponse;
import com.example.petrica.model.Comment;

import java.util.ArrayList;
import java.util.Date;

public class CommentsActivity extends BaseContentActivity {
    // Activity showing comments of an event
    protected Switch switch_manage;
    protected Button button_add;
    protected EditText edit_comment;
    protected Button button_send;
    protected CommentAdapter commentAdapter;
    protected String id_event;
    protected NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_comments);
        id_event = getIntent().getStringExtra(EXTRA_ID_EVENT);
        if (id_event == null){
            finish();
        }
        // Finding views
        button_add = findViewById(R.id.comment_add);
        switch_manage = findViewById(R.id.comment_manage);
        edit_comment = findViewById(R.id.comment_edit_write);
        button_send = findViewById(R.id.comment_send);
        scrollView = findViewById(R.id.main_layout);
        commentAdapter = new CommentAdapter(new ArrayList<Comment>(),getLayoutInflater());
        ListView lw = findViewById(R.id.comment_list_comments);
        
        // Adding listeners
        lw.setAdapter(commentAdapter);
        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Comment c = (Comment) parent.getAdapter().getItem(position);
                if (user != null && user.getUid().equals(c.getId_user())){
                    (new AlertDialog.Builder(CommentsActivity.this))
                            .setMessage(R.string.comment_delete_ask).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE){
                                        model.unwriteComment(c.getId_comment(),id_event);
                                        makeLoadingScreen();
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create().show();
                }
            }
        });
        
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null){
                    askLogin();
                }
                else{
                    edit_comment.setVisibility(View.VISIBLE);
                    button_send.setVisibility(View.VISIBLE);
                }
            }
        });
        
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null){
                    askLogin();
                }
                else if (!edit_comment.getText().toString().equals("")){
                    Comment c = new Comment(new Date(),user.getUid(),user.getDisplayName(),edit_comment.getText().toString(),"");
                    model.writeComment(c,id_event);
                    makeLoadingScreen();
                }
            }
        });

        switch_manage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (user == null){
                        switch_manage.setChecked(false);
                        askLogin();
                    }
                    else{
                        makeLoadingScreen();
                        commentAdapter.clear(true);
                        model.loadUserComments(id_event,user.getUid());
                    }
                }
                else {
                    if (user != null){
                        makeLoadingScreen();
                        commentAdapter.clear(true);
                        model.loadComments(id_event);
                    }
                }
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (!isListFinished && !commentAdapter.isEmpty() && scrollView.getChildAt(0).getBottom()
                        <= (scrollView.getHeight() + scrollView.getScrollY())) {
                    makeLoadingScreen();
                    model.getNextComments();
                }
            }
        });

        if (savedInstanceState == null){
            model.loadComments(id_event);
        }
        else{
            isListFinished = savedInstanceState.getBoolean(EXTRA_IS_LIST_FINISHED,false);
            commentAdapter.addData(model.getSavedComment());
        }
    }

    @Override
    public void onServerResponse(ServerResponse serverResponse) {
        switch (serverResponse.getResponseCode()){
            case ServerResponse.RESPONSE_WRITING_COMMENT_OK:
                Toast.makeText(this,R.string.comment_write_successful,Toast.LENGTH_SHORT).show();
                commentAdapter.addData(serverResponse.getReviewsList());
                edit_comment.setText("");
                break;
            case ServerResponse.RESPONSE_COMMENT_EVENT_OK:
                commentAdapter.addData(serverResponse.getReviewsList());
                isListFinished = false;
                break;
            case ServerResponse.RESPONSE_DELETE_COMMENT_OK:
                Toast.makeText(this,R.string.comment_delete_successful,Toast.LENGTH_SHORT).show();
                Comment c = serverResponse.getReviewsList().get(0);
                commentAdapter.deleteItem(c.getId_comment(),true);
                break;
            case ServerResponse.RESPONSE_COMMENT_EVENT_NEXT:
                commentAdapter.addData(serverResponse.getReviewsList());
                if (serverResponse.getReviewsList().isEmpty()){
                    isListFinished = true;
                }
                break;
            case ServerResponse.RESPONSE_WRITING_COMMENT_ERROR:
            case ServerResponse.RESPONSE_COMMENT_EVENT_ERROR:
            case ServerResponse.RESPONSE_DELETE_COMMENT_ERROR:
                Toast.makeText(this,R.string.err_retry,Toast.LENGTH_SHORT).show();
                isListFinished = true;
                break;
        }
    }

    @Override
    public void refresh() {
        commentAdapter.clear(true);
        makeLoadingScreen();
        model.loadComments(id_event);
    }

    @Override
    public void onUserDisconnect() {
        edit_comment.setVisibility(View.GONE);
        button_send.setVisibility(View.GONE);
    }

    @Override
    public void onUserConnect() {
        // Nothing to do
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_IS_LIST_FINISHED,isListFinished);
        model.setSavedComment(commentAdapter.getData());
    }
}
