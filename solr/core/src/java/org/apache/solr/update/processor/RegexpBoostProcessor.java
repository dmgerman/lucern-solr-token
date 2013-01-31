begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|common
operator|.
name|SolrInputDocument
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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|update
operator|.
name|AddUpdateCommand
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * A processor which will match content of "inputField" against regular expressions  * found in "boostFilename", and if it matches will return the corresponding boost  * value from the file and output this to "boostField" as a double value.  * If more than one pattern matches, the boosts from each are multiplied.  *<p>  * A typical use case may be to match a URL against patterns to boost or deboost  * web documents based on the URL itself:  *<pre>  * # Format of each line:&lt;pattern&gt;&lt;TAB&gt;&lt;boost&gt;  * # Example:  * https?://my.domain.com/temp.*  0.2  *</pre>  *<p>  * Both inputField, boostField and boostFilename are mandatory parameters.  */
end_comment
begin_class
DECL|class|RegexpBoostProcessor
specifier|public
class|class
name|RegexpBoostProcessor
extends|extends
name|UpdateRequestProcessor
block|{
DECL|field|INPUT_FIELD_PARAM
specifier|protected
specifier|static
specifier|final
name|String
name|INPUT_FIELD_PARAM
init|=
literal|"inputField"
decl_stmt|;
DECL|field|BOOST_FIELD_PARAM
specifier|protected
specifier|static
specifier|final
name|String
name|BOOST_FIELD_PARAM
init|=
literal|"boostField"
decl_stmt|;
DECL|field|BOOST_FILENAME_PARAM
specifier|protected
specifier|static
specifier|final
name|String
name|BOOST_FILENAME_PARAM
init|=
literal|"boostFilename"
decl_stmt|;
DECL|field|DEFAULT_INPUT_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_INPUT_FIELDNAME
init|=
literal|"url"
decl_stmt|;
DECL|field|DEFAULT_BOOST_FIELDNAME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_BOOST_FIELDNAME
init|=
literal|"urlboost"
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RegexpBoostProcessor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
DECL|field|inputFieldname
specifier|private
name|String
name|inputFieldname
init|=
name|DEFAULT_INPUT_FIELDNAME
decl_stmt|;
DECL|field|boostFieldname
specifier|private
name|String
name|boostFieldname
init|=
name|DEFAULT_BOOST_FIELDNAME
decl_stmt|;
DECL|field|boostFilename
specifier|private
name|String
name|boostFilename
decl_stmt|;
DECL|field|boostEntries
specifier|private
name|List
argument_list|<
name|BoostEntry
argument_list|>
name|boostEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|BoostEntry
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|BOOST_ENTRIES_CACHE_KEY
specifier|private
specifier|static
specifier|final
name|String
name|BOOST_ENTRIES_CACHE_KEY
init|=
literal|"boost-entries"
decl_stmt|;
DECL|method|RegexpBoostProcessor
name|RegexpBoostProcessor
parameter_list|(
name|SolrParams
name|parameters
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|,
name|UpdateRequestProcessor
name|nextProcessor
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|sharedObjectCache
parameter_list|)
block|{
name|super
argument_list|(
name|nextProcessor
argument_list|)
expr_stmt|;
name|this
operator|.
name|initParameters
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|boostFilename
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Null boost filename.  Disabling processor."
argument_list|)
expr_stmt|;
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
block|{
synchronized|synchronized
init|(
name|sharedObjectCache
init|)
block|{
name|List
argument_list|<
name|BoostEntry
argument_list|>
name|cachedBoostEntries
init|=
operator|(
name|List
argument_list|<
name|BoostEntry
argument_list|>
operator|)
name|sharedObjectCache
operator|.
name|get
argument_list|(
name|BOOST_ENTRIES_CACHE_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|cachedBoostEntries
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No pre-cached boost entry list found, initializing new"
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|openResource
argument_list|(
name|boostFilename
argument_list|)
decl_stmt|;
name|cachedBoostEntries
operator|=
name|initBoostEntries
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|sharedObjectCache
operator|.
name|put
argument_list|(
name|BOOST_ENTRIES_CACHE_KEY
argument_list|,
name|cachedBoostEntries
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Using cached boost entry list with "
operator|+
name|cachedBoostEntries
operator|.
name|size
argument_list|()
operator|+
literal|" elements."
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|boostEntries
operator|=
name|cachedBoostEntries
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"IOException while initializing boost entries from file "
operator|+
name|this
operator|.
name|boostFilename
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initParameters
specifier|private
name|void
name|initParameters
parameter_list|(
name|SolrParams
name|parameters
parameter_list|)
block|{
if|if
condition|(
name|parameters
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|setEnabled
argument_list|(
name|parameters
operator|.
name|getBool
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|inputFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|INPUT_FIELD_PARAM
argument_list|,
name|DEFAULT_INPUT_FIELDNAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|boostFieldname
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|BOOST_FIELD_PARAM
argument_list|,
name|DEFAULT_BOOST_FIELDNAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|boostFilename
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|BOOST_FILENAME_PARAM
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initBoostEntries
specifier|private
name|List
argument_list|<
name|BoostEntry
argument_list|>
name|initBoostEntries
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BoostEntry
argument_list|>
name|newBoostEntries
init|=
operator|new
name|ArrayList
argument_list|<
name|BoostEntry
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
comment|// Remove comments
name|line
operator|=
name|line
operator|.
name|replaceAll
argument_list|(
literal|"\\s+#.*$"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|line
operator|=
name|line
operator|.
name|replaceAll
argument_list|(
literal|"^#.*$"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Skip empty lines or comment lines
if|if
condition|(
name|line
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|String
index|[]
name|fields
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|String
name|regexp
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|String
name|boost
init|=
name|fields
index|[
literal|1
index|]
decl_stmt|;
name|newBoostEntries
operator|.
name|add
argument_list|(
operator|new
name|BoostEntry
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|regexp
argument_list|)
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|boost
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Read regexp "
operator|+
name|regexp
operator|+
literal|" with boost "
operator|+
name|boost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Malformed config input line: "
operator|+
name|line
operator|+
literal|" (expected 2 fields, got "
operator|+
name|fields
operator|.
name|length
operator|+
literal|" fields).  Skipping entry."
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|newBoostEntries
return|;
block|}
annotation|@
name|Override
DECL|method|processAdd
specifier|public
name|void
name|processAdd
parameter_list|(
name|AddUpdateCommand
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isEnabled
argument_list|()
condition|)
block|{
name|processBoost
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|processAdd
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
DECL|method|processBoost
specifier|public
name|void
name|processBoost
parameter_list|(
name|AddUpdateCommand
name|command
parameter_list|)
block|{
name|SolrInputDocument
name|document
init|=
name|command
operator|.
name|getSolrInputDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|document
operator|.
name|containsKey
argument_list|(
name|inputFieldname
argument_list|)
condition|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|document
operator|.
name|getFieldValue
argument_list|(
name|inputFieldname
argument_list|)
decl_stmt|;
name|double
name|boost
init|=
literal|1.0f
decl_stmt|;
for|for
control|(
name|BoostEntry
name|boostEntry
range|:
name|boostEntries
control|)
block|{
if|if
condition|(
name|boostEntry
operator|.
name|getPattern
argument_list|()
operator|.
name|matcher
argument_list|(
name|value
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Pattern match "
operator|+
name|boostEntry
operator|.
name|getPattern
argument_list|()
operator|.
name|pattern
argument_list|()
operator|+
literal|" for "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|boost
operator|=
operator|(
name|boostEntry
operator|.
name|getBoost
argument_list|()
operator|*
literal|1000
operator|)
operator|*
operator|(
name|boost
operator|*
literal|1000
operator|)
operator|/
literal|1000000
expr_stmt|;
block|}
block|}
name|document
operator|.
name|setField
argument_list|(
name|boostFieldname
argument_list|,
name|boost
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Value "
operator|+
name|boost
operator|+
literal|", applied to field "
operator|+
name|boostFieldname
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isEnabled
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
DECL|method|setEnabled
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|class|BoostEntry
specifier|private
specifier|static
class|class
name|BoostEntry
block|{
DECL|field|pattern
specifier|private
name|Pattern
name|pattern
decl_stmt|;
DECL|field|boost
specifier|private
name|double
name|boost
decl_stmt|;
DECL|method|BoostEntry
specifier|public
name|BoostEntry
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|double
name|d
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|d
expr_stmt|;
block|}
DECL|method|getPattern
specifier|public
name|Pattern
name|getPattern
parameter_list|()
block|{
return|return
name|pattern
return|;
block|}
DECL|method|getBoost
specifier|public
name|double
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
block|}
block|}
end_class
end_unit
