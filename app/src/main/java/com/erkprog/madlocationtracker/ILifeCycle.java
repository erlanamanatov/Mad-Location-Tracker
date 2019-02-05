package com.erkprog.madlocationtracker;

public interface ILifeCycle<V> {
  void bind(V view);

  void unBind();
}
