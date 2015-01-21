begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
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
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|Iterator
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
name|Map
import|;
end_import
begin_comment
comment|/**  SolrParams hold request parameters.  *  *  */
end_comment
begin_class
DECL|class|SolrParams
specifier|public
specifier|abstract
class|class
name|SolrParams
implements|implements
name|Serializable
block|{
comment|/** returns the String value of a param, or null if not set */
DECL|method|get
specifier|public
specifier|abstract
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|)
function_decl|;
comment|/** returns an array of the String values of a param, or null if none */
DECL|method|getParams
specifier|public
specifier|abstract
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|param
parameter_list|)
function_decl|;
comment|/** returns an Iterator over the parameter names */
DECL|method|getParameterNamesIterator
specifier|public
specifier|abstract
name|Iterator
argument_list|<
name|String
argument_list|>
name|getParameterNamesIterator
parameter_list|()
function_decl|;
comment|/** returns the value of the param, or def if not set */
DECL|method|get
specifier|public
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|val
return|;
block|}
comment|/** returns a RequiredSolrParams wrapping this */
DECL|method|required
specifier|public
name|RequiredSolrParams
name|required
parameter_list|()
block|{
comment|// TODO? should we want to stash a reference?
return|return
operator|new
name|RequiredSolrParams
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|fpname
specifier|protected
name|String
name|fpname
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
return|return
literal|"f."
operator|+
name|field
operator|+
literal|'.'
operator|+
name|param
return|;
block|}
comment|/** returns the String value of the field parameter, "f.field.param", or    *  the value for "param" if that is not set.    */
DECL|method|getFieldParam
specifier|public
name|String
name|getFieldParam
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|fpname
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|val
else|:
name|get
argument_list|(
name|param
argument_list|)
return|;
block|}
comment|/** returns the String value of the field parameter, "f.field.param", or    *  the value for "param" if that is not set.  If that is not set, def    */
DECL|method|getFieldParam
specifier|public
name|String
name|getFieldParam
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|String
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|fpname
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|val
else|:
name|get
argument_list|(
name|param
argument_list|,
name|def
argument_list|)
return|;
block|}
comment|/** returns the String values of the field parameter, "f.field.param", or    *  the values for "param" if that is not set.    */
DECL|method|getFieldParams
specifier|public
name|String
index|[]
name|getFieldParams
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
index|[]
name|val
init|=
name|getParams
argument_list|(
name|fpname
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|val
operator|!=
literal|null
condition|?
name|val
else|:
name|getParams
argument_list|(
name|param
argument_list|)
return|;
block|}
comment|/** Returns the Boolean value of the param, or null if not set */
DECL|method|getBool
specifier|public
name|Boolean
name|getBool
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|StrUtils
operator|.
name|parseBool
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the boolean value of the param, or def if not set */
DECL|method|getBool
specifier|public
name|boolean
name|getBool
parameter_list|(
name|String
name|param
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|StrUtils
operator|.
name|parseBool
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the Boolean value of the field param,        or the value for param, or null if neither is set. */
DECL|method|getFieldBool
specifier|public
name|Boolean
name|getFieldBool
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|StrUtils
operator|.
name|parseBool
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the boolean value of the field param,    or the value for param, or def if neither is set. */
DECL|method|getFieldBool
specifier|public
name|boolean
name|getFieldBool
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|boolean
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|StrUtils
operator|.
name|parseBool
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Returns the Integer value of the param, or null if not set */
DECL|method|getInt
specifier|public
name|Integer
name|getInt
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Integer
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the Long value of the param, or null if not set */
DECL|method|getLong
specifier|public
name|Long
name|getLong
parameter_list|(
name|String
name|param
parameter_list|,
name|Long
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the int value of the param, or def if not set */
DECL|method|getInt
specifier|public
name|int
name|getInt
parameter_list|(
name|String
name|param
parameter_list|,
name|int
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the Long value of the param, or null if not set */
DECL|method|getLong
specifier|public
name|Long
name|getLong
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Long
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the long value of the param, or def if not set */
DECL|method|getLong
specifier|public
name|long
name|getLong
parameter_list|(
name|String
name|param
parameter_list|,
name|long
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * @return The int value of the field param, or the value for param     * or<code>null</code> if neither is set.     **/
DECL|method|getFieldInt
specifier|public
name|Integer
name|getFieldInt
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Integer
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the int value of the field param,    or the value for param, or def if neither is set. */
DECL|method|getFieldInt
specifier|public
name|int
name|getFieldInt
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|int
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the Float value of the param, or null if not set */
DECL|method|getFloat
specifier|public
name|Float
name|getFloat
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Float
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the float value of the param, or def if not set */
DECL|method|getFloat
specifier|public
name|float
name|getFloat
parameter_list|(
name|String
name|param
parameter_list|,
name|float
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the Float value of the param, or null if not set */
DECL|method|getDouble
specifier|public
name|Double
name|getDouble
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Double
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the float value of the param, or def if not set */
DECL|method|getDouble
specifier|public
name|double
name|getDouble
parameter_list|(
name|String
name|param
parameter_list|,
name|double
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Double
operator|.
name|parseDouble
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the float value of the field param. */
DECL|method|getFieldFloat
specifier|public
name|Float
name|getFieldFloat
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Float
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the float value of the field param,   or the value for param, or def if neither is set. */
DECL|method|getFieldFloat
specifier|public
name|float
name|getFieldFloat
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|float
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Float
operator|.
name|parseFloat
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the float value of the field param. */
DECL|method|getFieldDouble
specifier|public
name|Double
name|getFieldDouble
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
literal|null
else|:
name|Double
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Returns the float value of the field param,   or the value for param, or def if neither is set. */
DECL|method|getFieldDouble
specifier|public
name|double
name|getFieldDouble
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|param
parameter_list|,
name|double
name|def
parameter_list|)
block|{
name|String
name|val
init|=
name|getFieldParam
argument_list|(
name|field
argument_list|,
name|param
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|val
operator|==
literal|null
condition|?
name|def
else|:
name|Double
operator|.
name|parseDouble
argument_list|(
name|val
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|wrapDefaults
specifier|public
specifier|static
name|SolrParams
name|wrapDefaults
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|SolrParams
name|defaults
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
return|return
name|defaults
return|;
if|if
condition|(
name|defaults
operator|==
literal|null
condition|)
return|return
name|params
return|;
return|return
operator|new
name|DefaultSolrParams
argument_list|(
name|params
argument_list|,
name|defaults
argument_list|)
return|;
block|}
DECL|method|wrapAppended
specifier|public
specifier|static
name|SolrParams
name|wrapAppended
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|SolrParams
name|defaults
parameter_list|)
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
return|return
name|defaults
return|;
if|if
condition|(
name|defaults
operator|==
literal|null
condition|)
return|return
name|params
return|;
return|return
name|AppendedSolrParams
operator|.
name|wrapAppended
argument_list|(
name|params
argument_list|,
name|defaults
argument_list|)
return|;
block|}
comment|/** Create a Map&lt;String,String&gt; from a NamedList given no keys are repeated */
DECL|method|toMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toMap
parameter_list|(
name|NamedList
name|params
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|params
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|params
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/** Create a Map&lt;String,String[]&gt; from a NamedList */
DECL|method|toMultiMap
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|toMultiMap
parameter_list|(
name|NamedList
name|params
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|params
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|params
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|MultiMapSolrParams
operator|.
name|addParam
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/** Create SolrParams from NamedList. */
DECL|method|toSolrParams
specifier|public
specifier|static
name|SolrParams
name|toSolrParams
parameter_list|(
name|NamedList
name|params
parameter_list|)
block|{
comment|// if no keys are repeated use the faster MapSolrParams
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|prev
init|=
name|map
operator|.
name|put
argument_list|(
name|params
operator|.
name|getName
argument_list|(
name|i
argument_list|)
argument_list|,
name|params
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
return|return
operator|new
name|MultiMapSolrParams
argument_list|(
name|toMultiMap
argument_list|(
name|params
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|MapSolrParams
argument_list|(
name|map
argument_list|)
return|;
block|}
comment|/** Create filtered SolrParams. */
DECL|method|toFilteredSolrParams
specifier|public
name|SolrParams
name|toFilteredSolrParams
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|NamedList
argument_list|<
name|String
argument_list|>
name|nl
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|getParameterNamesIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|name
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
specifier|final
name|String
index|[]
name|values
init|=
name|getParams
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|nl
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|toSolrParams
argument_list|(
name|nl
argument_list|)
return|;
block|}
comment|/** Convert this to a NamedList */
DECL|method|toNamedList
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|toNamedList
parameter_list|()
block|{
specifier|final
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|result
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|getParameterNamesIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|name
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|values
init|=
name|getParams
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// currently no reason not to use the same array
name|result
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
