/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.scaffold.aerogear.metawidget.widgetbuilder;

import static org.jboss.forge.scaffold.aerogear.metawidget.inspector.ForgeInspectionResultConstants.*;
import static org.metawidget.inspector.InspectionResultConstants.*;

import java.util.Map;

import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlStub;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.html.widgetbuilder.HtmlOutput;
import org.metawidget.statically.html.widgetbuilder.HtmlSelect;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;

/**
 * Builds widgets with Forge-specific behaviours (such as AJAX lookups).
 *
 * @author Richard Kennard
 */

public class EntityWidgetBuilder
         implements WidgetBuilder<StaticXmlWidget, StaticXmlMetawidget>
{
   //
   // Public methods
   //

   @Override
   public StaticXmlWidget buildWidget(String elementName, Map<String, String> attributes, StaticXmlMetawidget metawidget)
   {
      // Suppress nested INVERSE ONE_TO_ONE, to avoid recursion

      if (TRUE.equals(attributes.get(ONE_TO_ONE)) && attributes.containsKey(INVERSE_RELATIONSHIP)
               && metawidget.getParent() != null)
      {
         return new StaticXmlStub();
      }

      String type = WidgetBuilderUtils.getActualClassOrType(attributes);
      String restLookup = attributes.get(MANY_TO_ONE);

      if (WidgetBuilderUtils.isReadOnly(attributes))
      {
         // Read-only REST_LOOKUP

         if (restLookup != null)
         {
            return new HtmlOutput();
         }
      }
      else
      {
         // Non read-only REST_LOOKUP

         if (restLookup != null)
         {
            HtmlSelect select = new HtmlSelect();
            select.putAttribute("data-rest", StringUtils.decapitalize(ClassUtils.getSimpleName(type)));
            return select;
         }
      }

      // Delegate to next WidgetBuilder in the chain

      return null;
   }
}
