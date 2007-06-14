begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
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
name|net
operator|.
name|URLEncoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
begin_comment
comment|/**  * This class is similar to MultiMapSolrParams except you can edit the   * parameters after it is initialized.  It has helper functions to set/add  * integer and boolean param values.  *   * @author ryan  * @since solr 1.3  */
end_comment
begin_class
DECL|class|ModifiableSolrParams
specifier|public
class|class
name|ModifiableSolrParams
extends|extends
name|SolrParams
block|{
DECL|field|vals
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|vals
decl_stmt|;
DECL|method|ModifiableSolrParams
specifier|public
name|ModifiableSolrParams
parameter_list|()
block|{
comment|// LinkedHashMap so params show up in CGI in the same order as they are entered
name|vals
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|ModifiableSolrParams
specifier|public
name|ModifiableSolrParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|v
parameter_list|)
block|{
name|vals
operator|=
name|v
expr_stmt|;
block|}
comment|//----------------------------------------------------------------
comment|//----------------------------------------------------------------
comment|/**    * Replace any existing parameter with the given name.  if val==null remove key from params completely.    */
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
operator|||
operator|(
name|val
operator|.
name|length
operator|==
literal|1
operator|&&
name|val
index|[
literal|0
index|]
operator|==
literal|null
operator|)
condition|)
block|{
name|vals
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|vals
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|val
parameter_list|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|val
parameter_list|)
block|{
name|set
argument_list|(
name|name
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the given values to any existing name    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|val
parameter_list|)
block|{
name|String
index|[]
name|old
init|=
name|vals
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
operator|||
name|val
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|String
index|[]
name|both
init|=
operator|new
name|String
index|[
name|old
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|old
control|)
block|{
name|both
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
name|both
index|[
name|i
operator|++
index|]
operator|=
literal|null
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|both
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|both
init|=
operator|new
name|String
index|[
name|old
operator|.
name|length
operator|+
name|val
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|old
control|)
block|{
name|both
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
for|for
control|(
name|String
name|v
range|:
name|val
control|)
block|{
name|both
index|[
name|i
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
name|vals
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|both
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|vals
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * remove a field at the given name    */
DECL|method|remove
specifier|public
name|String
index|[]
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|vals
operator|.
name|remove
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**     * remove the given value for the given name    *     * @return true if the item was removed, false if null or not present    */
DECL|method|remove
specifier|public
name|boolean
name|remove
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|String
index|[]
name|tmp
init|=
name|vals
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmp
operator|==
literal|null
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tmp
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tmp
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|String
index|[]
name|tmp2
init|=
operator|new
name|String
index|[
name|tmp
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|tmp2
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|tmp2
operator|=
literal|null
expr_stmt|;
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|tmp2
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|tmp
argument_list|,
name|i
operator|+
literal|1
argument_list|,
name|tmp2
argument_list|,
name|i
argument_list|,
name|tmp
operator|.
name|length
operator|-
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|name
argument_list|,
name|tmp2
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|//----------------------------------------------------------------
comment|//----------------------------------------------------------------
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
index|[]
name|v
init|=
name|vals
operator|.
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|length
operator|>
literal|0
condition|)
block|{
return|return
name|v
index|[
literal|0
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getParameterNamesIterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getParameterNamesIterator
parameter_list|()
block|{
return|return
name|vals
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|getParameterNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getParameterNames
parameter_list|()
block|{
return|return
name|vals
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|param
parameter_list|)
block|{
return|return
name|vals
operator|.
name|get
argument_list|(
name|param
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|128
argument_list|)
decl_stmt|;
try|try
block|{
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|entry
range|:
name|vals
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
index|[]
name|valarr
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|val
range|:
name|valarr
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|URLEncoder
operator|.
name|encode
argument_list|(
name|val
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|e
argument_list|)
throw|;
block|}
comment|// can't happen
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
