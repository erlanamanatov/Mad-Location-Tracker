package com.erkprog.madlocationtracker.ui.detailedFitActivity;

import android.location.Location;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.entity.HeartRateModel;
import com.erkprog.madlocationtracker.data.entity.LocationItem;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

  @Test
  public void getLocations_WhenOnSuccessAndViewIsAttached_ShouldShowRoute() {
    List<LocationItem> locationList = getFakeLocationList();
    when(repository.getLocationsByActivity(fitActivityId, LocationItem.TAG_GEO_FILTERED))
        .thenReturn(Single.just(locationList));

    presenter.getLocations();
    verify(view).showStartOfRoute(any());
    verify(view).showEndOfRoute(any());
    verify(view).showRoute(any(), any());
  }

  @Test
  public void getLocations_WhenOnSuccessAndViewIsNotAttached_ShouldDoNothing() {
    List<LocationItem> locationList = getFakeLocationList();
    presenter.unBind();
    when(repository.getLocationsByActivity(fitActivityId, LocationItem.TAG_GEO_FILTERED))
        .thenReturn(Single.just(locationList));

    presenter.getLocations();
    verify(view, never()).showStartOfRoute(any());
    verify(view, never()).showEndOfRoute(any());
    verify(view, never()).showRoute(any(), any());
  }

  @Test
  public void getLocations_WhenOnErrorAndViewIsAttached_ShouldShowErrorMessage() {
    when(repository.getLocationsByActivity(fitActivityId, LocationItem.TAG_GEO_FILTERED))
        .thenReturn(Single.error(new Exception("some error")));

    presenter.getLocations();
    verify(view).showMessage(R.string.error_loading_detailed_data);
  }

  @Test
  public void getLocations_WhenOnErrorAndViewIsNotAttached_ShouldDoNothing() {
    presenter.unBind();
    when(repository.getLocationsByActivity(fitActivityId, LocationItem.TAG_GEO_FILTERED))
        .thenReturn(Single.error(new Exception("some error")));
    presenter.getLocations();
    verify(view, never()).showMessage(anyInt());
  }

  @Test
  public void getHeartRate_WhenOnSuccessAndViewIsAttachedAndEnoughData_ShouldPlotGraph() {
    when(repository.getHeartRateByFitId(fitActivityId))
        .thenReturn(Maybe.just(getFakeHeartRateData()));

    presenter.getHeartRate();
    verify(view).plotGraph(any(), anyLong());
  }

  @Test
  public void getHeartRate_WhenOnSuccessAndViewIsNotAttachedAndEnoughData_ShouldDoNothing() {
    presenter.unBind();
    when(repository.getHeartRateByFitId(fitActivityId))
        .thenReturn(Maybe.just(getFakeHeartRateData()));

    presenter.getHeartRate();
    verify(view, never()).plotGraph(any(), anyLong());
    verify(view, never()).hideGraph();
  }

  @Test
  public void getHeartRate_WhenOnSuccessAndViewIsAttachedAndNotEnoughData_ShouldHideGraph() {
    List<HeartRateModel> heartRateModels = mock(List.class);
    when(heartRateModels.size()).thenReturn(2);
    when(repository.getHeartRateByFitId(fitActivityId))
        .thenReturn(Maybe.just(heartRateModels));

    presenter.getHeartRate();
    verify(view).hideGraph();
    verify(view).showMessage(R.string.not_enough_hr_data);
  }

  @Test
  public void getHeartRate_WhenOnErrorAndViewIsAttached_ShouldHideGraph() {
    when(repository.getHeartRateByFitId(fitActivityId))
        .thenReturn(Maybe.error(new Exception("some error")));

    presenter.getHeartRate();
    verify(view).hideGraph();
  }

  @Test
  public void getHeartRate_WhenOnErrorAndViewIsNotAttached_ShouldDoNothing() {
    presenter.unBind();
    when(repository.getHeartRateByFitId(fitActivityId))
        .thenReturn(Maybe.error(new Exception("some error")));

    presenter.getHeartRate();
    verify(view, never()).hideGraph();
    verify(view, never()).plotGraph(any(), anyLong());
  }

  private List<HeartRateModel> getFakeHeartRateData() {
    List<HeartRateModel> data = new ArrayList<>();
    data.add(new HeartRateModel(50, 1));
    data.add(new HeartRateModel(45, 1));
    data.add(new HeartRateModel(56, 1));
    data.add(new HeartRateModel(77, 1));
    data.add(new HeartRateModel(88, 1));
    data.add(new HeartRateModel(50, 1));
    return data;
  }

  private List<LocationItem> getFakeLocationList() {
    List<LocationItem> locationList = new ArrayList<>();
    Location location = mock(Location.class);
    when(location.getLatitude()).thenReturn(42.875341);
    when(location.getLongitude()).thenReturn(74.620104);
    locationList.add(new LocationItem(location, 1));
    when(location.getLatitude()).thenReturn(42.875679);
    when(location.getLongitude()).thenReturn(74.611725);
    locationList.add(new LocationItem(location, 1));
    return locationList;
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