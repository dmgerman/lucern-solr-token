begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|ComparatorOrder
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|FieldComparator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|MultipleFieldComparator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|comp
operator|.
name|StreamComparator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eq
operator|.
name|Equalitor
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eq
operator|.
name|FieldEqualitor
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eq
operator|.
name|MultipleFieldEqualitor
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eq
operator|.
name|StreamEqualitor
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|Expressible
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
import|;
end_import
begin_comment
comment|/**  * Joins leftStream with rightStream based on a Equalitor. Both streams must be sorted by the fields being joined on.  * Resulting stream is sorted by the equalitor.  **/
end_comment
begin_class
DECL|class|BiJoinStream
specifier|public
specifier|abstract
class|class
name|BiJoinStream
extends|extends
name|JoinStream
implements|implements
name|Expressible
block|{
DECL|field|leftStream
specifier|protected
name|PushBackStream
name|leftStream
decl_stmt|;
DECL|field|rightStream
specifier|protected
name|PushBackStream
name|rightStream
decl_stmt|;
comment|// This is used to determine whether we should iterate the left or right side (depending on stream order).
comment|// It is built from the incoming equalitor and streams' comparators.
DECL|field|iterationComparator
specifier|protected
name|StreamComparator
name|iterationComparator
decl_stmt|;
DECL|field|leftStreamComparator
DECL|field|rightStreamComparator
specifier|protected
name|StreamComparator
name|leftStreamComparator
decl_stmt|,
name|rightStreamComparator
decl_stmt|;
DECL|method|BiJoinStream
specifier|public
name|BiJoinStream
parameter_list|(
name|TupleStream
name|leftStream
parameter_list|,
name|TupleStream
name|rightStream
parameter_list|,
name|StreamEqualitor
name|eq
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|eq
argument_list|,
name|leftStream
argument_list|,
name|rightStream
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|BiJoinStream
specifier|public
name|BiJoinStream
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|expression
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Validates all incoming streams for tuple order
name|validateTupleOrder
argument_list|()
expr_stmt|;
name|leftStream
operator|=
name|getStream
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|rightStream
operator|=
name|getStream
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// iterationComparator is a combination of the equalitor and the comp from each stream. This can easily be done by
comment|// grabbing the first N parts of each comp where N is the number of parts in the equalitor. Because we've already
comment|// validated tuple order (the comps) then we know this can be done.
name|iterationComparator
operator|=
name|createIterationComparator
argument_list|(
name|eq
argument_list|,
name|leftStream
operator|.
name|getStreamSort
argument_list|()
argument_list|)
expr_stmt|;
name|leftStreamComparator
operator|=
name|createSideComparator
argument_list|(
name|eq
argument_list|,
name|leftStream
operator|.
name|getStreamSort
argument_list|()
argument_list|)
expr_stmt|;
name|rightStreamComparator
operator|=
name|createSideComparator
argument_list|(
name|eq
argument_list|,
name|rightStream
operator|.
name|getStreamSort
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|validateTupleOrder
specifier|protected
name|void
name|validateTupleOrder
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isValidTupleOrder
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid JoinStream - all incoming stream comparators (sort) must be a superset of this stream's equalitor."
argument_list|)
throw|;
block|}
block|}
DECL|method|createIterationComparator
specifier|private
name|StreamComparator
name|createIterationComparator
parameter_list|(
name|StreamEqualitor
name|eq
parameter_list|,
name|StreamComparator
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|eq
operator|instanceof
name|MultipleFieldEqualitor
operator|&&
name|comp
operator|instanceof
name|MultipleFieldComparator
condition|)
block|{
comment|// we know the comp is at least as long as the eq because we've already validated the tuple order
name|StreamComparator
index|[]
name|compoundComps
init|=
operator|new
name|StreamComparator
index|[
operator|(
operator|(
name|MultipleFieldEqualitor
operator|)
name|eq
operator|)
operator|.
name|getEqs
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|compoundComps
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|StreamEqualitor
name|sourceEqualitor
init|=
operator|(
operator|(
name|MultipleFieldEqualitor
operator|)
name|eq
operator|)
operator|.
name|getEqs
argument_list|()
index|[
name|idx
index|]
decl_stmt|;
name|StreamComparator
name|sourceComparator
init|=
operator|(
operator|(
name|MultipleFieldComparator
operator|)
name|comp
operator|)
operator|.
name|getComps
argument_list|()
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
name|sourceEqualitor
operator|instanceof
name|FieldEqualitor
operator|&&
name|sourceComparator
operator|instanceof
name|FieldComparator
condition|)
block|{
name|FieldEqualitor
name|fieldEqualitor
init|=
operator|(
name|FieldEqualitor
operator|)
name|sourceEqualitor
decl_stmt|;
name|FieldComparator
name|fieldComparator
init|=
operator|(
name|FieldComparator
operator|)
name|sourceComparator
decl_stmt|;
name|compoundComps
index|[
name|idx
index|]
operator|=
operator|new
name|FieldComparator
argument_list|(
name|fieldEqualitor
operator|.
name|getLeftFieldName
argument_list|()
argument_list|,
name|fieldEqualitor
operator|.
name|getRightFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create an iteration comparator"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|MultipleFieldComparator
argument_list|(
name|compoundComps
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|comp
operator|instanceof
name|MultipleFieldComparator
condition|)
block|{
name|StreamEqualitor
name|sourceEqualitor
init|=
name|eq
decl_stmt|;
name|StreamComparator
name|sourceComparator
init|=
operator|(
operator|(
name|MultipleFieldComparator
operator|)
name|comp
operator|)
operator|.
name|getComps
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|sourceEqualitor
operator|instanceof
name|FieldEqualitor
operator|&&
name|sourceComparator
operator|instanceof
name|FieldComparator
condition|)
block|{
name|FieldEqualitor
name|fieldEqualitor
init|=
operator|(
name|FieldEqualitor
operator|)
name|sourceEqualitor
decl_stmt|;
name|FieldComparator
name|fieldComparator
init|=
operator|(
name|FieldComparator
operator|)
name|sourceComparator
decl_stmt|;
return|return
operator|new
name|FieldComparator
argument_list|(
name|fieldEqualitor
operator|.
name|getLeftFieldName
argument_list|()
argument_list|,
name|fieldEqualitor
operator|.
name|getRightFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getOrder
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create an iteration comparator"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|StreamEqualitor
name|sourceEqualitor
init|=
name|eq
decl_stmt|;
name|StreamComparator
name|sourceComparator
init|=
name|comp
decl_stmt|;
if|if
condition|(
name|sourceEqualitor
operator|instanceof
name|FieldEqualitor
operator|&&
name|sourceComparator
operator|instanceof
name|FieldComparator
condition|)
block|{
name|FieldEqualitor
name|fieldEqualitor
init|=
operator|(
name|FieldEqualitor
operator|)
name|sourceEqualitor
decl_stmt|;
name|FieldComparator
name|fieldComparator
init|=
operator|(
name|FieldComparator
operator|)
name|sourceComparator
decl_stmt|;
return|return
operator|new
name|FieldComparator
argument_list|(
name|fieldEqualitor
operator|.
name|getLeftFieldName
argument_list|()
argument_list|,
name|fieldEqualitor
operator|.
name|getRightFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getOrder
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create an iteration comparator"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|createSideComparator
specifier|private
name|StreamComparator
name|createSideComparator
parameter_list|(
name|StreamEqualitor
name|eq
parameter_list|,
name|StreamComparator
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|eq
operator|instanceof
name|MultipleFieldEqualitor
operator|&&
name|comp
operator|instanceof
name|MultipleFieldComparator
condition|)
block|{
comment|// we know the comp is at least as long as the eq because we've already validated the tuple order
name|StreamComparator
index|[]
name|compoundComps
init|=
operator|new
name|StreamComparator
index|[
operator|(
operator|(
name|MultipleFieldEqualitor
operator|)
name|eq
operator|)
operator|.
name|getEqs
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|compoundComps
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|StreamComparator
name|sourceComparator
init|=
operator|(
operator|(
name|MultipleFieldComparator
operator|)
name|comp
operator|)
operator|.
name|getComps
argument_list|()
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
name|sourceComparator
operator|instanceof
name|FieldComparator
condition|)
block|{
name|FieldComparator
name|fieldComparator
init|=
operator|(
name|FieldComparator
operator|)
name|sourceComparator
decl_stmt|;
name|compoundComps
index|[
name|idx
index|]
operator|=
operator|new
name|FieldComparator
argument_list|(
name|fieldComparator
operator|.
name|getLeftFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getRightFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getOrder
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create an side comparator"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|MultipleFieldComparator
argument_list|(
name|compoundComps
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|comp
operator|instanceof
name|MultipleFieldComparator
condition|)
block|{
name|StreamComparator
name|sourceComparator
init|=
operator|(
operator|(
name|MultipleFieldComparator
operator|)
name|comp
operator|)
operator|.
name|getComps
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|sourceComparator
operator|instanceof
name|FieldComparator
condition|)
block|{
name|FieldComparator
name|fieldComparator
init|=
operator|(
name|FieldComparator
operator|)
name|sourceComparator
decl_stmt|;
return|return
operator|new
name|FieldComparator
argument_list|(
name|fieldComparator
operator|.
name|getLeftFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getRightFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getOrder
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create an side comparator"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|StreamComparator
name|sourceComparator
init|=
name|comp
decl_stmt|;
if|if
condition|(
name|sourceComparator
operator|instanceof
name|FieldComparator
condition|)
block|{
name|FieldComparator
name|fieldComparator
init|=
operator|(
name|FieldComparator
operator|)
name|sourceComparator
decl_stmt|;
return|return
operator|new
name|FieldComparator
argument_list|(
name|fieldComparator
operator|.
name|getLeftFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getRightFieldName
argument_list|()
argument_list|,
name|fieldComparator
operator|.
name|getOrder
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to create an side comparator"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class
end_unit
