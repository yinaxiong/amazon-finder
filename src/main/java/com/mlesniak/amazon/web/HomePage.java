package com.mlesniak.amazon.web;

import com.mlesniak.amazon.backend.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Collections;
import java.util.List;

public class HomePage extends WebPage {

    public static final String DEFAULT_SEARCH_INDEX = "Books";

    public HomePage(final PageParameters parameters) {
        this(parameters, Collections.<AmazonItem>emptyList());
    }

    public HomePage(final PageParameters parameters, List<AmazonItem> items) {
        super(parameters);

        // For testing I18N.
        // getSession().setLocale(Locale.GERMANY);

        final Query query = new Query();
        query.setKeyword(parameters.get("keyword").toString(null));
        query.setMinPrice(parameters.get("minPrice").toString(null));
        query.setMaxPrice(parameters.get("maxPrice").toString(null));
        query.setSearchIndex(SearchIndex.valueOf(parameters.get("searchIndex").toString(DEFAULT_SEARCH_INDEX)));
        parameters.clearNamed();

        TextField<String> keyword = new TextField<>("keyword");
        TextField<String> minPrice = new TextField<>("minPrice");
        TextField<String> maxPrice = new TextField<>("maxPrice");
        DropDownChoice<SearchIndex> searchIndex = new DropDownChoice<>("searchIndex", SearchIndex.asList());
        Form<Query> form = new Form<Query>("form", new CompoundPropertyModel<Query>(query)) {
            @Override
            protected void onSubmit() {
                System.out.println(query);
                List<AmazonItem> amazonItems = performQuery(query);
                parameters.set("keyword", query.getKeyword());
                parameters.set("minPrice", query.getMinPrice());
                parameters.set("maxPrice", query.getMaxPrice());
                parameters.set("searchIndex", query.getSearchIndex().toString());
                setResponsePage(new HomePage(parameters, amazonItems));
            }
        };

        add(form);
        form.add(keyword);
        form.add(minPrice);
        form.add(maxPrice);
        form.add(searchIndex);

        // Display items, if we already have some.
        RepeatingView repeater = new RepeatingView("repeater");
        for (AmazonItem amazonItem : items) {
            repeater.add(new ItemComponent(repeater.newChildId(), amazonItem));
        }
        add(repeater);
    }

    public List<AmazonItem> performQuery(Query query) {
        AmazonRequestBuilder builder = AmazonRequestBuilder.init()
                .addKeywords(query.getKeyword())
                .addSearchIndex(query.getSearchIndex());

        // TODO Error handling on parse error? In Validators?
        if (StringUtils.isNotEmpty(query.getMaxPrice())) {
            builder.addMaximumPrice(Integer.parseInt(query.getMaxPrice()));
        }
        if (StringUtils.isNotEmpty(query.getMinPrice())) {
            builder.addMinimumPrice(Integer.parseInt(query.getMinPrice()));
        }

        System.out.println(builder);
        AmazonRequest request = builder.build();

        List<AmazonItem> amazonItems = AmazonItemConverter.convertFull(request);
        for (AmazonItem amazonItem : amazonItems) {
            System.out.println(amazonItem);
        }
        return amazonItems;
    }
}
