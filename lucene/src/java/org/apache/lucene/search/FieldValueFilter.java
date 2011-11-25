begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|MatchNoBits
import|;
end_import
begin_comment
comment|/**  * A {@link Filter} that accepts all documents that have one or more values in a  * given field. This {@link Filter} request {@link Bits} from the  * {@link FieldCache} and build the bits if not present.  */
end_comment
begin_class
DECL|class|FieldValueFilter
specifier|public
class|class
name|FieldValueFilter
extends|extends
name|Filter
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|negate
specifier|private
specifier|final
name|boolean
name|negate
decl_stmt|;
comment|/**    * Creates a new {@link FieldValueFilter}    *     * @param field    *          the field to filter    */
DECL|method|FieldValueFilter
specifier|public
name|FieldValueFilter
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link FieldValueFilter}    *     * @param field    *          the field to filter    * @param negate    *          iff<code>true</code> all documents with no value in the given    *          field are accepted.    *     */
DECL|method|FieldValueFilter
specifier|public
name|FieldValueFilter
parameter_list|(
name|String
name|field
parameter_list|,
name|boolean
name|negate
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|negate
operator|=
name|negate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Bits
name|docsWithField
init|=
name|FieldCache
operator|.
name|DEFAULT
operator|.
name|getDocsWithField
argument_list|(
name|context
operator|.
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|negate
condition|)
block|{
if|if
condition|(
name|docsWithField
operator|instanceof
name|MatchAllBits
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|FieldCacheDocIdSet
argument_list|(
name|context
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptDocs
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
specifier|final
name|boolean
name|matchDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|!
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
else|else
block|{
if|if
condition|(
name|docsWithField
operator|instanceof
name|MatchNoBits
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|docsWithField
operator|instanceof
name|DocIdSet
condition|)
block|{
comment|// UweSays: this is always the case for our current impl - but who knows
comment|// :-)
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
operator|(
name|DocIdSet
operator|)
name|docsWithField
argument_list|,
name|acceptDocs
argument_list|)
return|;
block|}
return|return
operator|new
name|FieldCacheDocIdSet
argument_list|(
name|context
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptDocs
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
specifier|final
name|boolean
name|matchDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|docsWithField
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|field
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|field
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|negate
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FieldValueFilter
name|other
init|=
operator|(
name|FieldValueFilter
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|field
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|negate
operator|!=
name|other
operator|.
name|negate
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
return|return
literal|"FieldValueFilter [field="
operator|+
name|field
operator|+
literal|", negate="
operator|+
name|negate
operator|+
literal|"]"
return|;
block|}
block|}
end_class
end_unit
