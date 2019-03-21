package com.erkprog.madlocationtracker.ui.main;

import com.erkprog.madlocationtracker.R;
import com.erkprog.madlocationtracker.data.entity.FitActivity;
import com.erkprog.madlocationtracker.data.repository.LocalRepository;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

  @Rule
  public MockitoRule mMockitoRule = MockitoJUnit.rule();

  @Mock
  MainContract.View view;

  @Mock
  LocalRepository repository;

  MainPresenter presenter;

  @Before
  public void setUp() {
    presenter = new MainPresenter(repository);
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
  public void loadFitActivities_WhenOnSuccessAndViewIsAttachedAndDbNotEmpty_ShouldDisplayActivities() {
    List<FitActivity> fitActivities = mock(List.class);
    when(fitActivities.size()).thenReturn(5);
    when(repository.getAllActivities()).thenReturn(Maybe.just(fitActivities));

    presenter.loadFitActivities();
    verify(view).displayActivities(anyList());
  }

  @Test
  public void loadFitActivities_WhenOnSuccessAndViewIsNotAttached_ShouldDoNothing() {
    List<FitActivity> fitActivities = mock(List.class);
    presenter.unBind();
    when(fitActivities.size()).thenReturn(5);
    when(repository.getAllActivities()).thenReturn(Maybe.just(fitActivities));

    presenter.loadFitActivities();
    verify(view, never()).displayActivities(anyList());
    verify(view, never()).showMessage(anyInt());
  }

  @Test
  public void loadFitActivities_WhenOnSuccessAndViewIsAttachedAndDbIsEmpty_ShouldDisplayEmptyListMessage() {
    List<FitActivity> fitActivities = mock(List.class);
    when(fitActivities.size()).thenReturn(0);
    when(repository.getAllActivities()).thenReturn(Maybe.just(fitActivities));

    presenter.loadFitActivities();
    verify(view).showEmptyListMessage();
  }

  @Test
  public void loadFitActivities_WhenOnErrorAndViewIsAttached_ShouldShowErrorMessage() {
    when(repository.getAllActivities())
        .thenReturn(Maybe.error(new Exception("some error")));

    presenter.loadFitActivities();
    verify(view).showMessage(R.string.error_loading_data);
  }

  @Test
  public void loadFitActivities_WhenOnErrorAndViewIsNotAttached_ShouldDoNothing() {
    presenter.unBind();
    when(repository.getAllActivities())
        .thenReturn(Maybe.error(new Exception("some error")));

    presenter.loadFitActivities();
    verify(view, never()).showMessage(R.string.error_loading_data);
    verify(view, never()).displayActivities(anyList());
  }
}