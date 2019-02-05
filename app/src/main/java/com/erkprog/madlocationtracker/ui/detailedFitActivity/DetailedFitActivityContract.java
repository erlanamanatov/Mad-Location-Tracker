package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import com.erkprog.madlocationtracker.ILifeCycle;
import com.erkprog.madlocationtracker.data.entity.LocationItem;

import java.util.List;

public class DetailedFitActivityContract {

  interface View {

    void showTrack(List<LocationItem> locationItems);

    void showMessage(int resId);
  }

  interface Presenter extends ILifeCycle<View> {

    boolean isAttached();

    void getLocations();
  }
}
