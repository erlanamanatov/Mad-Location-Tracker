package com.erkprog.madlocationtracker.ui.main;

import com.erkprog.madlocationtracker.Utils;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;

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

  @Override
  public void loadFitActivities() {
    mRepository.getDatabase().acitivityDao()
        .getAllActivities()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableMaybeObserver<List<FitActivity>>() {
          @Override
          public void onSuccess(List<FitActivity> fitActivities) {
            if (isAttached()) {
              mView.displayActivities(fitActivities);
            }
          }

          @Override
          public void onError(Throwable e) {
            Utils.logd(TAG, "Error loading activities");
          }

          @Override
          public void onComplete() {
            Utils.logd(TAG, "no activities in db");
          }
        });

  }
}