/*
 * Copyright (C) 2017.  QT-Software Ltd, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written and created by Shain Singh<support@qtsoftwareltd.net> on 14/6/17 7:21 PM
 */

package com.nieldeokar.hurumessenger.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by janisharali on 27/01/17.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {
}

