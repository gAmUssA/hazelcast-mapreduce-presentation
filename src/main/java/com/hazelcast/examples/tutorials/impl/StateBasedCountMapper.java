/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.examples.tutorials.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.examples.model.Person;
import com.hazelcast.examples.model.State;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.LifecycleMapperAdapter;

import java.util.HashMap;
import java.util.Map;

public class StateBasedCountMapper
        extends LifecycleMapperAdapter<String, Person, String, Integer>
        implements HazelcastInstanceAware {

    private static final Integer ONE = Integer.valueOf(1);

    private String state;
    private transient HazelcastInstance hazelcastInstance;

    private final Map<String, String> abbrevationMapping = new HashMap<>(50);

    public StateBasedCountMapper() {
    }

    public StateBasedCountMapper(String state) {
        this.state = state;
    }

    @Override
    public void initialize(Context<String, Integer> context) {
        super.initialize(context);

        IMap<Integer, State> states = hazelcastInstance.getMap("states");
        for (State s : states.values()) {
            String abbrevation = s.getAbbreviation();
            String name = s.getName();
            abbrevationMapping.put(abbrevation, name);
        }
    }

    @Override
    public void map(String key, Person value, Context<String, Integer> context) {
        if (state != null) {
            if (!state.equals(value.getState())) {
                return;
            }
        }

        String name = abbrevationMapping.get(value.getState());
        context.emit(name, ONE);
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
