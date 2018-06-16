package com.nieldeokar.hurumessenger.ui.main.di;

import com.nieldeokar.hurumessenger.di.ApplicationComponent;
import com.nieldeokar.hurumessenger.di.PerActivity;
import com.nieldeokar.hurumessenger.ui.main.MainActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = MainActivityModule.class)
public interface MainActivityComponent {

    void inject(MainActivity mainActivity);

}