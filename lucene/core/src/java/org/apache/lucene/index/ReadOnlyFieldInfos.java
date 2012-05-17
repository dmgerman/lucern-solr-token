begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Collections
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
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|index
operator|.
name|FieldInfo
operator|.
name|IndexOptions
import|;
end_import
begin_comment
comment|// nocommit: temporary
end_comment
begin_class
DECL|class|ReadOnlyFieldInfos
specifier|public
specifier|final
class|class
name|ReadOnlyFieldInfos
extends|extends
name|FieldInfos
block|{
comment|// nocommit
DECL|field|hasFreq
specifier|private
specifier|final
name|boolean
name|hasFreq
decl_stmt|;
DECL|field|hasProx
specifier|private
specifier|final
name|boolean
name|hasProx
decl_stmt|;
DECL|field|hasVectors
specifier|private
specifier|final
name|boolean
name|hasVectors
decl_stmt|;
DECL|field|hasNorms
specifier|private
specifier|final
name|boolean
name|hasNorms
decl_stmt|;
DECL|field|hasDocValues
specifier|private
specifier|final
name|boolean
name|hasDocValues
decl_stmt|;
DECL|field|byNumber
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|FieldInfo
argument_list|>
name|byNumber
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|byName
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|byName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|Collection
argument_list|<
name|FieldInfo
argument_list|>
name|values
decl_stmt|;
comment|// for an unmodifiable iterator
DECL|method|ReadOnlyFieldInfos
specifier|public
name|ReadOnlyFieldInfos
parameter_list|(
name|FieldInfo
index|[]
name|infos
parameter_list|)
block|{
name|boolean
name|hasVectors
init|=
literal|false
decl_stmt|;
name|boolean
name|hasProx
init|=
literal|false
decl_stmt|;
name|boolean
name|hasFreq
init|=
literal|false
decl_stmt|;
name|boolean
name|hasNorms
init|=
literal|false
decl_stmt|;
name|boolean
name|hasDocValues
init|=
literal|false
decl_stmt|;
for|for
control|(
name|FieldInfo
name|info
range|:
name|infos
control|)
block|{
assert|assert
operator|!
name|byNumber
operator|.
name|containsKey
argument_list|(
name|info
operator|.
name|number
argument_list|)
assert|;
name|byNumber
operator|.
name|put
argument_list|(
name|info
operator|.
name|number
argument_list|,
name|info
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|byName
operator|.
name|containsKey
argument_list|(
name|info
operator|.
name|name
argument_list|)
assert|;
name|byName
operator|.
name|put
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|hasVectors
operator||=
name|info
operator|.
name|hasVectors
argument_list|()
expr_stmt|;
name|hasProx
operator||=
name|info
operator|.
name|isIndexed
argument_list|()
operator|&&
name|info
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|hasFreq
operator||=
name|info
operator|.
name|isIndexed
argument_list|()
operator|&&
name|info
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
expr_stmt|;
name|hasNorms
operator||=
name|info
operator|.
name|hasNorms
argument_list|()
expr_stmt|;
name|hasDocValues
operator||=
name|info
operator|.
name|hasDocValues
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|hasVectors
operator|=
name|hasVectors
expr_stmt|;
name|this
operator|.
name|hasProx
operator|=
name|hasProx
expr_stmt|;
name|this
operator|.
name|hasFreq
operator|=
name|hasFreq
expr_stmt|;
name|this
operator|.
name|hasNorms
operator|=
name|hasNorms
expr_stmt|;
name|this
operator|.
name|hasDocValues
operator|=
name|hasDocValues
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|byNumber
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasFreq
specifier|public
name|boolean
name|hasFreq
parameter_list|()
block|{
return|return
name|hasFreq
return|;
block|}
annotation|@
name|Override
DECL|method|hasProx
specifier|public
name|boolean
name|hasProx
parameter_list|()
block|{
return|return
name|hasProx
return|;
block|}
annotation|@
name|Override
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
return|return
name|hasVectors
return|;
block|}
annotation|@
name|Override
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
return|return
name|hasNorms
return|;
block|}
annotation|@
name|Override
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
name|hasDocValues
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
assert|assert
name|byNumber
operator|.
name|size
argument_list|()
operator|==
name|byName
operator|.
name|size
argument_list|()
assert|;
return|return
name|byNumber
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|FieldInfo
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|values
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
operator|(
name|fieldNumber
operator|>=
literal|0
operator|)
condition|?
name|byNumber
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
else|:
literal|null
return|;
block|}
comment|// nocommit: probably unnecessary
annotation|@
name|Override
DECL|method|clone
specifier|public
name|ReadOnlyFieldInfos
name|clone
parameter_list|()
block|{
name|FieldInfo
name|infos
index|[]
init|=
operator|new
name|FieldInfo
index|[
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FieldInfo
name|info
range|:
name|this
control|)
block|{
name|infos
index|[
name|upto
operator|++
index|]
operator|=
name|info
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ReadOnlyFieldInfos
argument_list|(
name|infos
argument_list|)
return|;
block|}
block|}
end_class
end_unit
