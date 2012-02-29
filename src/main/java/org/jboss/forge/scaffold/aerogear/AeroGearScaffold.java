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
package org.jboss.forge.scaffold.aerogear;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.scaffold.aerogear.metawidget.config.ForgeConfigReader;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.spec.javaee.ValidationFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.spi.TemplateResolver;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.ClassLoaderTemplateResolver;
import org.metawidget.statically.StaticUtils.IndentedWriter;
import org.metawidget.statically.html.StaticHtmlMetawidget;
import org.metawidget.statically.html.widgetbuilder.HtmlTag;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.StringUtils;

/**
 * Facet to generate an AeroGear UI.
 * <p>
 * This facet utilizes <a href="http://metawidget.org">Metawidget</a> internally. This enables the use of the Metawidget
 * SPI (pluggable WidgetBuilders, Layouts etc) for customizing the generated User Interface. For more information on
 * writing Metawidget plugins, see <a href="http://metawidget.org/documentation.php">the Metawidget documentation</a>.
 * <p>
 * This Facet does <em>not</em> require Metawidget to be in the final project.
 *
 * @author Richard Kennard
 */

@Alias("aerogear")
@RequiresFacet({ WebResourceFacet.class,
         DependencyFacet.class,
         PersistenceFacet.class,
         EJBFacet.class,
         CDIFacet.class,
         ValidationFacet.class })
public class AeroGearScaffold extends BaseJavaEEFacet implements ScaffoldProvider
{
   //
   // Private statics
   //

   private static final String ACTIVATOR_TEMPLATE = "scaffold/aerogear/JaxRsActivator.jv";
   private static final String OBJECT_MAPPER_PROVIDER_TEMPLATE = "scaffold/aerogear/ObjectMapperProvider.jv";
   private static final String SERVICE_TEMPLATE = "scaffold/aerogear/Service.jv";
   private static final String CURRENT_TEMPLATE = "scaffold/aerogear/current.html";
   private static final String INDEX_TEMPLATE = "scaffold/aerogear/index.html";

   //
   // Protected members (nothing is private, to help subclassing)
   //

   protected CompiledTemplateResource serviceTemplate;
   protected int serviceQbeMetawidgetIndent;

   protected CompiledTemplateResource currentTemplate;
   protected int currentTemplateEntityMetawidgetIndent;
   protected int currentTemplateViewMetawidgetIndent;
   protected int currentTemplateSearchMetawidgetIndent;
   protected int currentTemplateServiceMetawidgetIndent;

   protected CompiledTemplateResource activatorTemplate;
   protected CompiledTemplateResource objectMapperProviderTemplate;

   protected CompiledTemplateResource indexTemplate;
   protected int indexTemplateIndent;

   protected TemplateResolver<ClassLoader> resolver;

   protected final ShellPrompt prompt;
   protected final TemplateCompiler compiler;
   protected StaticHtmlMetawidget entityMetawidget;
   protected StaticHtmlMetawidget searchMetawidget;
   protected StaticHtmlMetawidget serviceMetawidget;
   protected StaticJavaMetawidget qbeMetawidget;

   //
   // Constructor
   //

   @Inject
   public AeroGearScaffold(final ShellPrompt prompt,
            final TemplateCompiler compiler,
            final DependencyInstaller installer)
   {
      super(installer);

      this.prompt = prompt;
      this.compiler = compiler;

      this.resolver = new ClassLoaderTemplateResolver(AeroGearScaffold.class.getClassLoader());

      if (this.compiler != null)
      {
         this.compiler.getTemplateResolverFactory().addResolver(this.resolver);
      }
   }

   //
   // Public methods
   //

   @Override
   public List<Resource<?>> setup(final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> resources = generateIndex(template, overwrite);

      return resources;
   }

   /**
    * Overridden to setup the Metawidgets.
    * <p>
    * Metawidgets must be configured per project <em>and per Forge invocation</em>. It is not sufficient to simply
    * configure them in <code>setup</code> because the user may restart Forge and not run <code>scaffold setup</code> a
    * second time.
    */

   @Override
   public void setProject(Project project)
   {
      super.setProject(project);

      ForgeConfigReader configReader = new ForgeConfigReader(this.project);

      this.entityMetawidget = new StaticHtmlMetawidget();
      this.entityMetawidget.setConfigReader(configReader);
      this.entityMetawidget.setConfig("scaffold/aerogear/metawidget-entity.xml");

      this.searchMetawidget = new StaticHtmlMetawidget();
      this.searchMetawidget.setConfigReader(configReader);
      this.searchMetawidget.setConfig("scaffold/aerogear/metawidget-search.xml");

      this.serviceMetawidget = new StaticHtmlMetawidget();
      this.serviceMetawidget.setConfigReader(configReader);
      this.serviceMetawidget.setConfig("scaffold/aerogear/metawidget-service.xml");

      this.qbeMetawidget = new StaticJavaMetawidget();
      this.qbeMetawidget.setConfigReader(configReader);
      this.qbeMetawidget.setConfig("scaffold/aerogear/metawidget-qbe.xml");
   }

   @Override
   public List<Resource<?>> generateFromEntity(final Resource<?> template, final JavaClass entity,
            final boolean overwrite)
   {
      // Track the list of resources generated

      List<Resource<?>> result = new ArrayList<Resource<?>>();
      try
      {
         JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
         WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

         loadTemplates();
         Map<Object, Object> context = CollectionUtils.newHashMap();
         context.put("entity", entity);
         String ccEntity = StringUtils.decapitalize(entity.getName());
         context.put("ccEntity", ccEntity);

         // Prepare qbeMetawidget
         this.qbeMetawidget.setPath(entity.getQualifiedName());
         StringWriter stringWriter = new StringWriter();
         this.qbeMetawidget.write(stringWriter, this.serviceQbeMetawidgetIndent);
         context.put("qbeMetawidget", stringWriter.toString().trim());
         context.put("qbeMetawidgetImports",
                  CollectionUtils.toString(this.qbeMetawidget.getImports(), ";\r\nimport ", true, false));

         // Create the Service for this entity
         JavaClass serviceBean = JavaParser.parse(JavaClass.class, this.serviceTemplate.render(context));
         serviceBean.setPackage(java.getBasePackage() + ".rest");
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(serviceBean),
                  serviceBean.toString(),
                  overwrite));

         // Set new context for view generation
         context = CollectionUtils.newHashMap();
         context.put("ccEntity", ccEntity);
         context.put("entityName", StringUtils.uncamelCase(entity.getName()));

         // Generate current.html
         this.entityMetawidget.setPath(entity.getQualifiedName());
         this.entityMetawidget.setReadOnly(false);
         writeMetawidget(context, "entityMetawidget", this.entityMetawidget, this.currentTemplateEntityMetawidgetIndent);
         this.entityMetawidget.setReadOnly(true);
         writeMetawidget(context, "viewMetawidget", this.entityMetawidget, this.currentTemplateViewMetawidgetIndent);
         this.searchMetawidget.setPath(entity.getQualifiedName());
         writeMetawidget(context, "searchMetawidget", this.searchMetawidget, this.currentTemplateSearchMetawidgetIndent);
         this.serviceMetawidget.setId("search-results");
         this.serviceMetawidget.setPath("java.util.List<" + entity.getQualifiedName() + ">");
         writeMetawidget(context, "serviceMetawidget", this.serviceMetawidget,
                  this.currentTemplateServiceMetawidgetIndent);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource("scaffold/" + ccEntity + ".html"),
                  this.currentTemplate.render(context),
                  overwrite));

         // Generate JaxRsActivator.jv
         JavaClass activator = JavaParser.parse(JavaClass.class, this.activatorTemplate.render(context));
         activator.setPackage(serviceBean.getPackage());
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(activator), activator.toString(),
                  true));

         // Generate ObjectMapperProviderTemplate.jv
         JavaClass objectMapperProvider = JavaParser.parse(JavaClass.class,
                  this.objectMapperProviderTemplate.render(context));
         objectMapperProvider.setPackage(serviceBean.getPackage());
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(serviceBean.getPackage() + "/ObjectMapperProvider.java"),
                  objectMapperProvider.toString(),
                  true));

         // Generate index.html
         result.add(generateIndex(overwrite));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default scaffolding: " + e.getMessage(), e);
      }
      return result;
   }

   /**
    * Overridden to require JAX-RS dependencies.
    */

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList(
               (Dependency) DependencyBuilder.create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec"),
               DependencyBuilder.create("org.jboss.spec.javax.xml.bind:jboss-jaxb-api_2.2_spec"),
               DependencyBuilder.create("org.codehaus.jackson:jackson-jaxrs:1.6.3")
               );
   }

   @Override
   public List<Resource<?>> generateIndex(final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

      loadTemplates();

      generateTemplates(overwrite);

      // Static resources

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
               web.getWebResource("/resources/css/images/ajax-loader.png"),
               getClass().getResourceAsStream("/scaffold/aerogear/ajax-loader.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/favicon.ico"),
               getClass().getResourceAsStream("/scaffold/aerogear/favicon.ico"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/js/forge-aerogear.js"),
               getClass().getResourceAsStream("/scaffold/aerogear/forge-aerogear.js"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
               web.getWebResource("/resources/css/images/icons-18-black.png"),
               getClass().getResourceAsStream("/scaffold/aerogear/icons-18-black.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
               web.getWebResource("/resources/css/images/icons-18-white.png"),
               getClass().getResourceAsStream("/scaffold/aerogear/icons-18-white.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
               web.getWebResource("/resources/css/images/icons-36-black.png"),
               getClass().getResourceAsStream("/scaffold/aerogear/icons-36-black.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
               web.getWebResource("/resources/css/images/icons-36-white.png"),
               getClass().getResourceAsStream("/scaffold/aerogear/icons-36-white.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/js/jquery-1.6.4.js"),
               getClass().getResourceAsStream("/scaffold/aerogear/jquery-1.6.4.js"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
               web.getWebResource("/resources/css/jquery.mobile-1.0.min.css"),
               getClass().getResourceAsStream("/scaffold/aerogear/jquery.mobile-1.0.min.css"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/css/forge-aerogear.css"),
               getClass().getResourceAsStream("/scaffold/aerogear/forge-aerogear.css"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
               web.getWebResource("/resources/js/jquery.mobile-1.0.min.js"),
               getClass().getResourceAsStream("/scaffold/aerogear/jquery.mobile-1.0.min.js"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
               web.getWebResource("../resources/META-INF/MANIFEST.MF"),
               getClass().getResourceAsStream("/scaffold/aerogear/MANIFEST.MF"), overwrite));

      return result;
   }

   @Override
   public List<Resource<?>> getGeneratedResources()
   {
      throw new RuntimeException("Not yet implemented!");
   }

   @Override
   public AccessStrategy getAccessStrategy()
   {
      return null;
   }

   @Override
   public TemplateStrategy getTemplateStrategy()
   {
      return null;
   }

   @Override
   public List<Resource<?>> generateTemplates(final boolean overwrite)
   {
      return null;
   }

   //
   // Protected methods (nothing is private, to help subclassing)
   //

   protected void loadTemplates()
   {
      if (this.serviceTemplate == null)
      {
         this.serviceTemplate = this.compiler.compile(SERVICE_TEMPLATE);
         String template = Streams.toString(this.serviceTemplate.getSourceTemplateResource().getInputStream());
         this.serviceQbeMetawidgetIndent = parseIndent(template, "@{qbeMetawidget}");
      }
      if (this.activatorTemplate == null)
      {
         this.activatorTemplate = this.compiler.compile(ACTIVATOR_TEMPLATE);
      }
      if (this.objectMapperProviderTemplate == null)
      {
         this.objectMapperProviderTemplate = this.compiler.compile(OBJECT_MAPPER_PROVIDER_TEMPLATE);
      }
      if (this.currentTemplate == null)
      {
         this.currentTemplate = this.compiler.compile(CURRENT_TEMPLATE);
         String template = Streams.toString(this.currentTemplate.getSourceTemplateResource().getInputStream());
         this.currentTemplateEntityMetawidgetIndent = parseIndent(template, "@{entityMetawidget}");
         this.currentTemplateViewMetawidgetIndent = parseIndent(template, "@{viewMetawidget}");
         this.currentTemplateSearchMetawidgetIndent = parseIndent(template, "@{searchMetawidget}");
         this.currentTemplateServiceMetawidgetIndent = parseIndent(template, "@{serviceMetawidget}");
      }
      if (this.indexTemplate == null)
      {
         this.indexTemplate = this.compiler.compile(INDEX_TEMPLATE);
         String template = Streams.toString(this.indexTemplate.getSourceTemplateResource().getInputStream());
         this.indexTemplateIndent = parseIndent(template, "@{navigation}");
      }
   }

   protected Node removeConflictingErrorPages(final ServletFacet servlet)
   {
      Node webXML = XMLParser.parse(servlet.getConfigFile().getResourceInputStream());
      Node root = webXML.getRoot();
      List<Node> errorPages = root.get("error-page");

      for (String code : Arrays.asList("404", "500"))
      {
         for (Node errorPage : errorPages)
         {
            if (code.equals(errorPage.getSingle("error-code").getText())
                     && this.prompt.promptBoolean("Your web.xml already contains an error page for " + code
                              + " status codes, replace it?"))
            {
               root.removeChild(errorPage);
            }
         }
      }
      return webXML;
   }

   /**
    * Generates the index page based on scaffolded entities.
    */

   protected Resource<?> generateIndex(final boolean overwrite)
            throws IOException
   {
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
      HtmlTag nav = new HtmlTag("nav");

      FileResource<?> scaffold = web.getWebResource("scaffold");
      for (Resource<?> resource : scaffold.listResources())
      {
         HtmlTag anchor = new HtmlTag("a");
         anchor.putAttribute("href", scaffold.getName() + '/' + resource.getName());
         anchor.putAttribute("data-role", "button");

         // Using rel='external' ensures our 'onload' gets called. This is much cleaner than using 'pageshow'. It also
         // makes the sub-pages bookmarkable

         anchor.putAttribute("rel", "external");
         anchor.setTextContent(StringUtils.uncamelCase(StringUtils.substringBefore(resource.getName(), ".html")));

         nav.getChildren().add(anchor);
      }

      Writer writer = new IndentedWriter(new StringWriter(), this.indexTemplateIndent);
      nav.write(writer);
      Map<Object, Object> context = CollectionUtils.newHashMap();
      context.put("navigation", writer.toString().trim());

      if (this.indexTemplate == null)
      {
         loadTemplates();
      }

      return ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/index.html"),
               this.indexTemplate.render(context), overwrite);
   }

   /**
    * Parses the given XML and determines the indent of the given String namespaces that Metawidget introduces.
    */

   protected int parseIndent(final String template, final String indentOf)
   {
      int indent = 0;
      int indexOf = template.indexOf(indentOf);

      while ((indexOf >= 0) && (template.charAt(indexOf) != '\n'))
      {
         if (template.charAt(indexOf) == '\t')
         {
            indent++;
         }

         indexOf--;
      }

      return indent;
   }

   /**
    * Writes the given Metawidget into the given context.
    */

   protected void writeMetawidget(Map<Object, Object> context, String key, StaticHtmlMetawidget metawidget,
            int metawidgetIndent)
   {
      StringWriter stringWriter = new StringWriter();
      metawidget.write(stringWriter, metawidgetIndent);
      context.put(key, stringWriter.toString().trim());
   }
}
