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
operator|.
name|MatchAllBits
import|;
end_import
begin_comment
comment|/**   * Exposes multi-valued view over a single-valued instance.  *<p>  * This can be used if you want to have one multi-valued implementation  * that works for single or multi-valued types.  */
end_comment
begin_class
DECL|class|SingletonSortedNumericDocValues
specifier|final
class|class
name|SingletonSortedNumericDocValues
extends|extends
name|SortedNumericDocValues
block|{
DECL|field|in
specifier|private
specifier|final
name|NumericDocValues
name|in
decl_stmt|;
DECL|field|docsWithField
specifier|private
specifier|final
name|Bits
name|docsWithField
decl_stmt|;
DECL|field|value
specifier|private
name|long
name|value
decl_stmt|;
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
DECL|method|SingletonSortedNumericDocValues
specifier|public
name|SingletonSortedNumericDocValues
parameter_list|(
name|NumericDocValues
name|in
parameter_list|,
name|Bits
name|docsWithField
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|docsWithField
operator|=
name|docsWithField
operator|instanceof
name|MatchAllBits
condition|?
literal|null
else|:
name|docsWithField
expr_stmt|;
block|}
comment|/** Return the wrapped {@link NumericDocValues} */
DECL|method|getNumericDocValues
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|()
block|{
return|return
name|in
return|;
block|}
comment|/** Return the wrapped {@link Bits} */
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|()
block|{
return|return
name|docsWithField
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
name|doc
parameter_list|)
block|{
name|value
operator|=
name|in
operator|.
name|get
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|docsWithField
operator|!=
literal|null
operator|&&
name|value
operator|==
literal|0
operator|&&
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
operator|==
literal|false
condition|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|count
operator|=
literal|1
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|valueAt
specifier|public
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
block|}
end_class
end_unit