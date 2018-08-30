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

package com.liferay.document.library.indexerpostprocessor;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

@Component(
	immediate = true,
	property = {
		"indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	},
	service = IndexerPostProcessor.class
)
public class DLFileEntryIndexerPostProcessor implements IndexerPostProcessor {

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter booleanFilter, SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessDocument(Document document, Object obj)
		throws Exception {

		DLFileEntry dlFileEntry = (DLFileEntry)obj;

		Locale defaultLocale = PortalUtil.getSiteDefaultLocale(dlFileEntry.getGroupId());

		String localizedField = LocalizationUtil.getLocalizedName(Field.CONTENT, defaultLocale.toString());

		Field localizedContentField = document.getField(localizedField);

		document.addText(Field.CONTENT, localizedContentField.getValue());
	}

	@Override
	public void postProcessFullQuery(
			BooleanQuery fullQuery, SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter booleanFilter,
			SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

	}

	@Override
	public void postProcessSummary(
		Summary summary, Document document, Locale locale, String snippet) {

		if (Validator.isBlank(summary.getContent())) {
			String prefix = Field.SNIPPET + StringPool.UNDERLINE;

			String content = document.get(prefix + Field.CONTENT, Field.CONTENT);
	
			summary.setContent(content);
		}
	}

}