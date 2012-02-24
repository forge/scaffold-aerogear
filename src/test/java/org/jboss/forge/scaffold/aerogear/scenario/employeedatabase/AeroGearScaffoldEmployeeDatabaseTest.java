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
package org.jboss.forge.scaffold.aerogear.scenario.employeedatabase;

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
 * Example for mobile clients.
 *
 * @author Richard Kennard
 */

@RunWith(Arquillian.class)
public class AeroGearScaffoldEmployeeDatabaseTest extends AbstractAeroGearScaffoldTest
{
   // @Inject
   // private WebTest webTest;

   @Test
   public void testGenerate() throws Exception
   {
      //Project current = getShell().getCurrentProject();
      Project project = setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Company");
      getShell().execute("field string --named name");
      getShell().execute("field boolean --named publiclyListed");
      queueInputLines("java.awt.Color");
      getShell().execute("field custom --named color");
      getShell().execute("entity --named Employee");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      queueInputLines("java.util.Date");
      getShell().execute("field custom --named dateOfBirth");
      getShell().execute("field int --named salary");
      getShell().execute("field manyToOne --named company --fieldType com.test.model.Company");

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.model.*");

      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      // index.html

      FileResource<?> index = web.getWebResource("index.html");
      Assert.assertTrue(index.exists());
      String contents = Streams.toString(index.getResourceInputStream());

      String metawidget = "\t\t\t\t<nav>\r\n" +
               "\t\t\t\t\t<a data-role=\"button\" href=\"scaffold/company.html\" rel=\"external\">Company</a>\r\n" +
               "\t\t\t\t\t<a data-role=\"button\" href=\"scaffold/employee.html\" rel=\"external\">Employee</a>\r\n" +
               "\t\t\t\t</nav>";

      Assert.assertTrue(contents.contains(metawidget));

      // company.html

      FileResource<?> owner = web.getWebResource("scaffold/company.html");
      Assert.assertTrue(owner.exists());
      contents = Streams.toString(owner.getResourceInputStream());

      // Search

      Assert.assertTrue(contents.contains( "\t<body onload=\"aerogear.initialize('../rest/company')\">" ));

      metawidget = "\t\t\t\t<form name=\"search-form\" id=\"search-form\" data-ajax=\"false\">\n" +
               "\t\t\t\t\t<fieldset>\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"name\">Name:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"name\" name=\"name\" type=\"search\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"publiclyListed\">Publicly Listed</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"publiclyListed\" name=\"publiclyListed\" type=\"checkbox\"/>\r\n" +
               "\t\t\t\t\t\t</div>\n" +
               "\t\t\t\t\t\t<div data-role=\"controlgroup\" data-type=\"horizontal\" class=\"buttons\">\n" +
               "\t\t\t\t\t\t\t<input type=\"submit\" name=\"search\" onclick=\"return aerogear.search()\" data-icon=\"search\" value=\"Search\"/>\n" +
               "\t\t\t\t\t\t</div>\n" +
               "\t\t\t\t\t</fieldset>\n" +
               "\t\t\t\t</form>\n";

      Assert.assertTrue(contents.contains(metawidget));

      metawidget = "\t\t\t\t<table id=\"search-results\">\r\n" +
               "\t\t\t\t\t<thead>\r\n" +
               "\t\t\t\t\t\t<tr>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-name\">Name</th>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-publiclyListed\">Publicly Listed</th>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-color\">Color</th>\r\n" +
               "\t\t\t\t\t\t</tr>\r\n" +
               "\t\t\t\t\t</thead>\r\n" +
               "\t\t\t\t\t<tbody/>\r\n" +
               "\t\t\t\t</table>\n";

      Assert.assertTrue(contents.contains(metawidget));

      // View

      metawidget = "\t\t\t\t<fieldset id=\"view-fieldset\">\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"name\">Name:</label>\r\n" +
               "\t\t\t\t\t\t<output id=\"name\" name=\"name\"></output>\r\n" +
               "\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"publiclyListed\">Publicly Listed:</label>\r\n" +
               "\t\t\t\t\t\t<output id=\"publiclyListed\" name=\"publiclyListed\"></output>\r\n" +
               "\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"color\">Color:</label>\r\n" +
               "\t\t\t\t\t\t<output id=\"color\" name=\"color\"></output>\r\n" +
               "\t\t\t\t\t</div>\n";

      Assert.assertTrue(contents.contains(metawidget));

      // Create

      metawidget = "\t\t\t\t\t<fieldset id=\"create-fieldset\">\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"name\">Name:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"name\" name=\"name\" placeholder=\"Your Name\" type=\"text\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"publiclyListed\">Publicly Listed</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"publiclyListed\" name=\"publiclyListed\" placeholder=\"Your Publicly Listed\" type=\"checkbox\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"color\">Color:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"color\" name=\"color\" placeholder=\"Your Color\" type=\"color\"/>\r\n" +
               "\t\t\t\t\t\t</div>\n";

      Assert.assertTrue(contents.contains(metawidget));

      // employee.html

      owner = web.getWebResource("scaffold/employee.html");
      Assert.assertTrue(owner.exists());
      contents = Streams.toString(owner.getResourceInputStream());

      // Search

      Assert.assertTrue(contents.contains( "\t<body onload=\"aerogear.initialize('../rest/employee')\">" ));

      metawidget = "\t\t\t\t<form name=\"search-form\" id=\"search-form\" data-ajax=\"false\">\n" +
               "\t\t\t\t\t<fieldset>\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"firstName\">First Name:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"firstName\" name=\"firstName\" type=\"search\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"lastName\">Last Name:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"lastName\" name=\"lastName\" type=\"search\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"company\">Company:</label>\r\n" +
               "\t\t\t\t\t\t\t<select data-rest=\"company\" id=\"company\" name=\"company\"></select>\r\n" +
               "\t\t\t\t\t\t</div>\n" +
               "\t\t\t\t\t\t<div data-role=\"controlgroup\" data-type=\"horizontal\" class=\"buttons\">\n" +
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
               "\t\t\t\t\t\t\t<th id=\"search-results-dateOfBirth\">Date Of Birth</th>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-salary\">Salary</th>\r\n" +
               "\t\t\t\t\t\t\t<th id=\"search-results-company\">Company</th>\r\n" +
               "\t\t\t\t\t\t</tr>\r\n" +
               "\t\t\t\t\t</thead>\r\n" +
               "\t\t\t\t\t<tbody/>\r\n" +
               "\t\t\t\t</table>\n";

      Assert.assertTrue(contents.contains(metawidget));

      // View

      metawidget = "\t\t\t\t<fieldset id=\"view-fieldset\">\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"firstName\">First Name:</label>\r\n" +
               "\t\t\t\t\t\t<output id=\"firstName\" name=\"firstName\"></output>\r\n" +
               "\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"lastName\">Last Name:</label>\r\n" +
               "\t\t\t\t\t\t<output id=\"lastName\" name=\"lastName\"></output>\r\n" +
               "\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"dateOfBirth\">Date Of Birth:</label>\r\n" +
               "\t\t\t\t\t\t<output id=\"dateOfBirth\" name=\"dateOfBirth\"></output>\r\n" +
               "\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"salary\">Salary:</label>\r\n" +
               "\t\t\t\t\t\t<output id=\"salary\" name=\"salary\"></output>\r\n" +
               "\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t<label for=\"company\">Company:</label>\r\n" +
               "\t\t\t\t\t\t<output id=\"company\" name=\"company\"></output>\r\n" +
               "\t\t\t\t\t</div>\n";

      Assert.assertTrue(contents.contains(metawidget));

      // Create

      metawidget = "\t\t\t\t\t<fieldset id=\"create-fieldset\">\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"firstName\">First Name:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"firstName\" name=\"firstName\" placeholder=\"Your First Name\" type=\"text\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"lastName\">Last Name:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"lastName\" name=\"lastName\" placeholder=\"Your Last Name\" type=\"text\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"dateOfBirth\">Date Of Birth:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"dateOfBirth\" name=\"dateOfBirth\" placeholder=\"Your Date Of Birth\" type=\"date\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"salary\">Salary:</label>\r\n" +
               "\t\t\t\t\t\t\t<input id=\"salary\" name=\"salary\" placeholder=\"Your Salary\" type=\"number\"/>\r\n" +
               "\t\t\t\t\t\t</div>\r\n" +
               "\t\t\t\t\t\t<div>\r\n" +
               "\t\t\t\t\t\t\t<label for=\"company\">Company:</label>\r\n" +
               "\t\t\t\t\t\t\t<select data-rest=\"company\" id=\"company\" name=\"company\"></select>\r\n" +
               "\t\t\t\t\t\t</div>\n";

      Assert.assertTrue(contents.contains(metawidget));

      getShell().execute("build");
   }
}
