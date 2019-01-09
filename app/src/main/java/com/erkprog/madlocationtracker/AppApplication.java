package com.erkprog.madlocationtracker;

import android.app.Application;

import com.erkprog.madlocationtracker.data.repository.LocalRepository;

public class AppApplication extends Application {

  private static AppApplication instance;
  private LocalRepository repository;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    repository = new LocalRepository(this);
  }

  public static AppApplication getInstance() {
    return instance;
  }

  public LocalRepository getRepository() {
    return repository;
  }
}
