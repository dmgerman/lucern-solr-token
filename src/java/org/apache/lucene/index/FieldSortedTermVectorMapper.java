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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Copyright 2007 The Apache Software Foundation  *<p/>  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * For each Field, store a sorted collection of {@link TermVectorEntry}s  *<p/>  * This is not thread-safe.  */
end_comment
begin_class
DECL|class|FieldSortedTermVectorMapper
specifier|public
class|class
name|FieldSortedTermVectorMapper
extends|extends
name|TermVectorMapper
block|{
DECL|field|fieldToTerms
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|SortedSet
argument_list|<
name|TermVectorEntry
argument_list|>
argument_list|>
name|fieldToTerms
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SortedSet
argument_list|<
name|TermVectorEntry
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|currentSet
specifier|private
name|SortedSet
argument_list|<
name|TermVectorEntry
argument_list|>
name|currentSet
decl_stmt|;
DECL|field|currentField
specifier|private
name|String
name|currentField
decl_stmt|;
DECL|field|comparator
specifier|private
name|Comparator
argument_list|<
name|TermVectorEntry
argument_list|>
name|comparator
decl_stmt|;
comment|/**    *    * @param comparator A Comparator for sorting {@link TermVectorEntry}s    */
DECL|method|FieldSortedTermVectorMapper
specifier|public
name|FieldSortedTermVectorMapper
parameter_list|(
name|Comparator
argument_list|<
name|TermVectorEntry
argument_list|>
name|comparator
parameter_list|)
block|{
name|this
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
block|}
DECL|method|FieldSortedTermVectorMapper
specifier|public
name|FieldSortedTermVectorMapper
parameter_list|(
name|boolean
name|ignoringPositions
parameter_list|,
name|boolean
name|ignoringOffsets
parameter_list|,
name|Comparator
argument_list|<
name|TermVectorEntry
argument_list|>
name|comparator
parameter_list|)
block|{
name|super
argument_list|(
name|ignoringPositions
argument_list|,
name|ignoringOffsets
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|map
specifier|public
name|void
name|map
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|frequency
parameter_list|,
name|TermVectorOffsetInfo
index|[]
name|offsets
parameter_list|,
name|int
index|[]
name|positions
parameter_list|)
block|{
name|TermVectorEntry
name|entry
init|=
operator|new
name|TermVectorEntry
argument_list|(
name|currentField
argument_list|,
name|term
argument_list|,
name|frequency
argument_list|,
name|offsets
argument_list|,
name|positions
argument_list|)
decl_stmt|;
name|currentSet
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setExpectations
specifier|public
name|void
name|setExpectations
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|storeOffsets
parameter_list|,
name|boolean
name|storePositions
parameter_list|)
block|{
name|currentSet
operator|=
operator|new
name|TreeSet
argument_list|<
name|TermVectorEntry
argument_list|>
argument_list|(
name|comparator
argument_list|)
expr_stmt|;
name|currentField
operator|=
name|field
expr_stmt|;
name|fieldToTerms
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|currentSet
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the mapping between fields and terms, sorted by the comparator    *    * @return A map between field names and {@link java.util.SortedSet}s per field.  SortedSet entries are {@link TermVectorEntry}    */
DECL|method|getFieldToTerms
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SortedSet
argument_list|<
name|TermVectorEntry
argument_list|>
argument_list|>
name|getFieldToTerms
parameter_list|()
block|{
return|return
name|fieldToTerms
return|;
block|}
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|TermVectorEntry
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comparator
return|;
block|}
block|}
end_class
end_unit
