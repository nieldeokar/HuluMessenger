/*
 * Copyright (C) 2017.  QT-Software Ltd, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written and created by Shain Singh<support@qtsoftwareltd.net> on 28/6/17 6:14 PM
 */

package com.nieldeokar.hurumessenger.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationContext {
}
