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

import io.micrometer.core.instrument.Tags;

/**
 * An implementation of {@link QueryDerivationTagsProvider}.
 *
 * @author Greg Turnquist
 * @since 3.0.0
 */
public class DefaultQueryDerivationTagsProvider implements QueryDerivationTagsProvider {

	@Override
	public Tags getLowCardinalityTags(QueryDerivationContext context) {

		String strategy = context.getStrategy().getClass().getSimpleName();
		String methodName = context.getMethod().getName();
		String repositoryName = context.getInformation().getRepositoryInterface().getSimpleName();
		String domainName = context.getInformation().getDomainType().getCanonicalName();

		return Tags.of( //
				QueryDerivationObservation.LowCardinalityTags.STRATEGY.of(strategy), //
				QueryDerivationObservation.LowCardinalityTags.METHOD.of(methodName), //
				QueryDerivationObservation.LowCardinalityTags.REPOSITORY.of(repositoryName), //
				QueryDerivationObservation.LowCardinalityTags.DOMAIN_TYPE.of(domainName));
	}

	@Override
	public Tags getHighCardinalityTags(QueryDerivationContext context) {

		if (context.getQuery() != null) {
			return Tags.of(QueryDerivationObservation.HighCardinalityTags.QUERY.of(context.getQuery().toString()));
		}

		return Tags.empty();
	}
}
