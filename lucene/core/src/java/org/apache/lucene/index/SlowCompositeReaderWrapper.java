begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MultiDocValues
operator|.
name|MultiSortedDocValues
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
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
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
name|MultiDocValues
operator|.
name|OrdinalMap
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
name|util
operator|.
name|Bits
import|;
end_import
begin_comment
comment|/**  * This class forces a composite reader (eg a {@link  * MultiReader} or {@link DirectoryReader}) to emulate a  * {@link LeafReader}.  This requires implementing the postings  * APIs on-the-fly, using the static methods in {@link  * MultiFields}, {@link MultiDocValues}, by stepping through  * the sub-readers to merge fields/terms, appending docs, etc.  *  *<p><b>NOTE</b>: this class almost always results in a  * performance hit.  If this is important to your use case,  * you'll get better performance by gathering the sub readers using  * {@link IndexReader#getContext()} to get the  * leaves and then operate per-LeafReader,  * instead of using this class.  */
end_comment
begin_class
DECL|class|SlowCompositeReaderWrapper
specifier|public
specifier|final
class|class
name|SlowCompositeReaderWrapper
extends|extends
name|LeafReader
block|{
DECL|field|in
specifier|private
specifier|final
name|CompositeReader
name|in
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|Fields
name|fields
decl_stmt|;
DECL|field|merging
specifier|private
specifier|final
name|boolean
name|merging
decl_stmt|;
comment|/** This method is sugar for getting an {@link LeafReader} from    * an {@link IndexReader} of any kind. If the reader is already atomic,    * it is returned unchanged, otherwise wrapped by this class.    */
DECL|method|wrap
specifier|public
specifier|static
name|LeafReader
name|wrap
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|instanceof
name|CompositeReader
condition|)
block|{
return|return
operator|new
name|SlowCompositeReaderWrapper
argument_list|(
operator|(
name|CompositeReader
operator|)
name|reader
argument_list|,
literal|false
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|reader
operator|instanceof
name|LeafReader
assert|;
return|return
operator|(
name|LeafReader
operator|)
name|reader
return|;
block|}
block|}
DECL|method|SlowCompositeReaderWrapper
name|SlowCompositeReaderWrapper
parameter_list|(
name|CompositeReader
name|reader
parameter_list|,
name|boolean
name|merging
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|in
operator|=
name|reader
expr_stmt|;
name|fields
operator|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|in
operator|.
name|registerParentReader
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|merging
operator|=
name|merging
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SlowCompositeReaderWrapper("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|addCoreClosedListener
specifier|public
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|addCoreClosedListenerAsReaderClosedListener
argument_list|(
name|in
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeCoreClosedListener
specifier|public
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|removeCoreClosedListenerAsReaderClosedListener
argument_list|(
name|in
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getNumericValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getDocsWithField
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBinaryDocValues
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getBinaryValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedNumericDocValues
specifier|public
name|SortedNumericDocValues
name|getSortedNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getSortedNumericValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|OrdinalMap
name|map
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|cachedOrdMaps
init|)
block|{
name|map
operator|=
name|cachedOrdMaps
operator|.
name|get
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
comment|// uncached, or not a multi dv
name|SortedDocValues
name|dv
init|=
name|MultiDocValues
operator|.
name|getSortedValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|instanceof
name|MultiSortedDocValues
condition|)
block|{
name|map
operator|=
operator|(
operator|(
name|MultiSortedDocValues
operator|)
name|dv
operator|)
operator|.
name|mapping
expr_stmt|;
if|if
condition|(
name|map
operator|.
name|owner
operator|==
name|getCoreCacheKey
argument_list|()
operator|&&
name|merging
operator|==
literal|false
condition|)
block|{
name|cachedOrdMaps
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dv
return|;
block|}
block|}
name|int
name|size
init|=
name|in
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|SortedDocValues
index|[]
name|values
init|=
operator|new
name|SortedDocValues
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|LeafReaderContext
name|context
init|=
name|in
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|!=
literal|null
operator|&&
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|SORTED
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SortedDocValues
name|v
init|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|DocValues
operator|.
name|emptySorted
argument_list|()
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|maxDoc
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiSortedDocValues
argument_list|(
name|values
argument_list|,
name|starts
argument_list|,
name|map
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSetDocValues
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|OrdinalMap
name|map
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|cachedOrdMaps
init|)
block|{
name|map
operator|=
name|cachedOrdMaps
operator|.
name|get
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
comment|// uncached, or not a multi dv
name|SortedSetDocValues
name|dv
init|=
name|MultiDocValues
operator|.
name|getSortedSetValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|instanceof
name|MultiSortedSetDocValues
condition|)
block|{
name|map
operator|=
operator|(
operator|(
name|MultiSortedSetDocValues
operator|)
name|dv
operator|)
operator|.
name|mapping
expr_stmt|;
if|if
condition|(
name|map
operator|.
name|owner
operator|==
name|getCoreCacheKey
argument_list|()
operator|&&
name|merging
operator|==
literal|false
condition|)
block|{
name|cachedOrdMaps
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dv
return|;
block|}
block|}
assert|assert
name|map
operator|!=
literal|null
assert|;
name|int
name|size
init|=
name|in
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|SortedSetDocValues
index|[]
name|values
init|=
operator|new
name|SortedSetDocValues
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|LeafReaderContext
name|context
init|=
name|in
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|!=
literal|null
operator|&&
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|SORTED_SET
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SortedSetDocValues
name|v
init|=
name|reader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|DocValues
operator|.
name|emptySortedSet
argument_list|()
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|maxDoc
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiSortedSetDocValues
argument_list|(
name|values
argument_list|,
name|starts
argument_list|,
name|map
argument_list|)
return|;
block|}
comment|// TODO: this could really be a weak map somewhere else on the coreCacheKey,
comment|// but do we really need to optimize slow-wrapper any more?
DECL|field|cachedOrdMaps
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalMap
argument_list|>
name|cachedOrdMaps
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getNormValues
specifier|public
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getNormValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|in
operator|.
name|numDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|in
operator|.
name|maxDoc
argument_list|()
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|in
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPointValues
specifier|public
name|PointValues
name|getPointValues
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiPointValues
operator|.
name|get
argument_list|(
name|in
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiFields
operator|.
name|getMergedFieldInfos
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCombinedCoreAndDeletesKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: as this is a wrapper, should we really close the delegate?
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|in
operator|.
name|leaves
argument_list|()
control|)
block|{
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
