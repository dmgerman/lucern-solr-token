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
begin_comment
comment|/** A clause in a BooleanQuery. */
end_comment
begin_class
DECL|class|BooleanClause
specifier|public
class|class
name|BooleanClause
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
comment|/** Specifies how clauses are to occur in matching documents. */
DECL|enum|Occur
specifier|public
specifier|static
enum|enum
name|Occur
block|{
comment|/** Use this operator for clauses that<i>must</i> appear in the matching documents. */
DECL|enum constant|MUST
name|MUST
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"+"
return|;
block|}
block|}
block|,
comment|/** Use this operator for clauses that<i>should</i> appear in the       * matching documents. For a BooleanQuery with no<code>MUST</code>       * clauses one or more<code>SHOULD</code> clauses must match a document       * for the BooleanQuery to match.      * @see BooleanQuery#setMinimumNumberShouldMatch      */
DECL|enum constant|SHOULD
name|SHOULD
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
block|}
block|,
comment|/** Use this operator for clauses that<i>must not</i> appear in the matching documents.      * Note that it is not possible to search for queries that only consist      * of a<code>MUST_NOT</code> clause. */
DECL|enum constant|MUST_NOT
name|MUST_NOT
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"-"
return|;
block|}
block|}
block|;    }
comment|/** The query whose matching documents are combined by the boolean query.    */
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|occur
specifier|private
name|Occur
name|occur
decl_stmt|;
comment|/** Constructs a BooleanClause.   */
DECL|method|BooleanClause
specifier|public
name|BooleanClause
parameter_list|(
name|Query
name|query
parameter_list|,
name|Occur
name|occur
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|occur
operator|=
name|occur
expr_stmt|;
block|}
DECL|method|getOccur
specifier|public
name|Occur
name|getOccur
parameter_list|()
block|{
return|return
name|occur
return|;
block|}
DECL|method|setOccur
specifier|public
name|void
name|setOccur
parameter_list|(
name|Occur
name|occur
parameter_list|)
block|{
name|this
operator|.
name|occur
operator|=
name|occur
expr_stmt|;
block|}
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|setQuery
specifier|public
name|void
name|setQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
DECL|method|isProhibited
specifier|public
name|boolean
name|isProhibited
parameter_list|()
block|{
return|return
name|Occur
operator|.
name|MUST_NOT
operator|==
name|occur
return|;
block|}
DECL|method|isRequired
specifier|public
name|boolean
name|isRequired
parameter_list|()
block|{
return|return
name|Occur
operator|.
name|MUST
operator|==
name|occur
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
operator|!
operator|(
name|o
operator|instanceof
name|BooleanClause
operator|)
condition|)
return|return
literal|false
return|;
name|BooleanClause
name|other
init|=
operator|(
name|BooleanClause
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|other
operator|.
name|query
argument_list|)
operator|&&
name|this
operator|.
name|occur
operator|==
name|other
operator|.
name|occur
return|;
block|}
comment|/** Returns a hash code value for this object.*/
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|query
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|Occur
operator|.
name|MUST
operator|==
name|occur
condition|?
literal|1
else|:
literal|0
operator|)
operator|^
operator|(
name|Occur
operator|.
name|MUST_NOT
operator|==
name|occur
condition|?
literal|2
else|:
literal|0
operator|)
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
name|occur
operator|.
name|toString
argument_list|()
operator|+
name|query
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
