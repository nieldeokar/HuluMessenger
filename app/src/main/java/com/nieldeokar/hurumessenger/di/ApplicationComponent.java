/*
 * Copyright (C) 2017.  QT-Software Ltd, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written and created by Shain Singh<support@qtsoftwareltd.net> on 17/6/17 1:41 PM
 */

package com.nieldeokar.hurumessenger.di;

import android.content.Context;

import com.nieldeokar.hurumessenger.HuruApp;
import com.nieldeokar.hurumessenger.services.LocalTransport;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(HuruApp huruApp);

    @ApplicationContext
    Context context();

    LocalTransport getLocalTransport();
}
