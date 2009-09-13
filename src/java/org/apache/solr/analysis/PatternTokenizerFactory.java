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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|Token
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
name|Tokenizer
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
name|SolrException
import|;
end_import
begin_comment
comment|/**  * This tokenizer uses regex pattern matching to construct distinct tokens  * for the input stream.  It takes two arguments:  "pattern" and "group".  *<p/>  *<ul>  *<li>"pattern" is the regular expression.</li>  *<li>"group" says which group to extract into tokens.</li>  *</ul>  *<p>  * group=-1 (the default) is equivalent to "split".  In this case, the tokens will  * be equivalent to the output from:  *  * http://java.sun.com/j2se/1.4.2/docs/api/java/lang/String.html#split(java.lang.String)  *</p>  *<p>  * Using group>= 0 selects the matching group as the token.  For example, if you have:<br/>  *<pre>  *  pattern = \'([^\']+)\'  *  group = 0  *  input = aaa 'bbb' 'ccc'  *</pre>  * the output will be two tokens: 'bbb' and 'ccc' (including the ' marks).  With the same input  * but using group=1, the output would be: bbb and ccc (no ' marks)  *</p>  *  * @since solr1.2  * @version $Id:$  */
end_comment
begin_class
DECL|class|PatternTokenizerFactory
specifier|public
class|class
name|PatternTokenizerFactory
extends|extends
name|BaseTokenizerFactory
block|{
DECL|field|PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN
init|=
literal|"pattern"
decl_stmt|;
DECL|field|GROUP
specifier|public
specifier|static
specifier|final
name|String
name|GROUP
init|=
literal|"group"
decl_stmt|;
DECL|field|args
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
DECL|field|pattern
specifier|protected
name|Pattern
name|pattern
decl_stmt|;
DECL|field|group
specifier|protected
name|int
name|group
decl_stmt|;
comment|/**    * Require a configured pattern    */
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|String
name|regex
init|=
name|args
operator|.
name|get
argument_list|(
name|PATTERN
argument_list|)
decl_stmt|;
if|if
condition|(
name|regex
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"missing required argument: "
operator|+
name|PATTERN
argument_list|)
throw|;
block|}
name|int
name|flags
init|=
literal|0
decl_stmt|;
comment|// TODO? -- read flags from config CASE_INSENSITIVE, etc
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|group
operator|=
operator|-
literal|1
expr_stmt|;
comment|// use 'split'
name|String
name|g
init|=
name|args
operator|.
name|get
argument_list|(
name|GROUP
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|group
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|g
argument_list|)
expr_stmt|;
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
name|SERVER_ERROR
argument_list|,
literal|"invalid group argument: "
operator|+
name|g
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Split the input using configured pattern    */
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|(
specifier|final
name|Reader
name|in
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|Tokenizer
argument_list|(
name|in
argument_list|)
block|{
block|{
name|init
parameter_list|()
constructor_decl|;
block|}
name|List
argument_list|<
name|Token
argument_list|>
name|tokens
decl_stmt|;
name|Iterator
argument_list|<
name|Token
argument_list|>
name|iter
decl_stmt|;
name|void
name|init
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Read the input into a single string
name|String
name|str
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|tokens
operator|=
operator|(
name|group
operator|<
literal|0
operator|)
condition|?
name|split
argument_list|(
name|matcher
argument_list|,
name|str
argument_list|,
name|input
argument_list|)
else|:
name|group
argument_list|(
name|matcher
argument_list|,
name|str
argument_list|,
name|group
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|iter
operator|=
name|tokens
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
comment|//        @Override
comment|//        public boolean incrementToken() throws IOException {
comment|//          return super.incrementToken();
comment|//        }
annotation|@
name|Override
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
comment|//        @Override
comment|//        public Token next(Token reusableToken) throws IOException {
comment|//          return super.next(reusableToken);
comment|//        }
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|iter
operator|.
name|next
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
catch|catch
parameter_list|(
name|IOException
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
name|SERVER_ERROR
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * @deprecated    */
DECL|method|split
specifier|public
specifier|static
name|List
argument_list|<
name|Token
argument_list|>
name|split
parameter_list|(
name|Matcher
name|matcher
parameter_list|,
name|String
name|input
parameter_list|)
block|{
return|return
name|split
argument_list|(
name|matcher
argument_list|,
name|input
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * This behaves just like String.split( ), but returns a list of Tokens    * rather then an array of strings    */
DECL|method|split
specifier|public
specifier|static
name|List
argument_list|<
name|Token
argument_list|>
name|split
parameter_list|(
name|Matcher
name|matcher
parameter_list|,
name|String
name|input
parameter_list|,
name|Reader
name|stream
parameter_list|)
block|{
name|int
name|index
init|=
literal|0
decl_stmt|;
name|int
name|lastNonEmptySize
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|matchList
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
comment|// Add segments before each match found
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|match
init|=
name|input
operator|.
name|subSequence
argument_list|(
name|index
argument_list|,
name|matcher
operator|.
name|start
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|matchList
operator|.
name|add
argument_list|(
name|newToken
argument_list|(
name|stream
argument_list|,
name|match
argument_list|,
name|index
argument_list|,
name|matcher
operator|.
name|start
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|=
name|matcher
operator|.
name|end
argument_list|()
expr_stmt|;
if|if
condition|(
name|match
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lastNonEmptySize
operator|=
name|matchList
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
comment|// If no match is found, return the full string
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
name|matchList
operator|.
name|add
argument_list|(
name|newToken
argument_list|(
name|stream
argument_list|,
name|input
argument_list|,
literal|0
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|match
init|=
name|input
operator|.
name|subSequence
argument_list|(
name|index
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|matchList
operator|.
name|add
argument_list|(
name|newToken
argument_list|(
name|stream
argument_list|,
name|match
argument_list|,
name|index
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|match
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|lastNonEmptySize
operator|=
name|matchList
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Don't use trailing empty strings.  This behavior matches String.split();
if|if
condition|(
name|lastNonEmptySize
operator|<
name|matchList
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|matchList
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|lastNonEmptySize
argument_list|)
return|;
block|}
return|return
name|matchList
return|;
block|}
comment|/**    * @deprecated    */
DECL|method|group
specifier|public
specifier|static
name|List
argument_list|<
name|Token
argument_list|>
name|group
parameter_list|(
name|Matcher
name|matcher
parameter_list|,
name|String
name|input
parameter_list|,
name|int
name|group
parameter_list|)
block|{
return|return
name|group
argument_list|(
name|matcher
argument_list|,
name|input
argument_list|,
name|group
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Create tokens from the matches in a matcher     */
DECL|method|group
specifier|public
specifier|static
name|List
argument_list|<
name|Token
argument_list|>
name|group
parameter_list|(
name|Matcher
name|matcher
parameter_list|,
name|String
name|input
parameter_list|,
name|int
name|group
parameter_list|,
name|Reader
name|stream
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|matchList
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|Token
name|t
init|=
name|newToken
argument_list|(
name|stream
argument_list|,
name|matcher
operator|.
name|group
argument_list|(
name|group
argument_list|)
argument_list|,
name|matcher
operator|.
name|start
argument_list|(
name|group
argument_list|)
argument_list|,
name|matcher
operator|.
name|end
argument_list|(
name|group
argument_list|)
argument_list|)
decl_stmt|;
name|matchList
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
return|return
name|matchList
return|;
block|}
DECL|method|newToken
specifier|private
specifier|static
name|Token
name|newToken
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|text
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|Token
name|token
decl_stmt|;
if|if
condition|(
name|reader
operator|instanceof
name|CharStream
condition|)
block|{
name|CharStream
name|stream
init|=
operator|(
name|CharStream
operator|)
name|reader
decl_stmt|;
name|token
operator|=
operator|new
name|Token
argument_list|(
name|text
argument_list|,
name|stream
operator|.
name|correctOffset
argument_list|(
name|start
argument_list|)
argument_list|,
name|stream
operator|.
name|correctOffset
argument_list|(
name|end
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|token
operator|=
operator|new
name|Token
argument_list|(
name|text
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
block|}
end_class
end_unit
