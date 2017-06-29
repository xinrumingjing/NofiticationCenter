package com.mingjing.nofiticationcenter;

/**
 * Created by liukui on 2017/4/25.
 */
import com.mingjing.notification.annotation.*;

import java.util.List;

@Notification
public interface TestCallback {

    void onTeacherListAck(List<Teacher> teachers);

    void onStudentListAck(int size);

    void onTest();
}
