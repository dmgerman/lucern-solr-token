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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ArrayList
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
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import
begin_comment
comment|/**  * Shuffles field numbers around to try to trip bugs where field numbers  * are assumed to always be consistent across segments.  */
end_comment
begin_class
DECL|class|MismatchedLeafReader
specifier|public
class|class
name|MismatchedLeafReader
extends|extends
name|FilterLeafReader
block|{
DECL|field|shuffled
specifier|final
name|FieldInfos
name|shuffled
decl_stmt|;
comment|/** Creates a new reader which will renumber fields in {@code in} */
DECL|method|MismatchedLeafReader
specifier|public
name|MismatchedLeafReader
parameter_list|(
name|LeafReader
name|in
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|shuffled
operator|=
name|shuffleInfos
argument_list|(
name|in
operator|.
name|getFieldInfos
argument_list|()
argument_list|,
name|random
argument_list|)
expr_stmt|;
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
name|shuffled
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|document
argument_list|(
name|docID
argument_list|,
operator|new
name|MismatchedVisitor
argument_list|(
name|visitor
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|shuffleInfos
specifier|static
name|FieldInfos
name|shuffleInfos
parameter_list|(
name|FieldInfos
name|infos
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
comment|// first, shuffle the order
name|List
argument_list|<
name|FieldInfo
argument_list|>
name|shuffled
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|info
range|:
name|infos
control|)
block|{
name|shuffled
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|shuffled
argument_list|,
name|random
argument_list|)
expr_stmt|;
comment|// now renumber:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shuffled
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FieldInfo
name|oldInfo
init|=
name|shuffled
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// TODO: should we introduce "gaps" too?
name|FieldInfo
name|newInfo
init|=
operator|new
name|FieldInfo
argument_list|(
name|oldInfo
operator|.
name|name
argument_list|,
comment|// name
name|i
argument_list|,
comment|// number
name|oldInfo
operator|.
name|hasVectors
argument_list|()
argument_list|,
comment|// storeTermVector
name|oldInfo
operator|.
name|omitsNorms
argument_list|()
argument_list|,
comment|// omitNorms
name|oldInfo
operator|.
name|hasPayloads
argument_list|()
argument_list|,
comment|// storePayloads
name|oldInfo
operator|.
name|getIndexOptions
argument_list|()
argument_list|,
comment|// indexOptions
name|oldInfo
operator|.
name|getDocValuesType
argument_list|()
argument_list|,
comment|// docValuesType
name|oldInfo
operator|.
name|getDocValuesGen
argument_list|()
argument_list|,
comment|// dvGen
name|oldInfo
operator|.
name|attributes
argument_list|()
argument_list|)
decl_stmt|;
comment|// attributes
name|shuffled
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|newInfo
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FieldInfos
argument_list|(
name|shuffled
operator|.
name|toArray
argument_list|(
operator|new
name|FieldInfo
index|[
name|shuffled
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * StoredFieldsVisitor that remaps actual field numbers    * to our new shuffled ones.    */
comment|// TODO: its strange this part of our IR api exposes FieldInfo,
comment|// no other "user-accessible" codec apis do this?
DECL|class|MismatchedVisitor
class|class
name|MismatchedVisitor
extends|extends
name|StoredFieldVisitor
block|{
DECL|field|in
specifier|final
name|StoredFieldVisitor
name|in
decl_stmt|;
DECL|method|MismatchedVisitor
name|MismatchedVisitor
parameter_list|(
name|StoredFieldVisitor
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|binaryField
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
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|binaryField
argument_list|(
name|renumber
argument_list|(
name|fieldInfo
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stringField
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
name|in
operator|.
name|stringField
argument_list|(
name|renumber
argument_list|(
name|fieldInfo
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|intField
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
name|in
operator|.
name|intField
argument_list|(
name|renumber
argument_list|(
name|fieldInfo
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|longField
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
name|in
operator|.
name|longField
argument_list|(
name|renumber
argument_list|(
name|fieldInfo
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|floatField
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
name|in
operator|.
name|floatField
argument_list|(
name|renumber
argument_list|(
name|fieldInfo
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doubleField
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
name|in
operator|.
name|doubleField
argument_list|(
name|renumber
argument_list|(
name|fieldInfo
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsField
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
name|in
operator|.
name|needsField
argument_list|(
name|renumber
argument_list|(
name|fieldInfo
argument_list|)
argument_list|)
return|;
block|}
DECL|method|renumber
name|FieldInfo
name|renumber
parameter_list|(
name|FieldInfo
name|original
parameter_list|)
block|{
name|FieldInfo
name|renumbered
init|=
name|shuffled
operator|.
name|fieldInfo
argument_list|(
name|original
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|renumbered
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"stored fields sending bogus infos!"
argument_list|)
throw|;
block|}
return|return
name|renumbered
return|;
block|}
block|}
block|}
end_class
end_unit