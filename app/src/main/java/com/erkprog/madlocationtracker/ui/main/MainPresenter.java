package com.erkprog.madlocationtracker.ui.main;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;
import com.erkprog.madlocationtracker.utils.Utils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

  private static final String TAG = "MainPresenter";

  private MainContract.View mView;
  private LocalRepository mRepository;

  MainPresenter(LocalRepository repository) {
    mRepository = repository;
  }

  @Override
  public void loadFitActivities() {
    mRepository
        .getAllActivities()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableMaybeObserver<List<FitActivity>>() {
          @Override
          public void onSuccess(List<FitActivity> fitActivities) {
            if (isAttached()) {
              if (fitActivities.size() == 0) {
                mView.showEmptyListMessage();
              } else {
                mView.displayActivities(fitActivities);
              }
            }
          }

          @Override
          public void onError(Throwable e) {
            if (isAttached()) {
              mView.showMessage(R.string.error_loading_data);
              Utils.loge(TAG, e.getMessage());
            }
          }

          @Override
          public void onComplete() {
            if (isAttached()) {
              mView.showMessage(R.string.no_activities_in_db);
            }
          }
        });
  }

  @Override
  public void onFitActivityClicked(FitActivity fitActivity) {
    mView.startDetailedFitActivity(fitActivity);
  }

  @Override
  public void onTrackActivityClicked() {
    mView.startTrackingActivity();
  }

  @Override
  public void bind(MainContract.View view) {
    mView = view;
  }

  @Override
  public void unBind() {
    mView = null;
  }

  @Override
  public boolean isAttached() {
    return mView != null;
  }
}
