package com.mingjing.nofiticationcenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mingjing.notification.NotificationCenter;
import com.mingjing.notification.annotation.Notification;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements TestCallback {

    @Override
    public void onStudentListAck(int size) {
        Toast.makeText(this, "student list ack", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationCenter.INSTANCE.addObserver(this);

        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCenter.INSTANCE.getObserver(TestCallback.class).onTeacherListAck(teachers(3));
            }
        });

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCenter.INSTANCE.getObserver(TestCallback.class).onStudentListAck(20);
            }
        });
    }


    @Override
    public void onTeacherListAck(List<Teacher> teachers) {
        Toast.makeText(this, "teacher list ack", Toast.LENGTH_LONG).show();
    }

    private List<Teacher> teachers(int size) {
        List<Teacher> teachers = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            teachers.add(new Teacher());
        }
        return teachers;
    }
}
