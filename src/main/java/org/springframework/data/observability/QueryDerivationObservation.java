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

import io.micrometer.core.instrument.docs.DocumentedObservation;
import io.micrometer.core.instrument.docs.TagKey;

/**
 * Commons-based implemention of {@link DocumentedObservation}.
 *
 * @author Greg Turnquist
 * @since 3.0.0
 */
public enum QueryDerivationObservation implements DocumentedObservation {

	QUERY_DERIVATION_OBSERVATION {

		@Override
		public String getName() {
			return "spring.data.commons.query-derivation";
		}

		@Override
		public String getContextualName() {
			return "query-derivation";
		}

		@Override
		public TagKey[] getLowCardinalityTagKeys() {
			return LowCardinalityTags.values();
		}

		@Override
		public TagKey[] getHighCardinalityTagKeys() {
			return HighCardinalityTags.values();
		}

		@Override
		public String getPrefix() {
			return "spring.data.commons.";
		}
	};

	public enum LowCardinalityTags implements TagKey {

		/**
		 * The query lookup's strategy.
		 */
		STRATEGY {
			@Override
			public String getKey() {
				return "spring.data.commons.strategy";
			}
		},

		/**
		 * The "finder" method name
		 */
		METHOD {
			@Override
			public String getKey() {
				return "spring.data.commons.method";
			}
		},

		/**
		 * The arguments of the finder method.
		 */
		ARGUMENTS {
			@Override
			public String getKey() {
				return "spring.data.commons.arguments";
			}
		},

		/**
		 * The repository of the query.
		 */
		REPOSITORY {
			@Override
			public String getKey() {
				return "spring.data.commons.repository";
			}
		},

		/**
		 * The domain type.
		 */
		DOMAIN_TYPE {
			@Override
			public String getKey() {
				return "spring.data.commons.domain-type";
			}
		}

	}

	public enum HighCardinalityTags implements TagKey {

		/**
		 * A tag containing a query.
		 */
		QUERY {
			@Override
			public String getKey() {
				return "spring.data.commons.query";
			}
		}
	}
}
