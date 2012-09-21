begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|surround
operator|.
name|query
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
name|search
operator|.
name|BooleanClause
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
name|search
operator|.
name|BooleanQuery
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
name|search
operator|.
name|Query
import|;
end_import
begin_comment
comment|/** Lowest level base class for surround queries */
end_comment
begin_class
DECL|class|SrndQuery
specifier|public
specifier|abstract
class|class
name|SrndQuery
implements|implements
name|Cloneable
block|{
DECL|method|SrndQuery
specifier|public
name|SrndQuery
parameter_list|()
block|{}
DECL|field|weight
specifier|private
name|float
name|weight
init|=
operator|(
name|float
operator|)
literal|1.0
decl_stmt|;
DECL|field|weighted
specifier|private
name|boolean
name|weighted
init|=
literal|false
decl_stmt|;
DECL|method|setWeight
specifier|public
name|void
name|setWeight
parameter_list|(
name|float
name|w
parameter_list|)
block|{
name|weight
operator|=
name|w
expr_stmt|;
comment|/* as parsed from the query text */
name|weighted
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|isWeighted
specifier|public
name|boolean
name|isWeighted
parameter_list|()
block|{
return|return
name|weighted
return|;
block|}
DECL|method|getWeight
specifier|public
name|float
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
DECL|method|getWeightString
specifier|public
name|String
name|getWeightString
parameter_list|()
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|getWeight
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getWeightOperator
specifier|public
name|String
name|getWeightOperator
parameter_list|()
block|{
return|return
literal|"^"
return|;
block|}
DECL|method|weightToString
specifier|protected
name|void
name|weightToString
parameter_list|(
name|StringBuilder
name|r
parameter_list|)
block|{
comment|/* append the weight part of a query */
if|if
condition|(
name|isWeighted
argument_list|()
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
name|getWeightOperator
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|getWeightString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeLuceneQueryField
specifier|public
name|Query
name|makeLuceneQueryField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
block|{
name|Query
name|q
init|=
name|makeLuceneQueryFieldNoBoost
argument_list|(
name|fieldName
argument_list|,
name|qf
argument_list|)
decl_stmt|;
if|if
condition|(
name|isWeighted
argument_list|()
condition|)
block|{
name|q
operator|.
name|setBoost
argument_list|(
name|getWeight
argument_list|()
operator|*
name|q
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
comment|/* weight may be at any level in a SrndQuery */
block|}
return|return
name|q
return|;
block|}
DECL|method|makeLuceneQueryFieldNoBoost
specifier|public
specifier|abstract
name|Query
name|makeLuceneQueryFieldNoBoost
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|BasicQueryFactory
name|qf
parameter_list|)
function_decl|;
comment|/** This method is used by {@link #hashCode()} and {@link #equals(Object)},    *  see LUCENE-2945.    */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
DECL|method|isFieldsSubQueryAcceptable
specifier|public
name|boolean
name|isFieldsSubQueryAcceptable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SrndQuery
name|clone
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|SrndQuery
operator|)
name|super
operator|.
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|cns
parameter_list|)
block|{
throw|throw
operator|new
name|Error
argument_list|(
name|cns
argument_list|)
throw|;
block|}
block|}
comment|/** For subclasses of {@link SrndQuery} within the package    *  {@link org.apache.lucene.queryparser.surround.query}    *  it is not necessary to override this method,    *  @see #toString()    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** For subclasses of {@link SrndQuery} within the package    *  {@link org.apache.lucene.queryparser.surround.query}    *  it is not necessary to override this method,    *  @see #toString()    */
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
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|obj
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|obj
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** An empty Lucene query */
DECL|field|theEmptyLcnQuery
specifier|public
specifier|final
specifier|static
name|Query
name|theEmptyLcnQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
block|{
comment|/* no changes allowed */
annotation|@
name|Override
specifier|public
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|BooleanClause
name|clause
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|Query
name|query
parameter_list|,
name|BooleanClause
operator|.
name|Occur
name|occur
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit
