begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|CharStream
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
name|charfilter
operator|.
name|MappingCharFilter
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
name|charfilter
operator|.
name|NormalizeCharMap
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
name|ResourceLoader
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|plugin
operator|.
name|ResourceLoaderAware
import|;
end_import
begin_comment
comment|/**  * Factory for {@link MappingCharFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_map" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;charFilter class="solr.MappingCharFilterFactory" mapping="mapping.txt"/&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  * @version $Id$  * @since Solr 1.4  *  */
end_comment
begin_class
DECL|class|MappingCharFilterFactory
specifier|public
class|class
name|MappingCharFilterFactory
extends|extends
name|BaseCharFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|normMap
specifier|protected
name|NormalizeCharMap
name|normMap
decl_stmt|;
DECL|field|mapping
specifier|private
name|String
name|mapping
decl_stmt|;
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|mapping
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"mapping"
argument_list|)
expr_stmt|;
if|if
condition|(
name|mapping
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|mappingFile
init|=
operator|new
name|File
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappingFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|wlist
operator|=
name|loader
operator|.
name|getLines
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|StrUtils
operator|.
name|splitFileNames
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|wlist
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|loader
operator|.
name|getLines
argument_list|(
name|file
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|wlist
operator|.
name|addAll
argument_list|(
name|lines
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|normMap
operator|=
operator|new
name|NormalizeCharMap
argument_list|()
expr_stmt|;
name|parseRules
argument_list|(
name|wlist
argument_list|,
name|normMap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|create
specifier|public
name|CharStream
name|create
parameter_list|(
name|CharStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
name|input
argument_list|)
return|;
block|}
comment|// "source" => "target"
DECL|field|p
specifier|static
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\"(.*)\"\\s*=>\\s*\"(.*)\"\\s*$"
argument_list|)
decl_stmt|;
DECL|method|parseRules
specifier|protected
name|void
name|parseRules
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|rules
parameter_list|,
name|NormalizeCharMap
name|normMap
parameter_list|)
block|{
for|for
control|(
name|String
name|rule
range|:
name|rules
control|)
block|{
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|rule
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid Mapping Rule : ["
operator|+
name|rule
operator|+
literal|"], file = "
operator|+
name|mapping
argument_list|)
throw|;
name|normMap
operator|.
name|add
argument_list|(
name|parseString
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|parseString
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|out
name|char
index|[]
name|out
init|=
operator|new
name|char
index|[
literal|256
index|]
decl_stmt|;
DECL|method|parseString
specifier|protected
name|String
name|parseString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|readPos
init|=
literal|0
decl_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|writePos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|readPos
operator|<
name|len
condition|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|readPos
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
name|readPos
operator|>=
name|len
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid escaped char in ["
operator|+
name|s
operator|+
literal|"]"
argument_list|)
throw|;
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|readPos
operator|++
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\\'
case|:
name|c
operator|=
literal|'\\'
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
name|c
operator|=
literal|'"'
expr_stmt|;
break|break;
case|case
literal|'n'
case|:
name|c
operator|=
literal|'\n'
expr_stmt|;
break|break;
case|case
literal|'t'
case|:
name|c
operator|=
literal|'\t'
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|c
operator|=
literal|'\r'
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|c
operator|=
literal|'\b'
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|c
operator|=
literal|'\f'
expr_stmt|;
break|break;
case|case
literal|'u'
case|:
if|if
condition|(
name|readPos
operator|+
literal|3
operator|>=
name|len
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid escaped char in ["
operator|+
name|s
operator|+
literal|"]"
argument_list|)
throw|;
name|c
operator|=
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|readPos
argument_list|,
name|readPos
operator|+
literal|4
argument_list|)
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|readPos
operator|+=
literal|4
expr_stmt|;
break|break;
block|}
block|}
name|out
index|[
name|writePos
operator|++
index|]
operator|=
name|c
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|writePos
argument_list|)
return|;
block|}
block|}
end_class
end_unit
