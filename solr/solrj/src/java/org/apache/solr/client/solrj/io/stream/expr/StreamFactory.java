begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.expr
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
name|expr
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
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import
begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|HashMap
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
name|Locale
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ops
operator|.
name|StreamOperation
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
name|TupleStream
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
name|metrics
operator|.
name|Metric
import|;
end_import
begin_comment
comment|/**  * Used to convert strings into stream expressions  */
end_comment
begin_class
DECL|class|StreamFactory
specifier|public
class|class
name|StreamFactory
implements|implements
name|Serializable
block|{
DECL|field|collectionZkHosts
specifier|private
specifier|transient
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|collectionZkHosts
decl_stmt|;
DECL|field|functionNames
specifier|private
specifier|transient
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|functionNames
decl_stmt|;
DECL|field|defaultZkHost
specifier|private
specifier|transient
name|String
name|defaultZkHost
decl_stmt|;
DECL|method|StreamFactory
specifier|public
name|StreamFactory
parameter_list|()
block|{
name|collectionZkHosts
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|functionNames
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|withCollectionZkHost
specifier|public
name|StreamFactory
name|withCollectionZkHost
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|zkHost
parameter_list|)
block|{
name|this
operator|.
name|collectionZkHosts
operator|.
name|put
argument_list|(
name|collectionName
argument_list|,
name|zkHost
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withDefaultZkHost
specifier|public
name|StreamFactory
name|withDefaultZkHost
parameter_list|(
name|String
name|zkHost
parameter_list|)
block|{
name|this
operator|.
name|defaultZkHost
operator|=
name|zkHost
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getDefaultZkHost
specifier|public
name|String
name|getDefaultZkHost
parameter_list|()
block|{
return|return
name|this
operator|.
name|defaultZkHost
return|;
block|}
DECL|method|getCollectionZkHost
specifier|public
name|String
name|getCollectionZkHost
parameter_list|(
name|String
name|collectionName
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|collectionZkHosts
operator|.
name|containsKey
argument_list|(
name|collectionName
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|collectionZkHosts
operator|.
name|get
argument_list|(
name|collectionName
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getFunctionNames
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|getFunctionNames
parameter_list|()
block|{
return|return
name|functionNames
return|;
block|}
DECL|method|withFunctionName
specifier|public
name|StreamFactory
name|withFunctionName
parameter_list|(
name|String
name|functionName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
name|this
operator|.
name|functionNames
operator|.
name|put
argument_list|(
name|functionName
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getOperand
specifier|public
name|StreamExpressionParameter
name|getOperand
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|int
name|parameterIndex
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|expression
operator|.
name|getParameters
argument_list|()
operator|||
name|parameterIndex
operator|>=
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
name|parameterIndex
argument_list|)
return|;
block|}
comment|/** Given an expression, will return the value parameter at the given index, or null if doesn't exist */
DECL|method|getValueOperand
specifier|public
name|String
name|getValueOperand
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|int
name|parameterIndex
parameter_list|)
block|{
name|StreamExpressionParameter
name|parameter
init|=
name|getOperand
argument_list|(
name|expression
argument_list|,
name|parameterIndex
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|parameter
condition|)
block|{
if|if
condition|(
name|parameter
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
return|return
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|parameter
operator|)
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getNamedOperands
specifier|public
name|List
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
name|getNamedOperands
parameter_list|(
name|StreamExpression
name|expression
parameter_list|)
block|{
name|List
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
name|namedParameters
init|=
operator|new
name|ArrayList
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamExpressionParameter
name|parameter
range|:
name|getOperandsOfType
argument_list|(
name|expression
argument_list|,
name|StreamExpressionNamedParameter
operator|.
name|class
argument_list|)
control|)
block|{
name|namedParameters
operator|.
name|add
argument_list|(
operator|(
name|StreamExpressionNamedParameter
operator|)
name|parameter
argument_list|)
expr_stmt|;
block|}
return|return
name|namedParameters
return|;
block|}
DECL|method|getNamedOperand
specifier|public
name|StreamExpressionNamedParameter
name|getNamedOperand
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|StreamExpressionNamedParameter
argument_list|>
name|namedParameters
init|=
name|getNamedOperands
argument_list|(
name|expression
argument_list|)
decl_stmt|;
for|for
control|(
name|StreamExpressionNamedParameter
name|param
range|:
name|namedParameters
control|)
block|{
if|if
condition|(
name|param
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|param
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getExpressionOperands
specifier|public
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|getExpressionOperands
parameter_list|(
name|StreamExpression
name|expression
parameter_list|)
block|{
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|namedParameters
init|=
operator|new
name|ArrayList
argument_list|<
name|StreamExpression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamExpressionParameter
name|parameter
range|:
name|getOperandsOfType
argument_list|(
name|expression
argument_list|,
name|StreamExpression
operator|.
name|class
argument_list|)
control|)
block|{
name|namedParameters
operator|.
name|add
argument_list|(
operator|(
name|StreamExpression
operator|)
name|parameter
argument_list|)
expr_stmt|;
block|}
return|return
name|namedParameters
return|;
block|}
DECL|method|getExpressionOperands
specifier|public
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|getExpressionOperands
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|String
name|functionName
parameter_list|)
block|{
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|namedParameters
init|=
operator|new
name|ArrayList
argument_list|<
name|StreamExpression
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|StreamExpressionParameter
name|parameter
range|:
name|getOperandsOfType
argument_list|(
name|expression
argument_list|,
name|StreamExpression
operator|.
name|class
argument_list|)
control|)
block|{
name|StreamExpression
name|expressionOperand
init|=
operator|(
name|StreamExpression
operator|)
name|parameter
decl_stmt|;
if|if
condition|(
name|expressionOperand
operator|.
name|getFunctionName
argument_list|()
operator|.
name|equals
argument_list|(
name|functionName
argument_list|)
condition|)
block|{
name|namedParameters
operator|.
name|add
argument_list|(
name|expressionOperand
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|namedParameters
return|;
block|}
DECL|method|getOperandsOfType
specifier|public
name|List
argument_list|<
name|StreamExpressionParameter
argument_list|>
name|getOperandsOfType
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|Class
modifier|...
name|clazzes
parameter_list|)
block|{
name|List
argument_list|<
name|StreamExpressionParameter
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<
name|StreamExpressionParameter
argument_list|>
argument_list|()
decl_stmt|;
name|parameterLoop
label|:
for|for
control|(
name|StreamExpressionParameter
name|parameter
range|:
name|expression
operator|.
name|getParameters
argument_list|()
control|)
block|{
for|for
control|(
name|Class
name|clazz
range|:
name|clazzes
control|)
block|{
if|if
condition|(
operator|!
name|clazz
operator|.
name|isAssignableFrom
argument_list|(
name|parameter
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
continue|continue
name|parameterLoop
continue|;
comment|// go to the next parameter since this parameter cannot be assigned to at least one of the classes
block|}
block|}
name|parameters
operator|.
name|add
argument_list|(
name|parameter
argument_list|)
expr_stmt|;
block|}
return|return
name|parameters
return|;
block|}
DECL|method|getExpressionOperandsRepresentingTypes
specifier|public
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|getExpressionOperandsRepresentingTypes
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|Class
modifier|...
name|clazzes
parameter_list|)
block|{
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|matchingStreamExpressions
init|=
operator|new
name|ArrayList
argument_list|<
name|StreamExpression
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|allStreamExpressions
init|=
name|getExpressionOperands
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|parameterLoop
label|:
for|for
control|(
name|StreamExpression
name|streamExpression
range|:
name|allStreamExpressions
control|)
block|{
if|if
condition|(
name|functionNames
operator|.
name|containsKey
argument_list|(
name|streamExpression
operator|.
name|getFunctionName
argument_list|()
argument_list|)
condition|)
block|{
for|for
control|(
name|Class
name|clazz
range|:
name|clazzes
control|)
block|{
if|if
condition|(
operator|!
name|clazz
operator|.
name|isAssignableFrom
argument_list|(
name|functionNames
operator|.
name|get
argument_list|(
name|streamExpression
operator|.
name|getFunctionName
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
continue|continue
name|parameterLoop
continue|;
block|}
block|}
name|matchingStreamExpressions
operator|.
name|add
argument_list|(
name|streamExpression
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|matchingStreamExpressions
return|;
block|}
DECL|method|constructStream
specifier|public
name|TupleStream
name|constructStream
parameter_list|(
name|String
name|expressionClause
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|constructStream
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
name|expressionClause
argument_list|)
argument_list|)
return|;
block|}
DECL|method|constructStream
specifier|public
name|TupleStream
name|constructStream
parameter_list|(
name|StreamExpression
name|expression
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|function
init|=
name|expression
operator|.
name|getFunctionName
argument_list|()
decl_stmt|;
if|if
condition|(
name|functionNames
operator|.
name|containsKey
argument_list|(
name|function
argument_list|)
condition|)
block|{
name|Class
name|clazz
init|=
name|functionNames
operator|.
name|get
argument_list|(
name|function
argument_list|)
decl_stmt|;
if|if
condition|(
name|Expressible
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|&&
name|TupleStream
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|TupleStream
name|stream
init|=
operator|(
name|TupleStream
operator|)
name|createInstance
argument_list|(
name|functionNames
operator|.
name|get
argument_list|(
name|function
argument_list|)
argument_list|,
operator|new
name|Class
index|[]
block|{
name|StreamExpression
operator|.
name|class
block|,
name|StreamFactory
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|expression
block|,
name|this
block|}
argument_list|)
decl_stmt|;
return|return
name|stream
return|;
block|}
block|}
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
literal|"Invalid stream expression %s - function '%s' is unknown (not mapped to a valid TupleStream)"
argument_list|,
name|expression
argument_list|,
name|expression
operator|.
name|getFunctionName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|constructMetric
specifier|public
name|Metric
name|constructMetric
parameter_list|(
name|String
name|expressionClause
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|constructMetric
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
name|expressionClause
argument_list|)
argument_list|)
return|;
block|}
DECL|method|constructMetric
specifier|public
name|Metric
name|constructMetric
parameter_list|(
name|StreamExpression
name|expression
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|function
init|=
name|expression
operator|.
name|getFunctionName
argument_list|()
decl_stmt|;
if|if
condition|(
name|functionNames
operator|.
name|containsKey
argument_list|(
name|function
argument_list|)
condition|)
block|{
name|Class
name|clazz
init|=
name|functionNames
operator|.
name|get
argument_list|(
name|function
argument_list|)
decl_stmt|;
if|if
condition|(
name|Expressible
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|&&
name|Metric
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|Metric
name|metric
init|=
operator|(
name|Metric
operator|)
name|createInstance
argument_list|(
name|functionNames
operator|.
name|get
argument_list|(
name|function
argument_list|)
argument_list|,
operator|new
name|Class
index|[]
block|{
name|StreamExpression
operator|.
name|class
block|,
name|StreamFactory
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|expression
block|,
name|this
block|}
argument_list|)
decl_stmt|;
return|return
name|metric
return|;
block|}
block|}
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
literal|"Invalid metric expression %s - function '%s' is unknown (not mapped to a valid Metric)"
argument_list|,
name|expression
argument_list|,
name|expression
operator|.
name|getFunctionName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|constructComparator
specifier|public
name|StreamComparator
name|constructComparator
parameter_list|(
name|String
name|comparatorString
parameter_list|,
name|Class
name|comparatorType
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|comparatorString
operator|.
name|contains
argument_list|(
literal|","
argument_list|)
condition|)
block|{
name|String
index|[]
name|parts
init|=
name|comparatorString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|StreamComparator
index|[]
name|comps
init|=
operator|new
name|StreamComparator
index|[
name|parts
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
name|parts
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|comps
index|[
name|idx
index|]
operator|=
name|constructComparator
argument_list|(
name|parts
index|[
name|idx
index|]
operator|.
name|trim
argument_list|()
argument_list|,
name|comparatorType
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultipleFieldComparator
argument_list|(
name|comps
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|comparatorString
operator|.
name|contains
argument_list|(
literal|"="
argument_list|)
condition|)
block|{
comment|// expected format is "left=right order"
name|String
index|[]
name|parts
init|=
name|comparatorString
operator|.
name|split
argument_list|(
literal|"[ =]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|<
literal|3
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
literal|"Invalid comparator expression %s - expecting 'left=right order'"
argument_list|,
name|comparatorString
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|leftFieldName
init|=
literal|null
decl_stmt|;
name|String
name|rightFieldName
init|=
literal|null
decl_stmt|;
name|String
name|order
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|part
range|:
name|parts
control|)
block|{
comment|// skip empty
if|if
condition|(
literal|null
operator|==
name|part
operator|||
literal|0
operator|==
name|part
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// assign each in order
if|if
condition|(
literal|null
operator|==
name|leftFieldName
condition|)
block|{
name|leftFieldName
operator|=
name|part
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|null
operator|==
name|rightFieldName
condition|)
block|{
name|rightFieldName
operator|=
name|part
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|null
operator|==
name|order
condition|)
block|{
name|order
operator|=
name|part
operator|.
name|trim
argument_list|()
expr_stmt|;
break|break;
comment|// we're done, stop looping
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|leftFieldName
operator|||
literal|null
operator|==
name|rightFieldName
operator|||
literal|null
operator|==
name|order
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
literal|"Invalid comparator expression %s - expecting 'left=right order'"
argument_list|,
name|comparatorString
argument_list|)
argument_list|)
throw|;
block|}
return|return
operator|(
name|StreamComparator
operator|)
name|createInstance
argument_list|(
name|comparatorType
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|,
name|String
operator|.
name|class
block|,
name|ComparatorOrder
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|leftFieldName
block|,
name|rightFieldName
block|,
name|ComparatorOrder
operator|.
name|fromString
argument_list|(
name|order
argument_list|)
block|}
argument_list|)
return|;
block|}
else|else
block|{
comment|// expected format is "field order"
name|String
index|[]
name|parts
init|=
name|comparatorString
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
if|if
condition|(
literal|2
operator|!=
name|parts
operator|.
name|length
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
literal|"Invalid comparator expression %s - expecting 'field order'"
argument_list|,
name|comparatorString
argument_list|)
argument_list|)
throw|;
block|}
name|String
name|fieldName
init|=
name|parts
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|order
init|=
name|parts
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
decl_stmt|;
return|return
operator|(
name|StreamComparator
operator|)
name|createInstance
argument_list|(
name|comparatorType
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|,
name|ComparatorOrder
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|fieldName
block|,
name|ComparatorOrder
operator|.
name|fromString
argument_list|(
name|order
argument_list|)
block|}
argument_list|)
return|;
block|}
block|}
DECL|method|constructEqualitor
specifier|public
name|StreamEqualitor
name|constructEqualitor
parameter_list|(
name|String
name|equalitorString
parameter_list|,
name|Class
name|equalitorType
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|equalitorString
operator|.
name|contains
argument_list|(
literal|","
argument_list|)
condition|)
block|{
name|String
index|[]
name|parts
init|=
name|equalitorString
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|StreamEqualitor
index|[]
name|eqs
init|=
operator|new
name|StreamEqualitor
index|[
name|parts
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
name|parts
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|eqs
index|[
name|idx
index|]
operator|=
name|constructEqualitor
argument_list|(
name|parts
index|[
name|idx
index|]
operator|.
name|trim
argument_list|()
argument_list|,
name|equalitorType
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MultipleFieldEqualitor
argument_list|(
name|eqs
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|leftFieldName
decl_stmt|;
name|String
name|rightFieldName
decl_stmt|;
if|if
condition|(
name|equalitorString
operator|.
name|contains
argument_list|(
literal|"="
argument_list|)
condition|)
block|{
name|String
index|[]
name|parts
init|=
name|equalitorString
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
literal|2
operator|!=
name|parts
operator|.
name|length
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
literal|"Invalid equalitor expression %s - expecting fieldName=fieldName"
argument_list|,
name|equalitorString
argument_list|)
argument_list|)
throw|;
block|}
name|leftFieldName
operator|=
name|parts
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
name|rightFieldName
operator|=
name|parts
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|leftFieldName
operator|=
name|rightFieldName
operator|=
name|equalitorString
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|StreamEqualitor
operator|)
name|createInstance
argument_list|(
name|equalitorType
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|,
name|String
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|leftFieldName
block|,
name|rightFieldName
block|}
argument_list|)
return|;
block|}
block|}
DECL|method|constructOperation
specifier|public
name|Metric
name|constructOperation
parameter_list|(
name|String
name|expressionClause
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|constructMetric
argument_list|(
name|StreamExpressionParser
operator|.
name|parse
argument_list|(
name|expressionClause
argument_list|)
argument_list|)
return|;
block|}
DECL|method|constructOperation
specifier|public
name|StreamOperation
name|constructOperation
parameter_list|(
name|StreamExpression
name|expression
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|function
init|=
name|expression
operator|.
name|getFunctionName
argument_list|()
decl_stmt|;
if|if
condition|(
name|functionNames
operator|.
name|containsKey
argument_list|(
name|function
argument_list|)
condition|)
block|{
name|Class
name|clazz
init|=
name|functionNames
operator|.
name|get
argument_list|(
name|function
argument_list|)
decl_stmt|;
if|if
condition|(
name|Expressible
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|&&
name|StreamOperation
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
return|return
operator|(
name|StreamOperation
operator|)
name|createInstance
argument_list|(
name|functionNames
operator|.
name|get
argument_list|(
name|function
argument_list|)
argument_list|,
operator|new
name|Class
index|[]
block|{
name|StreamExpression
operator|.
name|class
block|,
name|StreamFactory
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|expression
block|,
name|this
block|}
argument_list|)
return|;
block|}
block|}
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
literal|"Invalid operation expression %s - function '%s' is unknown (not mapped to a valid StreamOperation)"
argument_list|,
name|expression
argument_list|,
name|expression
operator|.
name|getFunctionName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|createInstance
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|createInstance
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|paramTypes
parameter_list|,
name|Object
index|[]
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|Constructor
argument_list|<
name|T
argument_list|>
name|ctor
decl_stmt|;
try|try
block|{
name|ctor
operator|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|paramTypes
argument_list|)
expr_stmt|;
return|return
name|ctor
operator|.
name|newInstance
argument_list|(
name|params
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
decl||
name|SecurityException
decl||
name|InstantiationException
decl||
name|IllegalAccessException
decl||
name|IllegalArgumentException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|!=
name|e
operator|.
name|getMessage
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
literal|"Unable to construct instance of %s caused by %s"
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
else|else
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
literal|"Unable to construct instance of %s"
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getFunctionName
specifier|public
name|String
name|getFunctionName
parameter_list|(
name|Class
name|clazz
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|>
name|entry
range|:
name|functionNames
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|clazz
condition|)
block|{
return|return
name|entry
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
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
literal|"Unable to find function name for class '%s'"
argument_list|,
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
DECL|method|constructPrimitiveObject
specifier|public
name|Object
name|constructPrimitiveObject
parameter_list|(
name|String
name|original
parameter_list|)
block|{
name|String
name|lower
init|=
name|original
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"null"
operator|.
name|equals
argument_list|(
name|lower
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|lower
argument_list|)
operator|||
literal|"false"
operator|.
name|equals
argument_list|(
name|lower
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|lower
argument_list|)
return|;
block|}
try|try
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|original
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
empty_stmt|;
try|try
block|{
if|if
condition|(
name|original
operator|.
name|matches
argument_list|(
literal|".{1,8}"
argument_list|)
condition|)
block|{
return|return
name|Float
operator|.
name|valueOf
argument_list|(
name|original
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
empty_stmt|;
try|try
block|{
if|if
condition|(
name|original
operator|.
name|matches
argument_list|(
literal|".{1,17}"
argument_list|)
condition|)
block|{
return|return
name|Double
operator|.
name|valueOf
argument_list|(
name|original
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
empty_stmt|;
comment|// is a string
return|return
name|original
return|;
block|}
block|}
end_class
end_unit
