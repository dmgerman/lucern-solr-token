begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|prefix
operator|.
name|PrefixTreeStrategy
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTree
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
name|spatial
operator|.
name|prefix
operator|.
name|tree
operator|.
name|SpatialPrefixTreeFactory
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgsParser
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
name|MapListener
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
begin_class
DECL|class|AbstractSpatialPrefixTreeFieldType
specifier|public
specifier|abstract
class|class
name|AbstractSpatialPrefixTreeFieldType
parameter_list|<
name|T
extends|extends
name|PrefixTreeStrategy
parameter_list|>
extends|extends
name|AbstractSpatialFieldType
argument_list|<
name|T
argument_list|>
block|{
comment|/** @see org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy#setDefaultFieldValuesArrayLen(int)  */
DECL|field|DEFAULT_FIELD_VALUES_ARRAY_LEN
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_FIELD_VALUES_ARRAY_LEN
init|=
literal|"defaultFieldValuesArrayLen"
decl_stmt|;
DECL|field|grid
specifier|protected
name|SpatialPrefixTree
name|grid
decl_stmt|;
DECL|field|distErrPct
specifier|private
name|Double
name|distErrPct
decl_stmt|;
DECL|field|defaultFieldValuesArrayLen
specifier|private
name|Integer
name|defaultFieldValuesArrayLen
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|schema
argument_list|,
name|args
argument_list|)
expr_stmt|;
comment|//Solr expects us to remove the parameters we've used.
name|MapListener
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|argsWrap
init|=
operator|new
name|MapListener
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|grid
operator|=
name|SpatialPrefixTreeFactory
operator|.
name|makeSPT
argument_list|(
name|argsWrap
argument_list|,
name|schema
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|args
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|argsWrap
operator|.
name|getSeenKeys
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|v
init|=
name|args
operator|.
name|remove
argument_list|(
name|SpatialArgsParser
operator|.
name|DIST_ERR_PCT
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
name|distErrPct
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|v
operator|=
name|args
operator|.
name|remove
argument_list|(
name|DEFAULT_FIELD_VALUES_ARRAY_LEN
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
name|defaultFieldValuesArrayLen
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newSpatialStrategy
specifier|protected
name|T
name|newSpatialStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|T
name|strat
init|=
name|newPrefixTreeStrategy
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|distErrPct
operator|!=
literal|null
condition|)
name|strat
operator|.
name|setDistErrPct
argument_list|(
name|distErrPct
argument_list|)
expr_stmt|;
if|if
condition|(
name|defaultFieldValuesArrayLen
operator|!=
literal|null
condition|)
name|strat
operator|.
name|setDefaultFieldValuesArrayLen
argument_list|(
name|defaultFieldValuesArrayLen
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|" strat: "
operator|+
name|strat
operator|+
literal|" maxLevels: "
operator|+
name|grid
operator|.
name|getMaxLevels
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO output maxDetailKm
return|return
name|strat
return|;
block|}
DECL|method|newPrefixTreeStrategy
specifier|protected
specifier|abstract
name|T
name|newPrefixTreeStrategy
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
block|}
end_class
end_unit
