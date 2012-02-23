// Metawidget
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.jboss.forge.scaffold.aerogear.metawidget.widgetprocessor;

import java.util.Map;

import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.html.widgetbuilder.HtmlInput;
import org.metawidget.widgetprocessor.iface.WidgetProcessor;

/**
 * WidgetProcessor to replace &lt;input type="text"&gt; with &lt;input type="search"&gt;.
 *
 * @author Richard Kennard
 */

public class SearchInputWidgetProcessor
         implements WidgetProcessor<StaticXmlWidget, StaticXmlMetawidget>
{
   //
   // Public methods
   //

   @Override
   public StaticXmlWidget processWidget(StaticXmlWidget widget, String elementName, Map<String, String> attributes,
            StaticXmlMetawidget metawidget)
   {
      if (widget instanceof HtmlInput && "text".equals(widget.getAttribute("type")))
      {
         widget.putAttribute("type", "search");
      }

      return widget;
   }
}
