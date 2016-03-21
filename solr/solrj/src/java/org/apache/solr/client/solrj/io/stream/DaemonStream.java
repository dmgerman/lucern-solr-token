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
name|StreamExpressionNamedParameter
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
name|StreamExpressionValue
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
DECL|class|DaemonStream
specifier|public
class|class
name|DaemonStream
extends|extends
name|TupleStream
implements|implements
name|Expressible
block|{
DECL|field|tupleStream
specifier|private
name|TupleStream
name|tupleStream
decl_stmt|;
DECL|field|streamRunner
specifier|private
name|StreamRunner
name|streamRunner
decl_stmt|;
DECL|field|queue
specifier|private
name|ArrayBlockingQueue
argument_list|<
name|Tuple
argument_list|>
name|queue
decl_stmt|;
DECL|field|queueSize
specifier|private
name|int
name|queueSize
decl_stmt|;
DECL|field|eatTuples
specifier|private
name|boolean
name|eatTuples
decl_stmt|;
DECL|field|iterations
specifier|private
name|long
name|iterations
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|stopTime
specifier|private
name|long
name|stopTime
decl_stmt|;
DECL|field|exception
specifier|private
name|Exception
name|exception
decl_stmt|;
DECL|field|runInterval
specifier|private
name|long
name|runInterval
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|DaemonStream
specifier|public
name|DaemonStream
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
name|List
argument_list|<
name|StreamExpression
argument_list|>
name|streamExpressions
init|=
name|factory
operator|.
name|getExpressionOperandsRepresentingTypes
argument_list|(
name|expression
argument_list|,
name|Expressible
operator|.
name|class
argument_list|,
name|TupleStream
operator|.
name|class
argument_list|)
decl_stmt|;
name|TupleStream
name|tupleStream
init|=
name|factory
operator|.
name|constructStream
argument_list|(
name|streamExpressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|idExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|runExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"runInterval"
argument_list|)
decl_stmt|;
name|StreamExpressionNamedParameter
name|queueExpression
init|=
name|factory
operator|.
name|getNamedOperand
argument_list|(
name|expression
argument_list|,
literal|"queueSize"
argument_list|)
decl_stmt|;
name|String
name|id
init|=
literal|null
decl_stmt|;
name|long
name|runInterval
init|=
literal|0L
decl_stmt|;
name|int
name|queueSize
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|idExpression
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid expression id parameter expected"
argument_list|)
throw|;
block|}
else|else
block|{
name|id
operator|=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|idExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|runExpression
operator|==
literal|null
condition|)
block|{
name|runInterval
operator|=
literal|2000
expr_stmt|;
block|}
else|else
block|{
name|runInterval
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|runExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queueExpression
operator|!=
literal|null
condition|)
block|{
name|queueSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|queueExpression
operator|.
name|getParameter
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// validate expression contains only what we want.
if|if
condition|(
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|streamExpressions
operator|.
name|size
argument_list|()
operator|+
literal|2
operator|&&
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|streamExpressions
operator|.
name|size
argument_list|()
operator|+
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
literal|"Invalid expression %s - unknown operands found"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|1
operator|!=
name|streamExpressions
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
literal|"Invalid expression %s - expecting a single stream but found %d"
argument_list|,
name|expression
argument_list|,
name|streamExpressions
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|init
argument_list|(
name|tupleStream
argument_list|,
name|id
argument_list|,
name|runInterval
argument_list|,
name|queueSize
argument_list|)
expr_stmt|;
block|}
DECL|method|DaemonStream
specifier|public
name|DaemonStream
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|,
name|String
name|id
parameter_list|,
name|long
name|runInterval
parameter_list|,
name|int
name|queueSize
parameter_list|)
block|{
name|init
argument_list|(
name|tupleStream
argument_list|,
name|id
argument_list|,
name|runInterval
argument_list|,
name|queueSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpression
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// function name
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// streams
if|if
condition|(
name|tupleStream
operator|instanceof
name|Expressible
condition|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
operator|(
operator|(
name|Expressible
operator|)
name|tupleStream
operator|)
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"This UniqueStream contains a non-expressible TupleStream - it cannot be converted to an expression"
argument_list|)
throw|;
block|}
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"runInterval"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|runInterval
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionNamedParameter
argument_list|(
literal|"queueSize"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|queueSize
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|expression
return|;
block|}
DECL|method|remainingCapacity
specifier|public
name|int
name|remainingCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|queue
operator|.
name|remainingCapacity
argument_list|()
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|,
name|String
name|id
parameter_list|,
name|long
name|runInterval
parameter_list|,
name|int
name|queueSize
parameter_list|)
block|{
name|this
operator|.
name|tupleStream
operator|=
name|tupleStream
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|runInterval
operator|=
name|runInterval
expr_stmt|;
name|this
operator|.
name|queueSize
operator|=
name|queueSize
expr_stmt|;
if|if
condition|(
name|queueSize
operator|>
literal|0
condition|)
block|{
name|queue
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|(
name|queueSize
argument_list|)
expr_stmt|;
name|eatTuples
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|eatTuples
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|id
operator|.
name|hashCode
argument_list|()
return|;
block|}
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
operator|instanceof
name|DaemonStream
condition|)
block|{
return|return
name|id
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DaemonStream
operator|)
name|o
operator|)
operator|.
name|id
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
block|{
name|this
operator|.
name|streamRunner
operator|=
operator|new
name|StreamRunner
argument_list|(
name|runInterval
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|this
operator|.
name|streamRunner
operator|.
name|start
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
try|try
block|{
return|return
name|queue
operator|.
name|take
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getStreamSort
specifier|public
name|StreamComparator
name|getStreamSort
parameter_list|()
block|{
return|return
name|tupleStream
operator|.
name|getStreamSort
argument_list|()
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|streamContext
parameter_list|)
block|{
name|this
operator|.
name|tupleStream
operator|.
name|setStreamContext
argument_list|(
name|streamContext
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|streamRunner
operator|.
name|setShutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
name|streamRunner
operator|.
name|setShutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|true
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
name|children
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
name|tupleStream
argument_list|)
expr_stmt|;
return|return
name|children
return|;
block|}
DECL|method|getInfo
specifier|public
specifier|synchronized
name|Tuple
name|getInfo
parameter_list|()
block|{
name|Tuple
name|tuple
init|=
operator|new
name|Tuple
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
decl_stmt|;
name|tuple
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|tuple
operator|.
name|put
argument_list|(
literal|"startTime"
argument_list|,
name|startTime
argument_list|)
expr_stmt|;
name|tuple
operator|.
name|put
argument_list|(
literal|"stopTime"
argument_list|,
name|stopTime
argument_list|)
expr_stmt|;
name|tuple
operator|.
name|put
argument_list|(
literal|"iterations"
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
name|tuple
operator|.
name|put
argument_list|(
literal|"state"
argument_list|,
name|streamRunner
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
name|tuple
operator|.
name|put
argument_list|(
literal|"exception"
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|tuple
return|;
block|}
DECL|method|incrementIterations
specifier|private
specifier|synchronized
name|void
name|incrementIterations
parameter_list|()
block|{
operator|++
name|iterations
expr_stmt|;
block|}
DECL|method|setStartTime
specifier|private
specifier|synchronized
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
DECL|method|setStopTime
specifier|private
specifier|synchronized
name|void
name|setStopTime
parameter_list|(
name|long
name|stopTime
parameter_list|)
block|{
name|this
operator|.
name|stopTime
operator|=
name|stopTime
expr_stmt|;
block|}
DECL|class|StreamRunner
specifier|private
class|class
name|StreamRunner
extends|extends
name|Thread
block|{
DECL|field|sleepMillis
specifier|private
name|long
name|sleepMillis
init|=
literal|1000
decl_stmt|;
DECL|field|runInterval
specifier|private
name|long
name|runInterval
decl_stmt|;
DECL|field|lastRun
specifier|private
name|long
name|lastRun
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|shutdown
specifier|private
name|boolean
name|shutdown
decl_stmt|;
DECL|method|StreamRunner
specifier|public
name|StreamRunner
parameter_list|(
name|long
name|runInterval
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|runInterval
operator|=
name|runInterval
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|setShutdown
specifier|public
specifier|synchronized
name|void
name|setShutdown
parameter_list|(
name|boolean
name|shutdown
parameter_list|)
block|{
name|this
operator|.
name|shutdown
operator|=
name|shutdown
expr_stmt|;
block|}
DECL|method|getShutdown
specifier|public
specifier|synchronized
name|boolean
name|getShutdown
parameter_list|()
block|{
return|return
name|shutdown
return|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|errors
init|=
literal|0
decl_stmt|;
name|setStartTime
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|OUTER
label|:
while|while
condition|(
operator|!
name|getShutdown
argument_list|()
condition|)
block|{
name|long
name|now
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|now
operator|-
name|lastRun
operator|)
operator|>
name|this
operator|.
name|runInterval
condition|)
block|{
name|lastRun
operator|=
name|now
expr_stmt|;
try|try
block|{
name|tupleStream
operator|.
name|open
argument_list|()
expr_stmt|;
name|INNER
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|Tuple
name|tuple
init|=
name|tupleStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
name|errors
operator|=
literal|0
expr_stmt|;
comment|// Reset errors on successful run.
if|if
condition|(
name|tuple
operator|.
name|fields
operator|.
name|containsKey
argument_list|(
literal|"sleepMillis"
argument_list|)
condition|)
block|{
name|this
operator|.
name|sleepMillis
operator|=
name|tuple
operator|.
name|getLong
argument_list|(
literal|"sleepMillis"
argument_list|)
expr_stmt|;
name|this
operator|.
name|runInterval
operator|=
operator|-
literal|1
expr_stmt|;
block|}
break|break
name|INNER
break|;
block|}
elseif|else
if|if
condition|(
operator|!
name|eatTuples
condition|)
block|{
try|try
block|{
name|queue
operator|.
name|put
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
break|break
name|OUTER
break|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|exception
operator|=
name|e
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Error in DaemonStream:"
operator|+
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
operator|++
name|errors
expr_stmt|;
if|if
condition|(
name|errors
operator|>
literal|100
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Too many consectutive errors. Stopping DaemonStream:"
operator|+
name|id
argument_list|)
expr_stmt|;
break|break
name|OUTER
break|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Fatal Error in DaemonStream:"
operator|+
name|id
argument_list|,
name|t
argument_list|)
expr_stmt|;
comment|//For anything other then IOException break out of the loop and shutdown the thread.
break|break
name|OUTER
break|;
block|}
finally|finally
block|{
try|try
block|{
name|tupleStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|exception
operator|=
name|e1
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Error in DaemonStream:"
operator|+
name|id
argument_list|,
name|e1
argument_list|)
expr_stmt|;
break|break
name|OUTER
break|;
block|}
block|}
block|}
block|}
name|incrementIterations
argument_list|()
expr_stmt|;
if|if
condition|(
name|sleepMillis
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error in DaemonStream:"
operator|+
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
break|break
name|OUTER
break|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|eatTuples
condition|)
block|{
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Tuple
name|tuple
init|=
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
decl_stmt|;
try|try
block|{
name|queue
operator|.
name|put
argument_list|(
name|tuple
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error in DaemonStream:"
operator|+
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|setStopTime
argument_list|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
