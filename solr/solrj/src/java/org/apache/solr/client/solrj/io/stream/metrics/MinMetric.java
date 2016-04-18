begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.metrics
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
operator|.
name|metrics
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
name|Locale
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
name|StreamExpressionParameter
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
begin_class
DECL|class|MinMetric
specifier|public
class|class
name|MinMetric
extends|extends
name|Metric
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|longMin
specifier|private
name|long
name|longMin
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|doubleMin
specifier|private
name|double
name|doubleMin
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|columnName
specifier|private
name|String
name|columnName
decl_stmt|;
DECL|method|MinMetric
specifier|public
name|MinMetric
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
name|init
argument_list|(
literal|"min"
argument_list|,
name|columnName
argument_list|)
expr_stmt|;
block|}
DECL|method|MinMetric
specifier|public
name|MinMetric
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
comment|// grab all parameters out
name|String
name|functionName
init|=
name|expression
operator|.
name|getFunctionName
argument_list|()
decl_stmt|;
name|String
name|columnName
init|=
name|factory
operator|.
name|getValueOperand
argument_list|(
name|expression
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// validate expression contains only what we want.
if|if
condition|(
literal|null
operator|==
name|columnName
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid expression %s - expected %s(columnName)"
argument_list|,
name|expression
argument_list|,
name|functionName
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|1
operator|!=
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid expression %s - unknown operands found"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|init
argument_list|(
name|functionName
argument_list|,
name|columnName
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|functionName
parameter_list|,
name|String
name|columnName
parameter_list|)
block|{
name|this
operator|.
name|columnName
operator|=
name|columnName
expr_stmt|;
name|setFunctionName
argument_list|(
name|functionName
argument_list|)
expr_stmt|;
name|setIdentifier
argument_list|(
name|functionName
argument_list|,
literal|"("
argument_list|,
name|columnName
argument_list|,
literal|")"
argument_list|)
expr_stmt|;
block|}
DECL|method|getColumns
specifier|public
name|String
index|[]
name|getColumns
parameter_list|()
block|{
name|String
index|[]
name|cols
init|=
block|{
name|columnName
block|}
decl_stmt|;
return|return
name|cols
return|;
block|}
DECL|method|getValue
specifier|public
name|double
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|longMin
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|doubleMin
return|;
block|}
else|else
block|{
return|return
name|longMin
return|;
block|}
block|}
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
block|{
name|Object
name|o
init|=
name|tuple
operator|.
name|get
argument_list|(
name|columnName
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Double
condition|)
block|{
name|double
name|d
init|=
operator|(
name|double
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|d
operator|<
name|doubleMin
condition|)
block|{
name|doubleMin
operator|=
name|d
expr_stmt|;
block|}
block|}
else|else
block|{
name|long
name|l
init|=
operator|(
name|long
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|l
operator|<
name|longMin
condition|)
block|{
name|longMin
operator|=
name|l
expr_stmt|;
block|}
block|}
block|}
DECL|method|newInstance
specifier|public
name|Metric
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|MinMetric
argument_list|(
name|columnName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StreamExpression
argument_list|(
name|getFunctionName
argument_list|()
argument_list|)
operator|.
name|withParameter
argument_list|(
name|columnName
argument_list|)
return|;
block|}
block|}
end_class
end_unit
