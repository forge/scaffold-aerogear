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

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.scaffold.aerogear.AbstractAeroGearScaffoldTest;
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
      //Project project = setupScaffoldProject();
      setupScaffoldProject();

      queueInputLines("");
      getShell().execute("entity --named Company");
      getShell().execute("field string --named name");
      queueInputLines("java.awt.Color");
      getShell().execute("field custom --named color");
      getShell().execute("entity --named Employee");
      getShell().execute("field string --named firstName");
      getShell().execute("field string --named lastName");
      getShell().execute("field date --named dateOfBirth");
      getShell().execute("field int --named salary");
      getShell().execute("field manyToOne --named company --fieldType com.test.model.Company");

      queueInputLines("", "", "", "", "");
      getShell()
               .execute("scaffold from-entity com.test.model.*");

      getShell().execute("build");
   }
}
