begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request.json
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|json
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
name|Collection
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
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_class
DECL|class|ObjectUtil
specifier|public
class|class
name|ObjectUtil
block|{
DECL|class|ConflictHandler
specifier|public
specifier|static
class|class
name|ConflictHandler
block|{
DECL|method|isList
specifier|protected
name|boolean
name|isList
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|container
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|current
parameter_list|,
name|Object
name|previous
parameter_list|)
block|{
return|return
name|key
operator|!=
literal|null
operator|&&
operator|(
literal|"fields"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
operator|||
literal|"filter"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
operator|)
return|;
block|}
DECL|method|handleConflict
specifier|public
name|void
name|handleConflict
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|container
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|current
parameter_list|,
name|Object
name|previous
parameter_list|)
block|{
name|boolean
name|handleAsList
init|=
name|isList
argument_list|(
name|container
argument_list|,
name|path
argument_list|,
name|key
argument_list|,
name|current
argument_list|,
name|previous
argument_list|)
decl_stmt|;
if|if
condition|(
name|handleAsList
condition|)
block|{
name|container
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|makeList
argument_list|(
name|current
argument_list|,
name|previous
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|previous
operator|instanceof
name|Map
operator|&&
name|current
operator|instanceof
name|Map
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|prevMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|previous
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|currMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|current
decl_stmt|;
if|if
condition|(
name|prevMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|mergeMap
argument_list|(
name|prevMap
argument_list|,
name|currMap
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|container
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|prevMap
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// if we aren't handling as a list, and we aren't handling as a map, then just overwrite (i.e. nothing else to do)
return|return;
block|}
comment|// merges srcMap onto targetMap (i.e. changes targetMap but not srcMap)
DECL|method|mergeMap
specifier|public
name|void
name|mergeMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|targetMap
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|srcMap
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
if|if
condition|(
name|srcMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
comment|// to keep ordering correct, start with prevMap and merge in currMap
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|srcEntry
range|:
name|srcMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|subKey
init|=
name|srcEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|subVal
init|=
name|srcEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|subPrev
init|=
name|targetMap
operator|.
name|put
argument_list|(
name|subKey
argument_list|,
name|subVal
argument_list|)
decl_stmt|;
if|if
condition|(
name|subPrev
operator|!=
literal|null
condition|)
block|{
comment|// recurse
name|path
operator|.
name|add
argument_list|(
name|subKey
argument_list|)
expr_stmt|;
name|handleConflict
argument_list|(
name|targetMap
argument_list|,
name|path
argument_list|,
name|subKey
argument_list|,
name|subVal
argument_list|,
name|subPrev
argument_list|)
expr_stmt|;
name|path
operator|.
name|remove
argument_list|(
name|path
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|makeList
specifier|protected
name|Object
name|makeList
parameter_list|(
name|Object
name|current
parameter_list|,
name|Object
name|previous
parameter_list|)
block|{
name|ArrayList
name|lst
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|append
argument_list|(
name|lst
argument_list|,
name|previous
argument_list|)
expr_stmt|;
comment|// make the original value(s) come first
name|append
argument_list|(
name|lst
argument_list|,
name|current
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
DECL|method|append
specifier|protected
name|void
name|append
parameter_list|(
name|List
name|lst
parameter_list|,
name|Object
name|current
parameter_list|)
block|{
if|if
condition|(
name|current
operator|instanceof
name|Collection
condition|)
block|{
name|lst
operator|.
name|addAll
argument_list|(
operator|(
name|Collection
operator|)
name|current
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lst
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|mergeObjects
specifier|public
specifier|static
name|void
name|mergeObjects
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|top
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|,
name|Object
name|val
parameter_list|,
name|ConflictHandler
name|handler
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|outer
init|=
name|top
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|path
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sub
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|outer
operator|.
name|get
argument_list|(
name|path
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sub
operator|==
literal|null
condition|)
block|{
name|sub
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|outer
operator|.
name|put
argument_list|(
name|path
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
name|outer
operator|=
name|sub
expr_stmt|;
block|}
name|String
name|key
init|=
name|path
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
name|path
operator|.
name|get
argument_list|(
name|path
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
condition|)
block|{
name|Object
name|existingVal
init|=
name|outer
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingVal
operator|!=
literal|null
condition|)
block|{
comment|// OK, now we need to merge values
name|handler
operator|.
name|handleConflict
argument_list|(
name|outer
argument_list|,
name|path
argument_list|,
name|key
argument_list|,
name|val
argument_list|,
name|existingVal
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// merging at top level...
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|val
decl_stmt|;
name|handler
operator|.
name|mergeMap
argument_list|(
name|outer
argument_list|,
name|newMap
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
