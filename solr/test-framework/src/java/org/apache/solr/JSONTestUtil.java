begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_class
DECL|class|JSONTestUtil
specifier|public
class|class
name|JSONTestUtil
block|{
comment|/**    * Default delta used in numeric equality comparisons for floats and doubles.    */
DECL|field|DEFAULT_DELTA
specifier|public
specifier|final
specifier|static
name|double
name|DEFAULT_DELTA
init|=
literal|1e-5
decl_stmt|;
DECL|field|failRepeatedKeys
specifier|public
specifier|static
name|boolean
name|failRepeatedKeys
init|=
literal|false
decl_stmt|;
comment|/**    * comparison using default delta    * @see #DEFAULT_DELTA    * @see #match(String,String,double)    */
DECL|method|match
specifier|public
specifier|static
name|String
name|match
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|pathAndExpected
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|match
argument_list|(
name|input
argument_list|,
name|pathAndExpected
argument_list|,
name|DEFAULT_DELTA
argument_list|)
return|;
block|}
comment|/**    * comparison using default delta    * @see #DEFAULT_DELTA    * @see #match(String,String,String,double)    */
DECL|method|match
specifier|public
specifier|static
name|String
name|match
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|input
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|match
argument_list|(
name|path
argument_list|,
name|input
argument_list|,
name|expected
argument_list|,
name|DEFAULT_DELTA
argument_list|)
return|;
block|}
comment|/**    * comparison using default delta    * @see #DEFAULT_DELTA    * @see #matchObj(String,Object,Object,double)    */
DECL|method|matchObj
specifier|public
specifier|static
name|String
name|matchObj
parameter_list|(
name|String
name|path
parameter_list|,
name|Object
name|input
parameter_list|,
name|Object
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|matchObj
argument_list|(
name|path
argument_list|,
name|input
argument_list|,
name|expected
argument_list|,
name|DEFAULT_DELTA
argument_list|)
return|;
block|}
comment|/**    * @param input JSON Structure to parse and test against    * @param pathAndExpected JSON path expression + '==' + expected value    * @param delta tollerance allowed in comparing float/double values    */
DECL|method|match
specifier|public
specifier|static
name|String
name|match
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|pathAndExpected
parameter_list|,
name|double
name|delta
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|pos
init|=
name|pathAndExpected
operator|.
name|indexOf
argument_list|(
literal|"=="
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|pos
operator|>=
literal|0
condition|?
name|pathAndExpected
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
else|:
literal|null
decl_stmt|;
name|String
name|expected
init|=
name|pos
operator|>=
literal|0
condition|?
name|pathAndExpected
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|2
argument_list|)
else|:
name|pathAndExpected
decl_stmt|;
return|return
name|match
argument_list|(
name|path
argument_list|,
name|input
argument_list|,
name|expected
argument_list|,
name|delta
argument_list|)
return|;
block|}
comment|/**    * @param path JSON path expression    * @param input JSON Structure to parse and test against    * @param expected expected value of path    * @param delta tollerance allowed in comparing float/double values    */
DECL|method|match
specifier|public
specifier|static
name|String
name|match
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|input
parameter_list|,
name|String
name|expected
parameter_list|,
name|double
name|delta
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
name|inputObj
init|=
name|failRepeatedKeys
condition|?
operator|new
name|NoDupsObjectBuilder
argument_list|(
operator|new
name|JSONParser
argument_list|(
name|input
argument_list|)
argument_list|)
operator|.
name|getVal
argument_list|()
else|:
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|Object
name|expectObj
init|=
name|failRepeatedKeys
condition|?
operator|new
name|NoDupsObjectBuilder
argument_list|(
operator|new
name|JSONParser
argument_list|(
name|expected
argument_list|)
argument_list|)
operator|.
name|getVal
argument_list|()
else|:
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|expected
argument_list|)
decl_stmt|;
return|return
name|matchObj
argument_list|(
name|path
argument_list|,
name|inputObj
argument_list|,
name|expectObj
argument_list|,
name|delta
argument_list|)
return|;
block|}
DECL|class|NoDupsObjectBuilder
specifier|static
class|class
name|NoDupsObjectBuilder
extends|extends
name|ObjectBuilder
block|{
DECL|method|NoDupsObjectBuilder
specifier|public
name|NoDupsObjectBuilder
parameter_list|(
name|JSONParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addKeyVal
specifier|public
name|void
name|addKeyVal
parameter_list|(
name|Object
name|map
parameter_list|,
name|Object
name|key
parameter_list|,
name|Object
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|prev
init|=
operator|(
operator|(
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
operator|)
name|map
operator|)
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"REPEATED JSON OBJECT KEY: key="
operator|+
name|key
operator|+
literal|" prevValue="
operator|+
name|prev
operator|+
literal|" thisValue"
operator|+
name|val
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * @param path JSON path expression    * @param input JSON Structure    * @param expected expected JSON Object    * @param delta tollerance allowed in comparing float/double values    */
DECL|method|matchObj
specifier|public
specifier|static
name|String
name|matchObj
parameter_list|(
name|String
name|path
parameter_list|,
name|Object
name|input
parameter_list|,
name|Object
name|expected
parameter_list|,
name|double
name|delta
parameter_list|)
block|{
name|CollectionTester
name|tester
init|=
operator|new
name|CollectionTester
argument_list|(
name|input
argument_list|,
name|delta
argument_list|)
decl_stmt|;
name|boolean
name|reversed
init|=
name|path
operator|.
name|startsWith
argument_list|(
literal|"!"
argument_list|)
decl_stmt|;
name|String
name|positivePath
init|=
name|reversed
condition|?
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
name|path
decl_stmt|;
if|if
condition|(
operator|!
name|tester
operator|.
name|seek
argument_list|(
name|positivePath
argument_list|)
operator|^
name|reversed
condition|)
block|{
return|return
literal|"Path not found: "
operator|+
name|path
return|;
block|}
if|if
condition|(
name|expected
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|tester
operator|.
name|match
argument_list|(
name|expected
argument_list|)
operator|^
name|reversed
operator|)
condition|)
block|{
return|return
name|tester
operator|.
name|err
operator|+
literal|" @ "
operator|+
name|tester
operator|.
name|getPath
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class
begin_comment
comment|/** Tests simple object graphs, like those generated by the noggit JSON parser */
end_comment
begin_class
DECL|class|CollectionTester
class|class
name|CollectionTester
block|{
DECL|field|valRoot
specifier|public
name|Object
name|valRoot
decl_stmt|;
DECL|field|val
specifier|public
name|Object
name|val
decl_stmt|;
DECL|field|expectedRoot
specifier|public
name|Object
name|expectedRoot
decl_stmt|;
DECL|field|expected
specifier|public
name|Object
name|expected
decl_stmt|;
DECL|field|delta
specifier|public
name|double
name|delta
decl_stmt|;
DECL|field|path
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|path
decl_stmt|;
DECL|field|err
specifier|public
name|String
name|err
decl_stmt|;
DECL|method|CollectionTester
specifier|public
name|CollectionTester
parameter_list|(
name|Object
name|val
parameter_list|,
name|double
name|delta
parameter_list|)
block|{
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
name|this
operator|.
name|valRoot
operator|=
name|val
expr_stmt|;
name|this
operator|.
name|delta
operator|=
name|delta
expr_stmt|;
name|path
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|CollectionTester
specifier|public
name|CollectionTester
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
name|this
argument_list|(
name|val
argument_list|,
name|JSONTestUtil
operator|.
name|DEFAULT_DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Object
name|seg
range|:
name|path
control|)
block|{
if|if
condition|(
name|seg
operator|==
literal|null
condition|)
break|break;
if|if
condition|(
operator|!
name|first
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
else|else
name|first
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|seg
operator|instanceof
name|Integer
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|seg
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|seg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setPath
name|void
name|setPath
parameter_list|(
name|Object
name|lastSeg
parameter_list|)
block|{
name|path
operator|.
name|set
argument_list|(
name|path
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|lastSeg
argument_list|)
expr_stmt|;
block|}
DECL|method|popPath
name|Object
name|popPath
parameter_list|()
block|{
return|return
name|path
operator|.
name|remove
argument_list|(
name|path
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|pushPath
name|void
name|pushPath
parameter_list|(
name|Object
name|lastSeg
parameter_list|)
block|{
name|path
operator|.
name|add
argument_list|(
name|lastSeg
argument_list|)
expr_stmt|;
block|}
DECL|method|setErr
name|void
name|setErr
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|err
operator|=
name|msg
expr_stmt|;
block|}
DECL|method|match
specifier|public
name|boolean
name|match
parameter_list|(
name|Object
name|expected
parameter_list|)
block|{
name|this
operator|.
name|expectedRoot
operator|=
name|expected
expr_stmt|;
name|this
operator|.
name|expected
operator|=
name|expected
expr_stmt|;
return|return
name|match
argument_list|()
return|;
block|}
DECL|method|match
name|boolean
name|match
parameter_list|()
block|{
if|if
condition|(
name|expected
operator|==
name|val
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|expected
operator|==
literal|null
operator|||
name|val
operator|==
literal|null
condition|)
block|{
name|setErr
argument_list|(
literal|"mismatch: '"
operator|+
name|expected
operator|+
literal|"'!='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|expected
operator|instanceof
name|List
condition|)
block|{
return|return
name|matchList
argument_list|()
return|;
block|}
if|if
condition|(
name|expected
operator|instanceof
name|Map
condition|)
block|{
return|return
name|matchMap
argument_list|()
return|;
block|}
comment|// generic fallback
if|if
condition|(
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|val
argument_list|)
condition|)
block|{
if|if
condition|(
name|expected
operator|instanceof
name|String
condition|)
block|{
name|String
name|str
init|=
operator|(
name|String
operator|)
name|expected
decl_stmt|;
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|>
literal|6
operator|&&
name|str
operator|.
name|startsWith
argument_list|(
literal|"///"
argument_list|)
operator|&&
name|str
operator|.
name|endsWith
argument_list|(
literal|"///"
argument_list|)
condition|)
block|{
return|return
name|handleSpecialString
argument_list|(
name|str
argument_list|)
return|;
block|}
block|}
comment|// make an exception for some numerics
if|if
condition|(
operator|(
name|expected
operator|instanceof
name|Integer
operator|&&
name|val
operator|instanceof
name|Long
operator|||
name|expected
operator|instanceof
name|Long
operator|&&
name|val
operator|instanceof
name|Integer
operator|)
operator|&&
operator|(
operator|(
name|Number
operator|)
name|expected
operator|)
operator|.
name|longValue
argument_list|()
operator|==
operator|(
operator|(
name|Number
operator|)
name|val
operator|)
operator|.
name|longValue
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|expected
operator|instanceof
name|Double
operator|||
name|expected
operator|instanceof
name|Float
operator|)
operator|&&
operator|(
name|val
operator|instanceof
name|Double
operator|||
name|val
operator|instanceof
name|Float
operator|)
condition|)
block|{
name|double
name|a
init|=
operator|(
operator|(
name|Number
operator|)
name|expected
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|double
name|b
init|=
operator|(
operator|(
name|Number
operator|)
name|val
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
operator|==
literal|0
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|a
operator|-
name|b
argument_list|)
operator|<
name|delta
condition|)
return|return
literal|true
return|;
block|}
name|setErr
argument_list|(
literal|"mismatch: '"
operator|+
name|expected
operator|+
literal|"'!='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// setErr("unknown expected type " + expected.getClass().getName());
return|return
literal|true
return|;
block|}
DECL|method|handleSpecialString
specifier|private
name|boolean
name|handleSpecialString
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|String
name|code
init|=
name|str
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|str
operator|.
name|length
argument_list|()
operator|-
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"ignore"
operator|.
name|equals
argument_list|(
name|code
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|code
operator|.
name|startsWith
argument_list|(
literal|"regex:"
argument_list|)
condition|)
block|{
name|String
name|regex
init|=
name|code
operator|.
name|substring
argument_list|(
literal|"regex:"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|val
operator|instanceof
name|String
operator|)
condition|)
block|{
name|setErr
argument_list|(
literal|"mismatch: '"
operator|+
name|expected
operator|+
literal|"'!='"
operator|+
name|val
operator|+
literal|"', value is not a string"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
operator|(
name|String
operator|)
name|val
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|setErr
argument_list|(
literal|"mismatch: '"
operator|+
name|expected
operator|+
literal|"'!='"
operator|+
name|val
operator|+
literal|"', regex does not match"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|setErr
argument_list|(
literal|"mismatch: '"
operator|+
name|expected
operator|+
literal|"'!='"
operator|+
name|val
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|matchList
name|boolean
name|matchList
parameter_list|()
block|{
name|List
name|expectedList
init|=
operator|(
name|List
operator|)
name|expected
decl_stmt|;
name|List
name|v
init|=
name|asList
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|int
name|a
init|=
literal|0
decl_stmt|;
name|int
name|b
init|=
literal|0
decl_stmt|;
name|pushPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|a
operator|>=
name|expectedList
operator|.
name|size
argument_list|()
operator|&&
name|b
operator|>=
name|v
operator|.
name|size
argument_list|()
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|a
operator|>=
name|expectedList
operator|.
name|size
argument_list|()
operator|||
name|b
operator|>=
name|v
operator|.
name|size
argument_list|()
condition|)
block|{
name|popPath
argument_list|()
expr_stmt|;
name|setErr
argument_list|(
literal|"List size mismatch"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|expected
operator|=
name|expectedList
operator|.
name|get
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|val
operator|=
name|v
operator|.
name|get
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|setPath
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|match
argument_list|()
condition|)
return|return
literal|false
return|;
name|a
operator|++
expr_stmt|;
name|b
operator|++
expr_stmt|;
block|}
name|popPath
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|field|reserved
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|reserved
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"_SKIP_"
argument_list|,
literal|"_MATCH_"
argument_list|,
literal|"_ORDERED_"
argument_list|,
literal|"_UNORDERED_"
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|matchMap
name|boolean
name|matchMap
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|expected
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|v
init|=
name|asMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|boolean
name|ordered
init|=
literal|false
decl_stmt|;
name|String
name|skipList
init|=
operator|(
name|String
operator|)
name|expectedMap
operator|.
name|get
argument_list|(
literal|"_SKIP_"
argument_list|)
decl_stmt|;
name|String
name|matchList
init|=
operator|(
name|String
operator|)
name|expectedMap
operator|.
name|get
argument_list|(
literal|"_MATCH_"
argument_list|)
decl_stmt|;
name|Object
name|orderedStr
init|=
name|expectedMap
operator|.
name|get
argument_list|(
literal|"_ORDERED_"
argument_list|)
decl_stmt|;
name|Object
name|unorderedStr
init|=
name|expectedMap
operator|.
name|get
argument_list|(
literal|"_UNORDERED_"
argument_list|)
decl_stmt|;
if|if
condition|(
name|orderedStr
operator|!=
literal|null
condition|)
name|ordered
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|unorderedStr
operator|!=
literal|null
condition|)
name|ordered
operator|=
literal|false
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|match
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|matchList
operator|!=
literal|null
condition|)
block|{
name|match
operator|=
operator|new
name|HashSet
argument_list|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|matchList
argument_list|,
literal|","
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|skips
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|skipList
operator|!=
literal|null
condition|)
block|{
name|skips
operator|=
operator|new
name|HashSet
argument_list|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|skipList
argument_list|,
literal|","
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
name|match
operator|!=
literal|null
condition|?
name|match
else|:
name|expectedMap
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|visited
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iter
init|=
name|ordered
condition|?
name|v
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
else|:
literal|null
decl_stmt|;
name|int
name|numExpected
init|=
literal|0
decl_stmt|;
name|pushPath
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|expectedKey
range|:
name|keys
control|)
block|{
if|if
condition|(
name|reserved
operator|.
name|contains
argument_list|(
name|expectedKey
argument_list|)
condition|)
continue|continue;
name|numExpected
operator|++
expr_stmt|;
name|setPath
argument_list|(
name|expectedKey
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|v
operator|.
name|containsKey
argument_list|(
name|expectedKey
argument_list|)
condition|)
block|{
name|popPath
argument_list|()
expr_stmt|;
name|setErr
argument_list|(
literal|"expected key '"
operator|+
name|expectedKey
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|expected
operator|=
name|expectedMap
operator|.
name|get
argument_list|(
name|expectedKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|ordered
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
decl_stmt|;
name|String
name|foundKey
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|popPath
argument_list|()
expr_stmt|;
name|setErr
argument_list|(
literal|"expected key '"
operator|+
name|expectedKey
operator|+
literal|"' in ordered map"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|entry
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|foundKey
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|skips
operator|!=
literal|null
operator|&&
name|skips
operator|.
name|contains
argument_list|(
name|foundKey
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|match
operator|!=
literal|null
operator|&&
operator|!
name|match
operator|.
name|contains
argument_list|(
name|foundKey
argument_list|)
condition|)
continue|continue;
break|break;
block|}
if|if
condition|(
operator|!
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|expectedKey
argument_list|)
condition|)
block|{
name|popPath
argument_list|()
expr_stmt|;
name|setErr
argument_list|(
literal|"expected key '"
operator|+
name|expectedKey
operator|+
literal|"' instead of '"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"' in ordered map"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|val
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|skips
operator|!=
literal|null
operator|&&
name|skips
operator|.
name|contains
argument_list|(
name|expectedKey
argument_list|)
condition|)
continue|continue;
name|val
operator|=
name|v
operator|.
name|get
argument_list|(
name|expectedKey
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|match
argument_list|()
condition|)
return|return
literal|false
return|;
block|}
name|popPath
argument_list|()
expr_stmt|;
comment|// now check if there were any extra keys in the value (as long as there wasn't a specific list to include)
if|if
condition|(
name|match
operator|==
literal|null
condition|)
block|{
name|int
name|skipped
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|skips
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|skipStr
range|:
name|skips
control|)
if|if
condition|(
name|v
operator|.
name|containsKey
argument_list|(
name|skipStr
argument_list|)
condition|)
name|skipped
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|numExpected
operator|!=
operator|(
name|v
operator|.
name|size
argument_list|()
operator|-
name|skipped
operator|)
condition|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|v
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|set
operator|.
name|removeAll
argument_list|(
name|expectedMap
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|setErr
argument_list|(
literal|"unexpected map keys "
operator|+
name|set
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|seek
specifier|public
name|boolean
name|seek
parameter_list|(
name|String
name|seekPath
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|seekPath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|seekPath
operator|=
name|seekPath
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|seekPath
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|seekPath
operator|=
name|seekPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|seekPath
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|pathList
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|seekPath
argument_list|,
literal|"/"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|seek
argument_list|(
name|pathList
argument_list|)
return|;
block|}
DECL|method|asList
name|List
name|asList
parameter_list|()
block|{
comment|// TODO: handle native arrays
if|if
condition|(
name|val
operator|instanceof
name|List
condition|)
block|{
return|return
operator|(
name|List
operator|)
name|val
return|;
block|}
name|setErr
argument_list|(
literal|"expected List"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|asMap
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|asMap
parameter_list|()
block|{
comment|// TODO: handle NamedList
if|if
condition|(
name|val
operator|instanceof
name|Map
condition|)
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|val
return|;
block|}
name|setErr
argument_list|(
literal|"expected Map"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
DECL|method|seek
specifier|public
name|boolean
name|seek
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|seekPath
parameter_list|)
block|{
if|if
condition|(
name|seekPath
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|true
return|;
name|String
name|seg
init|=
name|seekPath
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|seg
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'['
condition|)
block|{
name|List
name|listVal
init|=
name|asList
argument_list|()
decl_stmt|;
if|if
condition|(
name|listVal
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|int
name|arrIdx
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|seg
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|seg
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|arrIdx
operator|>=
name|listVal
operator|.
name|size
argument_list|()
condition|)
return|return
literal|false
return|;
name|val
operator|=
name|listVal
operator|.
name|get
argument_list|(
name|arrIdx
argument_list|)
expr_stmt|;
name|pushPath
argument_list|(
name|arrIdx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapVal
init|=
name|asMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|mapVal
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|// use containsKey rather than get to handle null values
if|if
condition|(
operator|!
name|mapVal
operator|.
name|containsKey
argument_list|(
name|seg
argument_list|)
condition|)
return|return
literal|false
return|;
name|val
operator|=
name|mapVal
operator|.
name|get
argument_list|(
name|seg
argument_list|)
expr_stmt|;
name|pushPath
argument_list|(
name|seg
argument_list|)
expr_stmt|;
block|}
comment|// recurse after removing head of the path
return|return
name|seek
argument_list|(
name|seekPath
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|seekPath
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class
end_unit
