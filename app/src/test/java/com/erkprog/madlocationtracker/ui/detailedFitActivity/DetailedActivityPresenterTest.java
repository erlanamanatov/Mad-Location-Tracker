package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DetailedActivityPresenterTest {

  @Rule
  public MockitoRule mMockitoRule = MockitoJUnit.rule();

  @Mock
  DetailedFitActivityContract.View view;

  @Mock
  LocalRepository repository;

  DetailedActivityPresenter presenter;
  private long fitActivityId = 2;


  @Before
  public void setUp() {
    presenter = new DetailedActivityPresenter(repository, fitActivityId);
    presenter.bind(view);
  }

  @BeforeClass
  public static void setUpRxSchedulers() {
    Scheduler immediate = new Scheduler() {
      @Override
      public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
        return super.scheduleDirect(run, 0, unit);
      }

      @Override
      public Scheduler.Worker createWorker() {
        return new ExecutorScheduler.ExecutorWorker(Runnable::run);
      }
    };

    RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
    RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
    RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
    RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
    RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
  }

  @Test
  public void processFitActivity_shouldShowFitActivityDetails() {
    FitActivity fitActivity = getFakeFitActivity();
    presenter.processFitActivity(fitActivity);

    verify(view).showDistance("550.00 m");
    verify(view).showDuration("0h 2m 0s");
    verify(view).showAvgSpeed("28.29 km/h");
    verify(view).showTrackingTime("0h 1m 10s");
  }

  private FitActivity getFakeFitActivity() {
    FitActivity fitActivity = new FitActivity();
    fitActivity.setTrackingDuration(70 * 1000); // 1 min 10 secs
    fitActivity.setDistance(550); // 550 m
    Date endtime = new Date(120 * 1000); // 2 min
    Date starttime = new Date(0);
    fitActivity.setEndTime(endtime);
    fitActivity.setStartTime(starttime);
    return fitActivity;
  }

}