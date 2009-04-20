begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
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
begin_comment
comment|/**  * This class enables caching of data obtained from the DB to avoid too many sql  * queries  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|CachedSqlEntityProcessor
specifier|public
class|class
name|CachedSqlEntityProcessor
extends|extends
name|SqlEntityProcessor
block|{
DECL|field|isFirst
specifier|private
name|boolean
name|isFirst
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|super
operator|.
name|cacheInit
argument_list|()
expr_stmt|;
name|isFirst
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
if|if
condition|(
name|dataSourceRowCache
operator|!=
literal|null
condition|)
return|return
name|getFromRowCacheTransformed
argument_list|()
return|;
if|if
condition|(
operator|!
name|isFirst
condition|)
return|return
literal|null
return|;
name|String
name|query
init|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|context
operator|.
name|getEntityAttribute
argument_list|(
literal|"query"
argument_list|)
argument_list|)
decl_stmt|;
name|isFirst
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|simpleCache
operator|!=
literal|null
condition|)
block|{
return|return
name|getSimpleCacheData
argument_list|(
name|query
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getIdCacheData
argument_list|(
name|query
argument_list|)
return|;
block|}
block|}
DECL|method|getAllNonCachedRows
specifier|protected
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getAllNonCachedRows
parameter_list|()
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|q
init|=
name|getQuery
argument_list|()
decl_stmt|;
name|initQuery
argument_list|(
name|resolver
operator|.
name|replaceTokens
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rowIterator
operator|==
literal|null
condition|)
return|return
name|rows
return|;
while|while
condition|(
name|rowIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|arow
init|=
name|rowIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|arow
operator|==
literal|null
condition|)
block|{
break|break;
block|}
else|else
block|{
name|rows
operator|.
name|add
argument_list|(
name|arow
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rows
return|;
block|}
block|}
end_class
end_unit
