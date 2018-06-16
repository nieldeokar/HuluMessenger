package com.nieldeokar.hurumessenger.ui.chat.di;

import com.nieldeokar.hurumessenger.di.ApplicationComponent;
import com.nieldeokar.hurumessenger.di.PerActivity;
import com.nieldeokar.hurumessenger.ui.main.MainActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ChatActivityModule.class)
public interface ChatActivityComponent {

    void inject(MainActivity mainActivity);

}