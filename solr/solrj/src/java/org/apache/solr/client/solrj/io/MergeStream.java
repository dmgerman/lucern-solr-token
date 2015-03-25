begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io
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
name|Comparator
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
name|ArrayList
import|;
end_import
begin_comment
comment|/** * Unions streamA with streamB ordering the Tuples based on a Comparator. * Both streams must be sorted by the fields being compared. **/
end_comment
begin_class
DECL|class|MergeStream
specifier|public
class|class
name|MergeStream
extends|extends
name|TupleStream
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|streamA
specifier|private
name|PushBackStream
name|streamA
decl_stmt|;
DECL|field|streamB
specifier|private
name|PushBackStream
name|streamB
decl_stmt|;
DECL|field|comp
specifier|private
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
decl_stmt|;
DECL|method|MergeStream
specifier|public
name|MergeStream
parameter_list|(
name|TupleStream
name|streamA
parameter_list|,
name|TupleStream
name|streamB
parameter_list|,
name|Comparator
argument_list|<
name|Tuple
argument_list|>
name|comp
parameter_list|)
block|{
name|this
operator|.
name|streamA
operator|=
operator|new
name|PushBackStream
argument_list|(
name|streamA
argument_list|)
expr_stmt|;
name|this
operator|.
name|streamB
operator|=
operator|new
name|PushBackStream
argument_list|(
name|streamB
argument_list|)
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|streamA
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|streamB
operator|.
name|setStreamContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
name|List
argument_list|<
name|TupleStream
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|streamA
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|streamB
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|streamA
operator|.
name|open
argument_list|()
expr_stmt|;
name|streamB
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|streamA
operator|.
name|close
argument_list|()
expr_stmt|;
name|streamB
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|Tuple
name|a
init|=
name|streamA
operator|.
name|read
argument_list|()
decl_stmt|;
name|Tuple
name|b
init|=
name|streamB
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|EOF
operator|&&
name|b
operator|.
name|EOF
condition|)
block|{
return|return
name|a
return|;
block|}
if|if
condition|(
name|a
operator|.
name|EOF
condition|)
block|{
name|streamA
operator|.
name|pushBack
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
if|if
condition|(
name|b
operator|.
name|EOF
condition|)
block|{
name|streamB
operator|.
name|pushBack
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
name|int
name|c
init|=
name|comp
operator|.
name|compare
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
name|streamB
operator|.
name|pushBack
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|a
return|;
block|}
else|else
block|{
name|streamA
operator|.
name|pushBack
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|b
return|;
block|}
block|}
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class
end_unit
