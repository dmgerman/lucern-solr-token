begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.sinks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|sinks
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|SinkTokenizer
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
name|analysis
operator|.
name|TeeSinkTokenFilter
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
name|analysis
operator|.
name|Token
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
begin_comment
comment|/**  * If the {@link org.apache.lucene.analysis.Token#type()} matches the passed in<code>typeToMatch</code> then  * add it to the sink  *  * @deprecated Use {@link TokenTypeSinkFilter} and {@link TeeSinkTokenFilter} instead.  **/
end_comment
begin_class
DECL|class|TokenTypeSinkTokenizer
specifier|public
class|class
name|TokenTypeSinkTokenizer
extends|extends
name|SinkTokenizer
block|{
DECL|field|typeToMatch
specifier|private
name|String
name|typeToMatch
decl_stmt|;
DECL|method|TokenTypeSinkTokenizer
specifier|public
name|TokenTypeSinkTokenizer
parameter_list|(
name|String
name|typeToMatch
parameter_list|)
block|{
name|this
operator|.
name|typeToMatch
operator|=
name|typeToMatch
expr_stmt|;
block|}
DECL|method|TokenTypeSinkTokenizer
specifier|public
name|TokenTypeSinkTokenizer
parameter_list|(
name|int
name|initCap
parameter_list|,
name|String
name|typeToMatch
parameter_list|)
block|{
name|super
argument_list|(
name|initCap
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeToMatch
operator|=
name|typeToMatch
expr_stmt|;
block|}
DECL|method|TokenTypeSinkTokenizer
specifier|public
name|TokenTypeSinkTokenizer
parameter_list|(
name|List
comment|/*<Token>*/
name|input
parameter_list|,
name|String
name|typeToMatch
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeToMatch
operator|=
name|typeToMatch
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
comment|//check to see if this is a Category
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|typeToMatch
operator|.
name|equals
argument_list|(
name|t
operator|.
name|type
argument_list|()
argument_list|)
condition|)
block|{
name|super
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
