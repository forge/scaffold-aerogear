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

package org.jboss.forge.scaffold.aerogear.metawidget.layout;

import java.util.Map;

import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.html.StaticHtmlMetawidget;
import org.metawidget.statically.html.widgetbuilder.HtmlLabel;
import org.metawidget.statically.html.widgetbuilder.HtmlTag;

/**
 * Layout tweaks to target mobile apps.
 *
 * @author Richard Kennard
 */

public class HtmlDivLayout
         extends org.metawidget.statically.html.layout.HtmlDivLayout
{

   //
   // Protected methods
   //

   /**
    * @param elementName can be useful if the Layout needs to call a WidgetProcessor
    */

   @Override
   protected HtmlTag createLabel( StaticXmlWidget widgetNeedingLabel, String elementName, Map<String, String> attributes, StaticHtmlMetawidget metawidget ) {

      HtmlLabel label = new HtmlLabel();
      String id = getWidgetId( widgetNeedingLabel );

      if ( id != null ) {
         label.putAttribute( "for", id );
      }

      String labelText = metawidget.getLabelString( attributes );
      if ( labelText != null && labelText.length() > 0 ) {

         // Checkboxes shouldn't get a colon in their label

         if ( !"checkbox".equals( widgetNeedingLabel.getAttribute("type"))) {
            labelText += ":";
         }

         label.setTextContent( labelText );
      }

      return label;
   }
}
