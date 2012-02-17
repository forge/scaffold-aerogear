/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.scaffold.aerogear.metawidget.widgetprocessor;

import static org.metawidget.inspector.InspectionResultConstants.*;

import java.util.Map;

import junit.framework.TestCase;

import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.html.StaticHtmlMetawidget;
import org.metawidget.statically.html.widgetbuilder.HtmlInput;
import org.metawidget.util.CollectionUtils;

public class PlaceholderWidgetProcessorTest
         extends TestCase
{
   //
   // Public methods
   //

   public void testWidgetProcessor()
   {
      StaticXmlMetawidget metawidget = new StaticHtmlMetawidget();
      PlaceholderWidgetProcessor widgetProcessor = new PlaceholderWidgetProcessor();
      Map<String, String> attributes = CollectionUtils.newHashMap();
      attributes.put(NAME, "firstName");

      assertEquals("<input placeholder=\"Your First Name\"/>", widgetProcessor.processWidget(new HtmlInput(), PROPERTY, attributes, metawidget).toString());
   }
}
