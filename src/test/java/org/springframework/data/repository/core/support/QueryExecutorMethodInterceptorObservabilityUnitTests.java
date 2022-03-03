/*
 * Copyright 2011-2022 the original author or authors.
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
package org.springframework.data.repository.core.support;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.observability.QueryDerivationObservation.*;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.observation.Observation;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.tck.MeterRegistryAssert;
import io.micrometer.tracing.test.simple.SimpleTracer;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.annotation.Id;
import org.springframework.data.observability.DefaultQueryDerivationTagsProvider;
import org.springframework.data.observability.QueryDerivationTagsProvider;
import org.springframework.data.observability.QueryDerivationTracingObservationHandler;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.QueryExecutorMethodInterceptorObservabilityUnitTests.EmployeeRepository.Employee;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.data.util.Streamable;

/**
 * Observability unit tests for {@link QueryExecutorMethodInterceptor}.
 *
 * @author Greg Turnquist
 * @since 3.0.0
 */
@ExtendWith(MockitoExtension.class)
class QueryExecutorMethodInterceptorObservabilityUnitTests {

	@Mock RepositoryInformation information;
	@Mock QueryLookupStrategy strategy;
	@Mock RepositoryQuery repositoryQuery;

	@Test
	void shouldNotCreateAnyMetricsWhenThereIsNoObservation() {

		// given
		SimpleMeterRegistry registry = new SimpleMeterRegistry();
		SimpleTracer tracer = new SimpleTracer();
		registry.withTimerObservationHandler();
		registry.observationConfig().observationHandler(new QueryDerivationTracingObservationHandler(tracer));

		// then
		MeterRegistryAssert.then(registry).hasNoMetrics();
	}

	@Test
	void shouldCreateMetricsDuringSimpleQuery() {

		// given
		SimpleMeterRegistry registry = new SimpleMeterRegistry();
		QueryDerivationTagsProvider tagsProvider = new DefaultQueryDerivationTagsProvider();
		SimpleTracer tracer = new SimpleTracer();
		registry.withTimerObservationHandler();
		registry.observationConfig().observationHandler(new QueryDerivationTracingObservationHandler(tracer));

		when(information.getQueryMethods())
				.thenReturn(Streamable.of(ReflectionUtils.getMethod(EmployeeRepository.class, "findAll").get()));
		when(information.getRepositoryInterface()).then(invocationOnMock -> EmployeeRepository.class);
		when(information.getDomainType()).then(invocationOnMock -> Employee.class);
		when(strategy.resolveQuery(any(Method.class), any(RepositoryInformation.class),
				any(SpelAwareProxyProjectionFactory.class), any(NamedQueries.class))).thenReturn(repositoryQuery);

		// when
		Observation.start("test", registry).scoped(() -> {

			new QueryExecutorMethodInterceptor(information, new SpelAwareProxyProjectionFactory(), Optional.of(strategy),
					PropertiesBasedNamedQueries.EMPTY, Collections.emptyList(), Collections.emptyList(), registry, tagsProvider);
		});

		// then
		MeterRegistryAssert.then(registry).hasTimerWithNameAndTags(QUERY_DERIVATION_OBSERVATION.getName(), Tags.of( //
				LowCardinalityTags.DOMAIN_TYPE.of(Employee.class.getCanonicalName()), //
				LowCardinalityTags.REPOSITORY.of(EmployeeRepository.class.getSimpleName()), //
				LowCardinalityTags.METHOD.of("findAll") //
		));
		assertThat(registry.find(QUERY_DERIVATION_OBSERVATION.getName()).timer().getId()
				.getTag(LowCardinalityTags.STRATEGY.getKey())).contains("QueryLookupStrategy", "MockitoMock");
	}

	interface EmployeeRepository extends CrudRepository<Employee, Long> {
		record Employee(@Id Long id, String name, String role) {
		}
	}
}
