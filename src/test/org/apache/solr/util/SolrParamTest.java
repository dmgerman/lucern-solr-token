begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Map
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
name|core
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
name|request
operator|.
name|SolrParams
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
name|request
operator|.
name|MapSolrParams
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
name|request
operator|.
name|DefaultSolrParams
import|;
end_import
begin_comment
comment|/**  * @author ryan  */
end_comment
begin_class
DECL|class|SolrParamTest
specifier|public
class|class
name|SolrParamTest
extends|extends
name|TestCase
block|{
DECL|method|testGetParams
specifier|public
name|void
name|testGetParams
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pmap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"str"
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"bool"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"true-0"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"true-1"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"true-2"
argument_list|,
literal|"on"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"false-0"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"false-1"
argument_list|,
literal|"off"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"false-2"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"float"
argument_list|,
literal|"10.6"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.fl.str"
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.fl.bool"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.fl.int"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.fl.float"
argument_list|,
literal|"10.6"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.bad.bool"
argument_list|,
literal|"notbool"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.bad.int"
argument_list|,
literal|"notint"
argument_list|)
expr_stmt|;
name|pmap
operator|.
name|put
argument_list|(
literal|"f.bad.float"
argument_list|,
literal|"notfloat"
argument_list|)
expr_stmt|;
specifier|final
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
name|pmap
argument_list|)
decl_stmt|;
comment|// Test the string values we put in directly
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10.6"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"string"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.fl.str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.fl.bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.fl.int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"10.6"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.fl.float"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"notbool"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.bad.bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"notint"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.bad.int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"notfloat"
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"f.bad.float"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|pstr
init|=
literal|"string"
decl_stmt|;
specifier|final
name|Boolean
name|pbool
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
specifier|final
name|Integer
name|pint
init|=
operator|new
name|Integer
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|Float
name|pfloat
init|=
operator|new
name|Float
argument_list|(
literal|10.6f
argument_list|)
decl_stmt|;
comment|// Make sure they parse ok
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|params
operator|.
name|getInt
argument_list|(
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|params
operator|.
name|getFloat
argument_list|(
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"f.fl.bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|params
operator|.
name|getInt
argument_list|(
literal|"f.fl.int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|params
operator|.
name|getFloat
argument_list|(
literal|"f.fl.float"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|params
operator|.
name|getFieldParam
argument_list|(
literal|"fl"
argument_list|,
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|params
operator|.
name|getFieldBool
argument_list|(
literal|"fl"
argument_list|,
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|params
operator|.
name|getFieldInt
argument_list|(
literal|"fl"
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|params
operator|.
name|getFieldFloat
argument_list|(
literal|"fl"
argument_list|,
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test field defaulting (fall through to non-field-specific value)
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|params
operator|.
name|getFieldInt
argument_list|(
literal|"fff"
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test boolean parsing
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
comment|// Must use Boolean rather than boolean reference value to prevent
comment|// auto-unboxing ambiguity
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"true-"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"false-"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Malformed params: These should throw a 400
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|params
operator|.
name|getInt
argument_list|(
literal|"f.bad.int"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|params
operator|.
name|getBool
argument_list|(
literal|"f.bad.bool"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|params
operator|.
name|getFloat
argument_list|(
literal|"f.bad.float"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ask for params that arent there
name|assertNull
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|params
operator|.
name|getBool
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|params
operator|.
name|getFloat
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Get things with defaults
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"xxx"
argument_list|,
name|pstr
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
operator|.
name|booleanValue
argument_list|()
argument_list|,
name|params
operator|.
name|getBool
argument_list|(
literal|"xxx"
argument_list|,
name|pbool
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
operator|.
name|intValue
argument_list|()
argument_list|,
name|params
operator|.
name|getInt
argument_list|(
literal|"xxx"
argument_list|,
name|pint
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
operator|.
name|floatValue
argument_list|()
argument_list|,
name|params
operator|.
name|getFloat
argument_list|(
literal|"xxx"
argument_list|,
name|pfloat
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
operator|.
name|booleanValue
argument_list|()
argument_list|,
name|params
operator|.
name|getFieldBool
argument_list|(
literal|"xxx"
argument_list|,
literal|"bool"
argument_list|,
name|pbool
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
operator|.
name|intValue
argument_list|()
argument_list|,
name|params
operator|.
name|getFieldInt
argument_list|(
literal|"xxx"
argument_list|,
literal|"int"
argument_list|,
name|pint
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
operator|.
name|floatValue
argument_list|()
argument_list|,
name|params
operator|.
name|getFieldFloat
argument_list|(
literal|"xxx"
argument_list|,
literal|"float"
argument_list|,
name|pfloat
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|params
operator|.
name|getFieldParam
argument_list|(
literal|"xxx"
argument_list|,
literal|"str"
argument_list|,
name|pstr
argument_list|)
argument_list|)
expr_stmt|;
comment|// Required params testing uses decorator
specifier|final
name|SolrParams
name|required
init|=
name|params
operator|.
name|required
argument_list|()
decl_stmt|;
comment|// Required params which are present should test same as above
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|required
operator|.
name|get
argument_list|(
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|required
operator|.
name|getBool
argument_list|(
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|required
operator|.
name|getInt
argument_list|(
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pfloat
argument_list|,
name|required
operator|.
name|getFloat
argument_list|(
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
comment|// field value present
name|assertEquals
argument_list|(
name|pbool
argument_list|,
name|required
operator|.
name|getFieldBool
argument_list|(
literal|"fl"
argument_list|,
literal|"bool"
argument_list|)
argument_list|)
expr_stmt|;
comment|// field defaulting (fall through to non-field-specific value)
comment|//assertEquals( pint   , required.getFieldInt( "fff",  "int"      ) );
comment|// Required params which are missing: These should throw a 400
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|required
operator|.
name|get
argument_list|(
literal|"aaaa"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|required
operator|.
name|getInt
argument_list|(
literal|"f.bad.int"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|required
operator|.
name|getBool
argument_list|(
literal|"f.bad.bool"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|required
operator|.
name|getFloat
argument_list|(
literal|"f.bad.float"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|required
operator|.
name|getInt
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|required
operator|.
name|getBool
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|required
operator|.
name|getFloat
argument_list|(
literal|"aaa"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|params
operator|.
name|getFieldBool
argument_list|(
literal|"bad"
argument_list|,
literal|"bool"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|400
argument_list|,
name|getReturnCode
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|params
operator|.
name|getFieldInt
argument_list|(
literal|"bad"
argument_list|,
literal|"int"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fields with default use their parent value:
name|assertEquals
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"aaaa"
argument_list|,
literal|"str"
argument_list|)
argument_list|,
name|required
operator|.
name|get
argument_list|(
literal|"aaaa"
argument_list|,
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|params
operator|.
name|getInt
argument_list|(
literal|"f.bad.nnnn"
argument_list|,
name|pint
argument_list|)
argument_list|,
name|required
operator|.
name|getInt
argument_list|(
literal|"f.bad.nnnn"
argument_list|,
name|pint
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check default SolrParams
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|dmap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// these are not defined in params
name|dmap
operator|.
name|put
argument_list|(
literal|"dstr"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|dmap
operator|.
name|put
argument_list|(
literal|"dint"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
comment|// these are defined in params
name|dmap
operator|.
name|put
argument_list|(
literal|"int"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|SolrParams
name|defaults
init|=
operator|new
name|DefaultSolrParams
argument_list|(
name|params
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|dmap
argument_list|)
argument_list|)
decl_stmt|;
comment|// in params, not in default
name|assertEquals
argument_list|(
name|pstr
argument_list|,
name|defaults
operator|.
name|get
argument_list|(
literal|"str"
argument_list|)
argument_list|)
expr_stmt|;
comment|// in default, not in params
name|assertEquals
argument_list|(
literal|"default"
argument_list|,
name|defaults
operator|.
name|get
argument_list|(
literal|"dstr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Integer
argument_list|(
literal|123
argument_list|)
argument_list|,
name|defaults
operator|.
name|getInt
argument_list|(
literal|"dint"
argument_list|)
argument_list|)
expr_stmt|;
comment|// in params, overriding defaults
name|assertEquals
argument_list|(
name|pint
argument_list|,
name|defaults
operator|.
name|getInt
argument_list|(
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
comment|// in neither params nor defaults
name|assertNull
argument_list|(
name|defaults
operator|.
name|get
argument_list|(
literal|"asagdsaga"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getReturnCode
specifier|public
specifier|static
name|int
name|getReturnCode
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
try|try
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|sx
parameter_list|)
block|{
return|return
name|sx
operator|.
name|code
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|500
return|;
block|}
return|return
literal|200
return|;
block|}
block|}
end_class
end_unit
