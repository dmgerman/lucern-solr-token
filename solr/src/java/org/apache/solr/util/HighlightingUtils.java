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
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|ListIterator
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
name|DefaultSolrParams
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
name|HighlightParams
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
name|MapSolrParams
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|util
operator|.
name|SimpleOrderedMap
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
name|search
operator|.
name|DocIterator
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
name|search
operator|.
name|DocList
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
name|search
operator|.
name|SolrIndexSearcher
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
name|schema
operator|.
name|SchemaField
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
name|highlight
operator|.
name|SolrHighlighter
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
name|highlight
operator|.
name|DefaultSolrHighlighter
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * DEPRECATED Collection of Utility and Factory methods for Highlighting.  *  * @deprecated use DefaultSolrHighlighter  * @see DefaultSolrHighlighter  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|HighlightingUtils
specifier|public
class|class
name|HighlightingUtils
implements|implements
name|HighlightParams
block|{
DECL|field|DEFAULTS
specifier|static
name|SolrParams
name|DEFAULTS
init|=
literal|null
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SNIPPETS
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FRAGSIZE
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FORMATTER
argument_list|,
name|SIMPLE
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SIMPLE_PRE
argument_list|,
literal|"<em>"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SIMPLE_POST
argument_list|,
literal|"</em>"
argument_list|)
expr_stmt|;
name|DEFAULTS
operator|=
operator|new
name|MapSolrParams
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
DECL|field|HIGHLIGHTER
specifier|private
specifier|static
name|SolrHighlighterX
name|HIGHLIGHTER
init|=
operator|new
name|SolrHighlighterX
argument_list|()
decl_stmt|;
comment|/** Combine request parameters with highlighting defaults. */
DECL|method|getParams
specifier|static
name|SolrParams
name|getParams
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|DefaultSolrParams
argument_list|(
name|request
operator|.
name|getParams
argument_list|()
argument_list|,
name|DEFAULTS
argument_list|)
return|;
block|}
comment|/**     * @deprecated use DefaultSolrHighlighter     * @see DefaultSolrHighlighter#isHighlightingEnabled     */
annotation|@
name|Deprecated
DECL|method|isHighlightingEnabled
specifier|public
specifier|static
name|boolean
name|isHighlightingEnabled
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|HIGHLIGHTER
operator|.
name|isHighlightingEnabled
argument_list|(
name|getParams
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
comment|/**     * @deprecated use DefaultSolrHighlighter     * @see DefaultSolrHighlighter     */
annotation|@
name|Deprecated
DECL|method|getHighlighter
specifier|public
specifier|static
name|Highlighter
name|getHighlighter
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|HIGHLIGHTER
operator|.
name|getHighlighterX
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|request
argument_list|)
return|;
block|}
comment|/**     * @deprecated use DefaultSolrHighlighter     * @see DefaultSolrHighlighter#getHighlightFields     */
annotation|@
name|Deprecated
DECL|method|getHighlightFields
specifier|public
specifier|static
name|String
index|[]
name|getHighlightFields
parameter_list|(
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|String
index|[]
name|defaultFields
parameter_list|)
block|{
return|return
name|HIGHLIGHTER
operator|.
name|getHighlightFields
argument_list|(
name|query
argument_list|,
name|request
argument_list|,
name|defaultFields
argument_list|)
return|;
block|}
comment|/**     * @deprecated use DefaultSolrHighlighter     * @see DefaultSolrHighlighter     */
annotation|@
name|Deprecated
DECL|method|getMaxSnippets
specifier|public
specifier|static
name|int
name|getMaxSnippets
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|HIGHLIGHTER
operator|.
name|getMaxSnippetsX
argument_list|(
name|fieldName
argument_list|,
name|request
argument_list|)
return|;
block|}
comment|/**     * @deprecated use DefaultSolrHighlighter     * @see DefaultSolrHighlighter     */
annotation|@
name|Deprecated
DECL|method|getFormatter
specifier|public
specifier|static
name|Formatter
name|getFormatter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|HIGHLIGHTER
operator|.
name|getFormatterX
argument_list|(
name|fieldName
argument_list|,
name|request
argument_list|)
return|;
block|}
comment|/**     * @deprecated use DefaultSolrHighlighter     * @see DefaultSolrHighlighter     */
annotation|@
name|Deprecated
DECL|method|getFragmenter
specifier|public
specifier|static
name|Fragmenter
name|getFragmenter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|HIGHLIGHTER
operator|.
name|getFragmenterX
argument_list|(
name|fieldName
argument_list|,
name|request
argument_list|)
return|;
block|}
comment|/**     * @deprecated use DefaultSolrHighlighter     * @see DefaultSolrHighlighter#doHighlighting     */
annotation|@
name|Deprecated
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|doHighlighting
specifier|public
specifier|static
name|NamedList
name|doHighlighting
parameter_list|(
name|DocList
name|docs
parameter_list|,
name|Query
name|query
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|String
index|[]
name|defaultFields
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|HIGHLIGHTER
operator|.
name|doHighlighting
argument_list|(
name|docs
argument_list|,
name|query
argument_list|,
name|req
argument_list|,
name|defaultFields
argument_list|)
return|;
block|}
block|}
end_class
begin_comment
comment|/**  * subclass containing package protected versions of some protected methods, used for proxying calls to deprecated methods that have been moved and made protected.  */
end_comment
begin_class
DECL|class|SolrHighlighterX
class|class
name|SolrHighlighterX
extends|extends
name|DefaultSolrHighlighter
block|{
DECL|method|getHighlighterX
name|Highlighter
name|getHighlighterX
parameter_list|(
name|Query
name|query
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|getHighlighter
argument_list|(
name|query
argument_list|,
name|fieldName
argument_list|,
name|request
argument_list|)
return|;
block|}
DECL|method|getMaxSnippetsX
name|int
name|getMaxSnippetsX
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|getMaxSnippets
argument_list|(
name|fieldName
argument_list|,
name|HighlightingUtils
operator|.
name|getParams
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getFormatterX
name|Formatter
name|getFormatterX
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|getFormatter
argument_list|(
name|fieldName
argument_list|,
name|HighlightingUtils
operator|.
name|getParams
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getFragmenterX
name|Fragmenter
name|getFragmenterX
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
block|{
return|return
name|getFragmenter
argument_list|(
name|fieldName
argument_list|,
name|HighlightingUtils
operator|.
name|getParams
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
