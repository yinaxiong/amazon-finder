package com.mlesniak.amazon.web;

import com.mlesniak.amazon.backend.AmazonItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

public class ItemComponent extends Panel {
    private static final PackageResourceReference defaultImage =
            new PackageResourceReference(ItemComponent.class, "default.png");;

    public ItemComponent(String id, AmazonItem amazonItem) {
        super(id);

        add(new Label("title", amazonItem.getTitle()));
        add(new Label("price", amazonItem.getPrice()));

        Image image;
        if (StringUtils.isEmpty(amazonItem.getImageURL())) {
            image = new Image("image", defaultImage);
        } else {
            image = new Image("image", "");
            image.add(new AttributeModifier("src", amazonItem.getImageURL()));
        }

        ExternalLink link = new ExternalLink("link", amazonItem.getURL());
        link.add(image);
        add(link);
    }
}
