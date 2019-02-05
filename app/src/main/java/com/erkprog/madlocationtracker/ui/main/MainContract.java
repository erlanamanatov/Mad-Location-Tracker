package com.erkprog.madlocationtracker.ui.main;

import com.erkprog.madlocationtracker.ILifeCycle;
import com.erkprog.madlocationtracker.data.entity.FitActivity;

import java.util.List;

class MainContract {

  interface View {

    void displayActivities(List<FitActivity> fitActivities);
  }

  interface Presenter extends ILifeCycle<View> {

    boolean isAttached();

    void loadFitActivities();

  }
}
