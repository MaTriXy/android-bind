/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */

package com.mobandme.android.bind.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.mobandme.android.bind.binder.GenericDataBinder;
import com.mobandme.android.bind.parser.GenericDataParser;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindTo {
	/**
	 * Define the View control ID linked to the Entity property.
	 * @return
	 */
	public int viewId();
	
	/**
	 * Define the custom property getter.
	 * @return
	 */
	public String getter() default "";
	
	/**
	 * Define the custom property setter.
	 * @return
	 */
	public String setter() default "";
	
	/**
	 * Define the custom DataBinder Class.
	 * @return
	 */
	public Class<?> binder() default GenericDataBinder.class;
	
	
	/**
	 * Define the custom dapa parser class.
	 * @return
	 */
	public Class<?> parser()  default GenericDataParser.class;
}
