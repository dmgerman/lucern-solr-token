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
name|Set
import|;
end_import
begin_class
DECL|class|FieldFilterAtomicReader
specifier|public
specifier|final
class|class
name|FieldFilterAtomicReader
extends|extends
name|FilterAtomicReader
block|{
DECL|field|fields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|fields
decl_stmt|;
DECL|field|negate
specifier|private
specifier|final
name|boolean
name|negate
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|method|FieldFilterAtomicReader
specifier|public
name|FieldFilterAtomicReader
parameter_list|(
name|AtomicReader
name|in
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|,
name|boolean
name|negate
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|negate
operator|=
name|negate
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
operator|new
name|FieldInfos
argument_list|()
expr_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|in
operator|.
name|getFieldInfos
argument_list|()
control|)
block|{
if|if
condition|(
name|hasField
argument_list|(
name|fi
operator|.
name|name
argument_list|)
condition|)
block|{
name|fieldInfos
operator|.
name|add
argument_list|(
name|fi
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|hasField
name|boolean
name|hasField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|negate
operator|^
name|fields
operator|.
name|contains
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|InvertedFields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|InvertedFields
name|f
init|=
name|super
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|f
operator|=
operator|new
name|FieldFilterFields
argument_list|(
name|f
argument_list|)
expr_stmt|;
comment|// we need to check for emptyness, so we can return null:
return|return
operator|(
name|f
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|f
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
specifier|final
name|int
name|docID
parameter_list|,
specifier|final
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|super
operator|.
name|document
argument_list|(
name|docID
argument_list|,
operator|new
name|StoredFieldVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|binaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|byte
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|binaryField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stringField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|stringField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|intField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|intField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|longField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|longField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|floatField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|floatField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doubleField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|doubleField
argument_list|(
name|fieldInfo
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|needsField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|fieldInfo
operator|.
name|name
argument_list|)
condition|?
name|visitor
operator|.
name|needsField
argument_list|(
name|fieldInfo
argument_list|)
else|:
name|Status
operator|.
name|NO
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|InvertedFields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|InvertedFields
name|f
init|=
name|super
operator|.
name|fields
argument_list|()
decl_stmt|;
return|return
operator|(
name|f
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|FieldFilterFields
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
condition|?
name|super
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|normValues
specifier|public
name|DocValues
name|normValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
condition|?
name|super
operator|.
name|normValues
argument_list|(
name|field
argument_list|)
else|:
literal|null
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"FieldFilterAtomicReader(reader="
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|in
argument_list|)
operator|.
name|append
argument_list|(
literal|", fields="
argument_list|)
expr_stmt|;
if|if
condition|(
name|negate
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'!'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
name|fields
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|class|FieldFilterFields
specifier|private
class|class
name|FieldFilterFields
extends|extends
name|FilterFields
block|{
DECL|method|FieldFilterFields
specifier|public
name|FieldFilterFields
parameter_list|(
name|InvertedFields
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUniqueFieldCount
specifier|public
name|int
name|getUniqueFieldCount
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: add faster implementation!
name|int
name|c
init|=
literal|0
decl_stmt|;
specifier|final
name|FieldsEnum
name|it
init|=
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|c
operator|++
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|FilterFieldsEnum
argument_list|(
name|super
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|super
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|hasField
argument_list|(
name|f
argument_list|)
condition|)
return|return
name|f
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|hasField
argument_list|(
name|field
argument_list|)
condition|?
name|super
operator|.
name|terms
argument_list|(
name|field
argument_list|)
else|:
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
