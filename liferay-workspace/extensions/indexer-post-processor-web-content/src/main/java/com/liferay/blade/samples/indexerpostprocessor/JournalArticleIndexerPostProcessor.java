/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.samples.indexerpostprocessor;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

/**
 * @author Liferay
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = IndexerPostProcessor.class
)
public class JournalArticleIndexerPostProcessor
	implements IndexerPostProcessor {

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter booleanFilter, SearchContext searchContext)
		throws Exception {

		_log.log(LogService.LOG_INFO, "postProcessContextBooleanFilter");
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		_log.log(LogService.LOG_INFO, "postProcessContextQuery");
	}

	@Override
	public void postProcessDocument(Document document, Object obj)
		throws Exception {

		_log.log(LogService.LOG_INFO, "postProcessDocument");
	}

	@Override
	public void postProcessFullQuery(
			BooleanQuery fullQuery, SearchContext searchContext)
		throws Exception {

		_log.log(LogService.LOG_INFO, "postProcessFullQuery");
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter booleanFilter,
			SearchContext searchContext)
		throws Exception {

		_log.log(LogService.LOG_INFO, "postProcessSearchQuery");

		Collection<Locale> locales = _getMultiLanguageSearchLocales(
			searchContext);

		for (String field : _localizedFields) {
			String value = GetterUtil.getString(
				searchContext.getAttribute(field));

			if (Validator.isBlank(value)) {
				value = searchContext.getKeywords();
			}

			if (Validator.isBlank(value)) {
				continue;
			}

			if (Validator.isBlank(searchContext.getKeywords())) {
				BooleanQuery localizedQuery = new BooleanQueryImpl();

				for (Locale locale : locales) {
					String localizedField = DocumentImpl.getLocalizedName(
						locale, field);

					localizedQuery.addTerm(localizedField, value, false);
				}

				BooleanClauseOccur booleanClauseOccur =
					BooleanClauseOccur.SHOULD;

				if (searchContext.isAndSearch()) {
					booleanClauseOccur = BooleanClauseOccur.MUST;
				}

				searchQuery.add(localizedQuery, booleanClauseOccur);
			}
			else {
				for (Locale locale : locales) {
					String localizedField = DocumentImpl.getLocalizedName(
						locale, field);

					searchQuery.addTerm(localizedField, value, false);
				}
			}
		}
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		_log.log(LogService.LOG_INFO, "postProcessSearchQuery");
	}

	@Override
	public void postProcessSummary(
		Summary summary, Document document, Locale locale, String snippet) {

		_log.log(LogService.LOG_INFO, "postProcessSummary");
	}

	private Collection<Locale> _getMultiLanguageSearchLocales(
		SearchContext searchContext) {

		Set<Locale> locales = new HashSet<>();

		long[] groupIds = searchContext.getGroupIds();

		if (groupIds == null) {
			locales = LanguageUtil.getAvailableLocales(
				searchContext.getCompanyId());
		}
		else {
			for (long groupId : groupIds) {
				locales.addAll(LanguageUtil.getAvailableLocales(groupId));
			}
		}

		return locales;
	}

	private final String[] _localizedFields =
		{Field.TITLE, Field.DESCRIPTION, Field.CONTENT};

	@Reference
	private LogService _log;

}