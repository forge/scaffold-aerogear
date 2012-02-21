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
package org.jboss.forge.scaffold.aerogear.scenario.petclinic;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.scaffold.aerogear.AbstractAeroGearScaffoldTest;
import org.jboss.forge.shell.util.Streams;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Burr's example domain model from 7th Dec 2011.
 *
 * @author Richard Kennard
 */

@RunWith(Arquillian.class)
public class AeroGearScaffoldPetClinicTest extends AbstractAeroGearScaffoldTest
{
   // @Inject
   // private WebTest webTest;

   @Test
   public void testGenerate() throws Exception
   {
      //Project current = getShell().getCurrentProject();
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Owner");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field string --named address");
      getShell().execute("field string --named city");
      getShell().execute("field string --named telephone");
      getShell().execute("field string --named homePage");
      getShell().execute("field string --named email");
      getShell().execute("field date --named birthday");
      getShell().execute("entity --named Vet");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field string --named address");
      getShell().execute("field string --named city");
      getShell().execute("field string --named telephone");
      getShell().execute("field string --named homePage");
      getShell().execute("field string --named email");
      getShell().execute("field date --named birthday");
      getShell().execute("field date --named employedSince");
      getShell().execute("field int --named specialty");
      getShell().execute("entity --named Pet");
      getShell().execute("field string --named name");
      getShell().execute("field float --named weight");
      getShell().execute("field int --named type");
      getShell().execute("field boolean --named sendReminders");
      getShell().execute("field manyToOne --named owner --fieldType com.test.model.Owner");
      getShell().execute("entity --named Visit");
      getShell().execute("field string --named description");
      getShell().execute("field data --named visitDate");
      getShell().execute("field manyToOne --named pet --fieldType com.test.model.Pet");
      getShell().execute("field manyToOne --named vet --fieldType com.test.model.Vet");

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.model.*");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // index.html

      FileResource<?> index = web.getWebResource("index.html");
      Assert.assertTrue(index.exists());
      String contents = Streams.toString(index.getResourceInputStream());

      String metawidget = "\t\t\t\t<nav>\r\n" +
               "\t\t\t\t\t<a data-role=\"button\" href=\"scaffold/owner.html\">Owner</a>\r\n" +
               "\t\t\t\t\t<a data-role=\"button\" href=\"scaffold/pet.html\">Pet</a>\r\n" +
               "\t\t\t\t\t<a data-role=\"button\" href=\"scaffold/vet.html\">Vet</a>\r\n" +
               "\t\t\t\t\t<a data-role=\"button\" href=\"scaffold/visit.html\">Visit</a>\r\n" +
               "\t\t\t\t</nav>";

      Assert.assertTrue(contents.contains(metawidget));

      // owner.html

      FileResource<?> owner = web.getWebResource("scaffold/owner.html");
      Assert.assertTrue(owner.exists());
      contents = Streams.toString(owner.getResourceInputStream());

      // Search

      Assert.assertTrue(contents.contains( "\t<body onload=\"aerogear.initialize('../rest/owner')\">" ));

      metawidget = "\t\t\t\t<form name=\"search-form\" id=\"search-form\" data-ajax=\"false\">\n" +
               "\t\t\t\t\t<fieldset>\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"firstName\">First Name:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"firstName\" name=\"firstName\" type=\"text\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"lastName\">Last Name:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"lastName\" name=\"lastName\" type=\"text\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"address\">Address:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"address\" name=\"address\" type=\"text\"/>\r\n" +
               "\t\t\t\t\t\t</div>\n" +
               "\t\t\t\t\t\t<div data-role=\"controlgroup\" data-type=\"horizontal\" style=\"text-align: center\">\n" +
               "\t\t\t\t\t\t\t<input type=\"submit\" name=\"search\" onclick=\"return aerogear.search()\" data-icon=\"search\" value=\"Search\"/>\n" +
               "\t\t\t\t\t\t</div>\n" +
               "\t\t\t\t\t</fieldset>\n" +
               "\t\t\t\t</form>\n";

      Assert.assertTrue(contents.contains(metawidget));

      metawidget = "\t\t\t\t<table id=\"search-results\">\r\n" +
               "\t\t\t\t\t<thead>\r\n" +
               "\t\t\t\t\t\t<tr>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-firstName\">First Name</th>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-lastName\">Last Name</th>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-address\">Address</th>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-city\">City</th>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-telephone\">Telephone</th>\r\n" +
               "\t\t\t\t\t\t</tr>\r\n" +
               "\t\t\t\t\t</thead>\r\n" +
               "\t\t\t\t\t<tbody/>\r\n" +
               "\t\t\t\t</table>\n";

      Assert.assertTrue(contents.contains(metawidget));

      // View

      metawidget = "\t\t\t\t<fieldset id=\"view-fieldset\">\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"firstName\">First Name:</label>\r\n" +
               "\t\t\t\t\t\t<div id=\"firstName\"></div>\r\n" +
               "\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"lastName\">Last Name:</label>\r\n" +
               "\t\t\t\t\t\t<div id=\"lastName\"></div>\r\n" +
               "\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"address\">Address:</label>\r\n" +
               "\t\t\t\t\t\t<div id=\"address\"></div>\r\n" +
               "\t\t\t\t\t</div>\r\n";

      Assert.assertTrue(contents.contains(metawidget));

      // pet.html

      FileResource<?> pet = web.getWebResource("scaffold/pet.html");
      Assert.assertTrue(pet.exists());
      contents = Streams.toString(pet.getResourceInputStream());

      Assert.assertTrue(contents.contains( "<select data-rest=\"owner\" id=\"owner\" name=\"owner\"></select>"));
      Assert.assertTrue(!contents.contains( "<label for=\"pet-owner-firstName\">First Name:</label>"));

      // visit.html

      FileResource<?> visit = web.getWebResource("scaffold/visit.html");
      Assert.assertTrue(visit.exists());
      contents = Streams.toString(pet.getResourceInputStream());

      Assert.assertTrue(contents.contains( "<select data-rest=\"owner\" id=\"owner\" name=\"owner\"></select>"));

      // Deploy to a real container and test

      //this.webTest.setup(project);
      //JavaClass clazz = this.webTest.from(current, AeroGearScaffoldPetClinicClient.class);

      //this.webTest.buildDefaultDeploymentMethod(project, clazz, Arrays.asList(
      //".addAsResource(\"META-INF/persistence.xml\", \"META-INF/persistence.xml\")"
       //));
      //this.webTest.addAsTestClass(project, clazz);

      getShell().execute("build");
   }
}
