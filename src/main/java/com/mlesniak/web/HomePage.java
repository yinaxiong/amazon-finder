package com.mlesniak.web;

import com.mlesniak.amazon.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

public class HomePage extends WebPage {
    public HomePage(final PageParameters parameters) {
        super(parameters);

        TextField<String> keyword = new TextField<>("keyword");
        final Query query = new Query();
        Form<Query> form = new Form<Query>("form", new CompoundPropertyModel<Query>(query)) {
            @Override
            protected void onSubmit() {
                System.out.println(query);
                List<Item> items = performQuery(query);
                setResponsePage(new ResultPage(items));
            }
        };

        add(form);
        form.add(keyword);
    }

    public List<Item> performQuery(Query query) {
        AmazonRequest request = AmazonRequestBuilder.init()
                .addKeywords(query.getKeyword())
                .addSearchIndex(SearchIndex.Books)
                .addMaximumPrice(10000)
                .addMinimumPrice(1000)
                .build();

        List<Item> items = ItemConverter.convert(request.nextPage());
        //        List<Item> items = ItemConverter.convertFull(request);
        for (Item item : items) {
            System.out.println(item);
        }
        return items;
    }
}