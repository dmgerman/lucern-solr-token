begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|core
operator|.
name|AbstractSolrEventListener
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
name|core
operator|.
name|SolrCore
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
name|search
operator|.
name|function
operator|.
name|FileFloatSource
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
begin_comment
comment|/**  * An event listener to reload ExternalFileFields for new searchers.  *  * Opening a new IndexSearcher will invalidate the internal caches used by  * {@link ExternalFileField}.  By default, these caches are reloaded lazily  * by the first search that uses them.  For large external files, this can  * slow down searches unacceptably.  *  * To reload the caches when the searcher is first opened, set up event  * listeners in your solrconfig.xml:  *  *<pre>  *&lt;listener event="newSearcher" class="org.apache.solr.schema.ExternalFileFieldReloader"/>  *&lt;listener event="firstSearcher" class="org.apache.solr.schema.ExternalFileFieldReloader"/>  *</pre>  *  * The caches will be reloaded for all ExternalFileFields in your schema after  * each commit.  */
end_comment
begin_class
DECL|class|ExternalFileFieldReloader
specifier|public
class|class
name|ExternalFileFieldReloader
extends|extends
name|AbstractSolrEventListener
block|{
DECL|field|datadir
specifier|private
name|String
name|datadir
decl_stmt|;
DECL|field|fieldSources
specifier|private
name|List
argument_list|<
name|FileFloatSource
argument_list|>
name|fieldSources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|ExternalFileFieldReloader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ExternalFileFieldReloader
specifier|public
name|ExternalFileFieldReloader
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|super
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|datadir
operator|=
name|core
operator|.
name|getDataDir
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|cacheFieldSources
argument_list|(
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
block|{
comment|// We need to reload the caches for the new searcher
if|if
condition|(
literal|null
operator|==
name|currentSearcher
operator|||
name|newSearcher
operator|.
name|getSchema
argument_list|()
operator|!=
name|currentSearcher
operator|.
name|getSchema
argument_list|()
condition|)
block|{
name|cacheFieldSources
argument_list|(
name|newSearcher
operator|.
name|getSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|newSearcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
for|for
control|(
name|FileFloatSource
name|fieldSource
range|:
name|fieldSources
control|)
block|{
name|fieldSource
operator|.
name|refreshCache
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Caches FileFloatSource's from all ExternalFileField instances in the schema */
DECL|method|cacheFieldSources
specifier|public
name|void
name|cacheFieldSources
parameter_list|(
name|IndexSchema
name|schema
parameter_list|)
block|{
name|fieldSources
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|SchemaField
name|field
range|:
name|schema
operator|.
name|getFields
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|FieldType
name|type
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|ExternalFileField
condition|)
block|{
name|ExternalFileField
name|eff
init|=
operator|(
name|ExternalFileField
operator|)
name|type
decl_stmt|;
name|fieldSources
operator|.
name|add
argument_list|(
name|eff
operator|.
name|getFileFloatSource
argument_list|(
name|field
argument_list|,
name|datadir
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Adding ExternalFileFieldReloader listener for field {}"
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
