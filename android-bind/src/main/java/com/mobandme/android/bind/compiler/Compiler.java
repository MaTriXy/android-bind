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

package com.mobandme.android.bind.compiler;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.mobandme.android.bind.annotations.BindTo;
import com.mobandme.android.bind.annotations.Bindings;

public final class Compiler {

    //region "PRIVATE VARIABLES"

    private Object object;
    private ViewGroup rootView;
    private Collection<Mapping> mappings;

    //endregion

    public Compiler(Object object, ViewGroup rootView) {
        this.object = object;
        this.rootView = rootView;
        this.mappings = new ArrayList<>();
    }

    public Compiler compile() {
        readObjectConfiguration();
        return this;
    }

    public Collection<Mapping> getMappingsList() {
        return this.mappings;
    }

    //region "PRIVATE METHODS"

    private void readObjectConfiguration() {
        Field[] declaredFields = this.object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {

            Bindings bindingsAnnotation = field.getAnnotation(Bindings.class);
            if (bindingsAnnotation != null) {
                BindTo[] bindsToAnnotations = bindingsAnnotation.value();
                if (bindsToAnnotations != null && bindsToAnnotations.length > 0) {
                    for (BindTo bindToAnnotation : bindsToAnnotations) {
                        Mapping mapping = extractMapping(field, bindToAnnotation);
                        if (mapping != null)
                            this.mappings.add(mapping);
                    }
                }
            } else {
                BindTo bindToAnnotation = field.getAnnotation(BindTo.class);
                if (bindToAnnotation != null) {
                    Mapping mapping = extractMapping(field, bindToAnnotation);
                    if (mapping != null)
                        this.mappings.add(mapping);
                }
            }
        }
    }

    private Mapping extractMapping(Field field, BindTo bindToAnnotation) {
        Mapping result = null;

        int viewId = bindToAnnotation.viewId();
        View view = extractView(viewId);

        if (view != null) {
            Method getterMethod = extractGetterMethod(this.object, field, bindToAnnotation);
            Method setterMethod = extractSetterMethod(this.object, field, bindToAnnotation);
            Class binder = bindToAnnotation.binder();
            Class parser = bindToAnnotation.parser();

            result = new Mapping(field, view, getterMethod, setterMethod, binder, parser);
        }

        return result;
    }

    private Method extractGetterMethod(Object object, Field field, BindTo bind) {
        Method result = null;

        if (bind.getter() != null) {
            String methodName;
            if (bind.getter().equals("")) {
                String methodSuffix = capitalize(field.getName());
                if (field.getType() != Boolean.class)
                    methodName = String.format("get%s", methodSuffix);
                else
                    methodName = String.format("is%s", methodSuffix);
            } else {
                methodName = bind.getter();
            }

            result = extractMethod(object.getClass(), methodName, null);
        }
        return result;
    }

    private Method extractSetterMethod(Object object, Field field, BindTo bind) {
        Method result = null;

        if (bind.setter() != null) {
            String methodName;

            if (bind.setter().equals("")) {
                String methodSuffix = capitalize(field.getName());
                methodName = String.format("set%s", methodSuffix);
            } else {
                methodName = bind.setter();
            }

            result = extractMethod(object.getClass(), methodName, new Class[]{field.getType()});
        }

        return result;
    }

    private Method extractMethod(Class objectClass, String methodName, Class[] argumentsTypes) {
        Method result = null;

        try {
            result = objectClass.getMethod(methodName, argumentsTypes);
        } catch (NoSuchMethodException error) { }

        return result;
    }

    private View extractView(int viewId) {
        return this.rootView.findViewById(viewId);
    }

    private String capitalize(String source) {
        String result = source.trim();

        if (result != null && !result.equals("")) {
            if (result.length() > 1) {
                result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
            } else {
                result = result.toUpperCase();
            }
        }
        return result;
    }

    //endregion

    //region "EMBEDDED CLASSES"

    public class Mapping {

        private Field field;
        private View view;
        private Method getter;
        private Method setter;
        private Class  binder;
        private Class  parser;

        public Field getField() { return field; }
        public View getView() { return view; }
        public Method getGetter() { return getter; }
        public Method getSetter() { return setter; }
        public Class getBinder() { return binder; }
        public Class getParser() { return parser; }

        public Mapping(Field field, View view, Method getter, Method setter, Class binder, Class parser) {
            this.field = field;
            this.view = view;
            this.getter = getter;
            this.setter = setter;
            this.binder = binder;
            this.parser = parser;
        }

        @Override
        public String toString() {
            if (field != null)
                return field.getName();
            else
                return super.toString();
        }
    }

    //endregion
}
