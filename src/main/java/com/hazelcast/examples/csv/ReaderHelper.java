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

package com.hazelcast.examples.csv;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.examples.model.Crime;
import com.hazelcast.examples.model.Person;
import com.hazelcast.examples.model.SalaryYear;
import com.hazelcast.examples.model.State;

import java.io.InputStream;
import java.util.List;

public final class ReaderHelper {

    private static final ClassLoader CLASS_LOADER = ReaderHelper.class.getClassLoader();

    private ReaderHelper() {
    }

    public static void read(HazelcastInstance hazelcastInstance)
            throws Exception {

        readStates(hazelcastInstance);
        readPeople(hazelcastInstance);
        readCrimes(hazelcastInstance);
        readSalary(hazelcastInstance);
    }

    private static void readStates(HazelcastInstance hazelcastInstance)
            throws Exception {

        StateDataReader stateDataReader = new StateDataReader();
        try (InputStream is = CLASS_LOADER.getResourceAsStream("state_table.csv")) {
            IMap<Integer, State> statesMap = hazelcastInstance.getMap("states");
            List<State> states = stateDataReader.read(is);
            for (State state : states) {
                statesMap.put(state.getId(), state);
            }
        }
    }

    private static void readPeople(HazelcastInstance hazelcastInstance)
            throws Exception {

        PersonDataReader personDataReader = new PersonDataReader();
        try (InputStream is = CLASS_LOADER.getResourceAsStream("us-500.csv")) {
            IList<Person> personsList = hazelcastInstance.getList("persons");
            List<Person> persons = personDataReader.read(is);
            personsList.addAll(persons);
        }
    }

    private static void readSalary(HazelcastInstance hazelcastInstance)
            throws Exception {

        SalaryDataReader salaryDataReader = new SalaryDataReader();
        try (InputStream is = CLASS_LOADER.getResourceAsStream("salary.csv")) {
            IMap<String, SalaryYear> salariesMap = hazelcastInstance.getMap("salaries");
            List<SalaryYear> salaries = salaryDataReader.read(is);
            for (SalaryYear salary : salaries) {
                salariesMap.put(salary.getEmail(), salary);
            }
        }
    }

    private static void readCrimes(HazelcastInstance hazelcastInstance)
            throws Exception {

        CrimeDataReader crimeDataReader = new CrimeDataReader();
        try (InputStream is = CLASS_LOADER.getResourceAsStream("CrimeStatebyState.csv")) {
            IList<Crime> crimesList = hazelcastInstance.getList("crimes");
            List<Crime> crimes = crimeDataReader.read(is);
            crimesList.addAll(crimes);
        }
    }
}
