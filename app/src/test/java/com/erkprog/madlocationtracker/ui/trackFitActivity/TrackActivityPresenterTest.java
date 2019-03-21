package com.erkprog.madlocationtracker.ui.trackFitActivity;

import android.location.Location;

import com.erkprog.madlocationtracker.data.entity.FitActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrackActivityPresenterTest {

  @Rule
  public MockitoRule mMockitoRule = MockitoJUnit.rule();

  @Mock
  TrackActivityContract.View view;


  TrackActivityPresenter presenter;

  @Before
  public void setUp() {
    presenter = new TrackActivityPresenter();
    presenter.bind(view);
  }

  @Test
  public void onTrackingBroadcastReceived_WhenFitActivityIsNotNull_ShouldShowPositionAndRoute() {
    FitActivity fitActivity = mock(FitActivity.class);
    when(fitActivity.getDistance()).thenReturn(123f);
    when(view.getLocationsList()).thenReturn(mock(List.class));
    Location location = mock(Location.class);

    presenter.onTrackingBroadcastReceived(fitActivity, location);
    verify(view).showCurrentPosition(location);
    verify(view).showDistance(anyString());
    verify(view).showRoute(anyList());
  }

  @Test
  public void onTrackingBroadcastReceived_WhenFitActivityIsNull_ShouldShowOnlyCurrentPosition() {
    when(view.getLocationsList()).thenReturn(mock(List.class));
    Location location = mock(Location.class);

    presenter.onTrackingBroadcastReceived(null, location);
    verify(view).showCurrentPosition(location);
    verify(view, never()).showDistance(anyString());
    verify(view, never()).showRoute(anyList());
  }

  @Test
  public void onHeartRateRead_WhenViewIsAttachedAndValueIsValid_ShouldShowHeartRate() {
    int value = 70;
    presenter.onHeartRateRead(value);
    verify(view).showHeartRateValue(value);
  }

  @Test
  public void onHeartRateRead_WhenViewIsAttachedAndValueIsNotValid_ShouldShowErrorHeartRate() {
    int value = 0;
    presenter.onHeartRateRead(value);
    verify(view).showErrorHeartRate();
  }

  @Test
  public void onHeartRate_WhenViewIsNotAttached_ShouldDoNothing() {
    presenter.unBind();
    presenter.onHeartRateRead(67);
    verify(view, never()).showHeartRateValue(anyInt());
    verify(view, never()).showErrorHeartRate();
  }
}