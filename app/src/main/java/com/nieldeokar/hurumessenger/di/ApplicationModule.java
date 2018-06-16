/*
 * Copyright (C) 2017.  QT-Software Ltd, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written and created by Shain Singh<support@qtsoftwareltd.net> on 21/6/17 4:21 PM
 */

package com.nieldeokar.hurumessenger.di;

import android.app.Application;
import android.content.Context;

import com.nieldeokar.hurumessenger.services.LocalTransport;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {

        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    LocalTransport provideLocalTransport() {
        return new LocalTransport();
    }

}
