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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import
begin_comment
comment|/**   * Exposes multi-valued view over a single-valued instance.  *<p>  * This can be used if you want to have one multi-valued implementation  * that works for single or multi-valued types.  */
end_comment
begin_class
DECL|class|SingletonSortedSetDocValues
specifier|final
class|class
name|SingletonSortedSetDocValues
extends|extends
name|RandomAccessOrds
block|{
DECL|field|in
specifier|private
specifier|final
name|SortedDocValues
name|in
decl_stmt|;
DECL|field|currentOrd
specifier|private
name|long
name|currentOrd
decl_stmt|;
DECL|field|ord
specifier|private
name|long
name|ord
decl_stmt|;
comment|/** Creates a multi-valued view over the provided SortedDocValues */
DECL|method|SingletonSortedSetDocValues
specifier|public
name|SingletonSortedSetDocValues
parameter_list|(
name|SortedDocValues
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
comment|/** Return the wrapped {@link SortedDocValues} */
DECL|method|getSortedDocValues
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|()
block|{
return|return
name|in
return|;
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
name|long
name|v
init|=
name|currentOrd
decl_stmt|;
name|currentOrd
operator|=
name|NO_MORE_ORDS
expr_stmt|;
return|return
name|v
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|currentOrd
operator|=
name|ord
operator|=
name|in
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
comment|// cast is ok: single-valued cannot exceed Integer.MAX_VALUE
return|return
name|in
operator|.
name|lookupOrd
argument_list|(
operator|(
name|int
operator|)
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|long
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ordAt
specifier|public
name|long
name|ordAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ord
return|;
block|}
annotation|@
name|Override
DECL|method|cardinality
specifier|public
name|int
name|cardinality
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|ord
operator|>>>
literal|63
argument_list|)
operator|^
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|termsEnum
specifier|public
name|TermsEnum
name|termsEnum
parameter_list|()
block|{
return|return
name|in
operator|.
name|termsEnum
argument_list|()
return|;
block|}
block|}
end_class
end_unit
