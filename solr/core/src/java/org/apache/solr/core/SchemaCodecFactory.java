begin_unit
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|DocValuesFormat
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|lucene410
operator|.
name|Lucene410Codec
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Per-field CodecFactory implementation, extends Lucene's   * and returns postings format implementations according to the   * schema configuration.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SchemaCodecFactory
specifier|public
class|class
name|SchemaCodecFactory
extends|extends
name|CodecFactory
implements|implements
name|SolrCoreAware
block|{
DECL|field|codec
specifier|private
name|Codec
name|codec
decl_stmt|;
DECL|field|core
specifier|private
specifier|volatile
name|SolrCore
name|core
decl_stmt|;
comment|// TODO: we need to change how solr does this?
comment|// rather than a string like "Pulsing" you need to be able to pass parameters
comment|// and everything to a field in the schema, e.g. we should provide factories for
comment|// the Lucene's core formats (Memory, Pulsing, ...) and such.
comment|//
comment|// So I think a FieldType should return PostingsFormat, not a String.
comment|// how it constructs this from the XML... i don't care.
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
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
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|codec
operator|=
operator|new
name|Lucene410Codec
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|SchemaField
name|schemaField
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemaField
operator|!=
literal|null
condition|)
block|{
name|String
name|postingsFormatName
init|=
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getPostingsFormat
argument_list|()
decl_stmt|;
if|if
condition|(
name|postingsFormatName
operator|!=
literal|null
condition|)
block|{
return|return
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|postingsFormatName
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|getPostingsFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocValuesFormat
name|getDocValuesFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|SchemaField
name|schemaField
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|schemaField
operator|!=
literal|null
condition|)
block|{
name|String
name|docValuesFormatName
init|=
name|schemaField
operator|.
name|getType
argument_list|()
operator|.
name|getDocValuesFormat
argument_list|()
decl_stmt|;
if|if
condition|(
name|docValuesFormatName
operator|!=
literal|null
condition|)
block|{
return|return
name|DocValuesFormat
operator|.
name|forName
argument_list|(
name|docValuesFormatName
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|getDocValuesFormatForField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCodec
specifier|public
name|Codec
name|getCodec
parameter_list|()
block|{
assert|assert
name|core
operator|!=
literal|null
operator|:
literal|"inform must be called first"
assert|;
return|return
name|codec
return|;
block|}
block|}
end_class
end_unit
