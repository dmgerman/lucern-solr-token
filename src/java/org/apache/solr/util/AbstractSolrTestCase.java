begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|TestHarness
import|;
end_import
begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * An Abstract base class that makes writing Solr JUnit tests "easier"  *  *<p>  * Test classes that subclass this need only specify the path to the  * schema.xml file (:TODO: the solrconfig.xml as well) and write some  * testMethods.  This class takes care of creating/destroying the index,  * and provides several assert methods to assist you.  *</p>  *  * @see #setUp  * @see #tearDown  */
end_comment
begin_class
DECL|class|AbstractSolrTestCase
specifier|public
specifier|abstract
class|class
name|AbstractSolrTestCase
extends|extends
name|TestCase
block|{
comment|/**    * Harness initialized by initTestHarness.    *    *<p>    * For use in test methods as needed.    *</p>    */
DECL|field|h
specifier|protected
name|TestHarness
name|h
decl_stmt|;
comment|/**    * LocalRequestFactory initialized by initTestHarness using sensible    * defaults.    *    *<p>    * For use in test methods as needed.    *</p>    */
DECL|field|lrf
specifier|protected
name|TestHarness
operator|.
name|LocalRequestFactory
name|lrf
decl_stmt|;
comment|/**    * Subclasses must define this method to return the name of the    * schema.xml they wish to use.    */
DECL|method|getSchemaFile
specifier|public
specifier|abstract
name|String
name|getSchemaFile
parameter_list|()
function_decl|;
comment|/**    * Subclasses must define this method to return the name of the    * solrconfig.xml they wish to use.    */
DECL|method|getSolrConfigFile
specifier|public
specifier|abstract
name|String
name|getSolrConfigFile
parameter_list|()
function_decl|;
comment|/**    * The directory used to story the index managed by the TestHarness h    */
DECL|field|dataDir
specifier|protected
name|File
name|dataDir
decl_stmt|;
comment|/**    * Initializes things your test might need    *    *<ul>    *<li>Creates a dataDir in the "java.io.tmpdir"</li>    *<li>initializes the TestHarness h using this data directory, and getSchemaPath()</li>    *<li>initializes the LocalRequestFactory lrf using sensible defaults.</li>    *</ul>    *    */
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|h
operator|=
operator|new
name|TestHarness
argument_list|(
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|getSolrConfigFile
argument_list|()
argument_list|,
name|getSchemaFile
argument_list|()
argument_list|)
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|"version"
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shuts down the test harness, and makes the best attempt possible    * to delete dataDir, unless the system property "solr.test.leavedatadir"    * is set.    */
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|h
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|skip
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|skip
operator|&&
literal|0
operator|!=
name|skip
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"NOTE: per solr.test.leavedatadir, dataDir will not be removed: "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|recurseDelete
argument_list|(
name|dataDir
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"!!!! WARNING: best effort to remove "
operator|+
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" FAILED !!!!!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Validates an update XML String is successful    */
DECL|method|assertU
specifier|public
name|void
name|assertU
parameter_list|(
name|String
name|update
parameter_list|)
block|{
name|assertU
argument_list|(
literal|null
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
comment|/** Validates an update XML String is successful    */
DECL|method|assertU
specifier|public
name|void
name|assertU
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|update
parameter_list|)
block|{
try|try
block|{
name|String
name|m
init|=
operator|(
literal|null
operator|==
name|message
operator|)
condition|?
literal|""
else|:
name|message
operator|+
literal|" "
decl_stmt|;
name|String
name|res
init|=
name|h
operator|.
name|validateUpdate
argument_list|(
name|update
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|res
condition|)
block|{
name|fail
argument_list|(
name|m
operator|+
literal|"update was not successful: "
operator|+
name|res
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid XML"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Validates a query matches some XPath test expressions and closes the query */
DECL|method|assertQ
specifier|public
name|void
name|assertQ
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
block|{
name|assertQ
argument_list|(
literal|null
argument_list|,
name|req
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
comment|/** Validates a query matches some XPath test expressions and closes the query */
DECL|method|assertQ
specifier|public
name|void
name|assertQ
parameter_list|(
name|String
name|message
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
block|{
try|try
block|{
name|String
name|m
init|=
operator|(
literal|null
operator|==
name|message
operator|)
condition|?
literal|""
else|:
name|message
operator|+
literal|" "
decl_stmt|;
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|String
name|results
init|=
name|h
operator|.
name|validateXPath
argument_list|(
name|response
argument_list|,
name|tests
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|results
condition|)
block|{
name|fail
argument_list|(
name|m
operator|+
literal|"query failed XPath: "
operator|+
name|results
operator|+
literal|" xml response was: "
operator|+
name|response
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"XPath is invalid"
argument_list|,
name|e1
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Exception during query"
argument_list|,
name|e2
argument_list|)
throw|;
block|}
block|}
comment|/** Makes sure a query throws a SolrException with the listed response code */
DECL|method|assertQEx
specifier|public
name|void
name|assertQEx
parameter_list|(
name|String
name|message
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|int
name|code
parameter_list|)
block|{
try|try
block|{
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|sex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|code
argument_list|,
name|sex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Exception during query"
argument_list|,
name|e2
argument_list|)
throw|;
block|}
block|}
comment|/**    * @see TestHarness#optimize    */
DECL|method|optimize
specifier|public
name|String
name|optimize
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|h
operator|.
name|optimize
argument_list|()
return|;
block|}
comment|/**    * @see TestHarness#commit    */
DECL|method|commit
specifier|public
name|String
name|commit
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
return|return
name|h
operator|.
name|commit
argument_list|()
return|;
block|}
comment|/**    * Generates a simple&lt;add&gt;&lt;doc&gt;... XML String with no options    *    * @param fieldsAndValues 0th and Even numbered args are fields names odds are field values.    * @see #add    * @see #doc    */
DECL|method|adoc
specifier|public
name|String
name|adoc
parameter_list|(
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
block|{
name|Doc
name|d
init|=
name|doc
argument_list|(
name|fieldsAndValues
argument_list|)
decl_stmt|;
return|return
name|add
argument_list|(
name|d
argument_list|)
return|;
block|}
comment|/**    * Generates an&lt;add&gt;&lt;doc&gt;... XML String with options    * on the add.    *    * @param doc the Document to add    * @param args 0th and Even numbered args are param names, Odds are param values.    * @see #add    * @see #doc    */
DECL|method|add
specifier|public
name|String
name|add
parameter_list|(
name|Doc
name|doc
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
block|{
try|try
block|{
name|StringWriter
name|r
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
comment|// this is anoying
if|if
condition|(
literal|null
operator|==
name|args
operator|||
literal|0
operator|==
name|args
operator|.
name|length
condition|)
block|{
name|r
operator|.
name|write
argument_list|(
literal|"<add>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|write
argument_list|(
name|doc
operator|.
name|xml
argument_list|)
expr_stmt|;
name|r
operator|.
name|write
argument_list|(
literal|"</add>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|XML
operator|.
name|writeUnescapedXML
argument_list|(
name|r
argument_list|,
literal|"add"
argument_list|,
name|doc
operator|.
name|xml
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|args
argument_list|)
expr_stmt|;
block|}
return|return
name|r
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"this should never happen with a StringWriter"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Generates a&lt;delete&gt;... XML string for an ID    *    * @see TestHarness#deleteById    */
DECL|method|delI
specifier|public
name|String
name|delI
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|h
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**    * Generates a&lt;delete&gt;... XML string for an query    *    * @see TestHarness#deleteByQuery    */
DECL|method|delQ
specifier|public
name|String
name|delQ
parameter_list|(
name|String
name|q
parameter_list|)
block|{
return|return
name|h
operator|.
name|deleteByQuery
argument_list|(
name|q
argument_list|)
return|;
block|}
comment|/**    * Generates a simple&lt;doc&gt;... XML String with no options    *    * @param fieldsAndValues 0th and Even numbered args are fields names, Odds are field values.    * @see TestHarness#makeSimpleDoc    */
DECL|method|doc
specifier|public
name|Doc
name|doc
parameter_list|(
name|String
modifier|...
name|fieldsAndValues
parameter_list|)
block|{
name|Doc
name|d
init|=
operator|new
name|Doc
argument_list|()
decl_stmt|;
name|d
operator|.
name|xml
operator|=
name|h
operator|.
name|makeSimpleDoc
argument_list|(
name|fieldsAndValues
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|d
return|;
block|}
comment|/**    * Generates a SolrQueryRequest using the LocalRequestFactory    * @see #lrf    */
DECL|method|req
specifier|public
name|SolrQueryRequest
name|req
parameter_list|(
name|String
modifier|...
name|q
parameter_list|)
block|{
return|return
name|lrf
operator|.
name|makeRequest
argument_list|(
name|q
argument_list|)
return|;
block|}
comment|/** Neccessary to make method signatures un-ambiguous */
DECL|class|Doc
specifier|public
specifier|static
class|class
name|Doc
block|{
DECL|field|xml
specifier|public
name|String
name|xml
decl_stmt|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|xml
return|;
block|}
block|}
DECL|method|recurseDelete
specifier|public
specifier|static
name|boolean
name|recurseDelete
parameter_list|(
name|File
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|sub
range|:
name|f
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|recurseDelete
argument_list|(
name|sub
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
name|f
operator|.
name|delete
argument_list|()
return|;
block|}
block|}
end_class
end_unit
