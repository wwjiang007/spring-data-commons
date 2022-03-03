/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.observability;

import io.micrometer.core.instrument.observation.Observation;

import java.lang.reflect.Method;

import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * A {@link Observation.Context} for Commons.
 *
 * @author Greg Turnquist
 * @since 3.0.0
 */
public class QueryDerivationContext extends Observation.Context {

	private final QueryLookupStrategy strategy;
	private final Method method;
	private final RepositoryInformation information;

	private RepositoryQuery query;

	public QueryDerivationContext(QueryLookupStrategy strategy, Method method, RepositoryInformation information) {

		this.strategy = strategy;
		this.method = method;
		this.information = information;
	}

	public QueryLookupStrategy getStrategy() {
		return strategy;
	}

	public Method getMethod() {
		return method;
	}

	public RepositoryInformation getInformation() {
		return information;
	}

	public RepositoryQuery getQuery() {
		return query;
	}

	public void setQuery(RepositoryQuery query) {
		this.query = query;
	}
}
