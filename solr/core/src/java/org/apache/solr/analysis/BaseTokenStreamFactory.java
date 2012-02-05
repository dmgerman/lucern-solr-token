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
name|core
operator|.
name|Config
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
name|schema
operator|.
name|IndexSchema
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
operator|.
name|StopFilter
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
name|util
operator|.
name|CharArraySet
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
name|util
operator|.
name|WordlistLoader
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
name|util
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
name|util
operator|.
name|Version
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
begin_comment
comment|/**  * Simple abstract implementation that handles init arg processing, is not really  * a factory as it implements no interface, but removes code duplication  * in its subclasses.  *   *  */
end_comment
begin_class
DECL|class|BaseTokenStreamFactory
specifier|abstract
class|class
name|BaseTokenStreamFactory
block|{
comment|/** The init args */
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
comment|/** the luceneVersion arg */
DECL|field|luceneMatchVersion
specifier|protected
name|Version
name|luceneMatchVersion
init|=
literal|null
decl_stmt|;
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BaseTokenStreamFactory
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|matchVersion
init|=
name|args
operator|.
name|get
argument_list|(
name|IndexSchema
operator|.
name|LUCENE_MATCH_VERSION_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchVersion
operator|!=
literal|null
condition|)
block|{
name|luceneMatchVersion
operator|=
name|Config
operator|.
name|parseLuceneVersionString
argument_list|(
name|matchVersion
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getArgs
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getArgs
parameter_list|()
block|{
return|return
name|args
return|;
block|}
comment|/** this method can be called in the {@link TokenizerFactory#create(java.io.Reader)}     * or {@link TokenFilterFactory#create(org.apache.lucene.analysis.TokenStream)} methods,    * to inform user, that for this factory a {@link #luceneMatchVersion} is required */
DECL|method|assureMatchVersion
specifier|protected
specifier|final
name|void
name|assureMatchVersion
parameter_list|()
block|{
if|if
condition|(
name|luceneMatchVersion
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Configuration Error: Factory '"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' needs a 'luceneMatchVersion' parameter"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|luceneMatchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_40
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" is using deprecated "
operator|+
name|luceneMatchVersion
operator|+
literal|" emulation. You should at some point declare and reindex to at least 4.0, because "
operator|+
literal|"3.x emulation is deprecated and will be removed in 5.0"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|warnDeprecated
specifier|protected
specifier|final
name|void
name|warnDeprecated
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" is deprecated. "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
comment|// TODO: move these somewhere that tokenizers and others
comment|// can also use them...
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getInt
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|defaultVal
parameter_list|)
block|{
return|return
name|getInt
argument_list|(
name|name
argument_list|,
name|defaultVal
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|defaultVal
parameter_list|,
name|boolean
name|useDefault
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|useDefault
condition|)
return|return
name|defaultVal
return|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Configuration Error: missing parameter '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|getBoolean
specifier|protected
name|boolean
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|defaultVal
parameter_list|)
block|{
return|return
name|getBoolean
argument_list|(
name|name
argument_list|,
name|defaultVal
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getBoolean
specifier|protected
name|boolean
name|getBoolean
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|defaultVal
parameter_list|,
name|boolean
name|useDefault
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|useDefault
condition|)
return|return
name|defaultVal
return|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Configuration Error: missing parameter '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|s
argument_list|)
return|;
block|}
DECL|method|getWordSet
specifier|protected
name|CharArraySet
name|getWordSet
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|wordFiles
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
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
name|wordFiles
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// default stopwords list has 35 or so words, but maybe don't make it that
comment|// big to start
name|words
operator|=
operator|new
name|CharArraySet
argument_list|(
name|luceneMatchVersion
argument_list|,
name|files
operator|.
name|size
argument_list|()
operator|*
literal|10
argument_list|,
name|ignoreCase
argument_list|)
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
name|wlist
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
name|words
operator|.
name|addAll
argument_list|(
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|luceneMatchVersion
argument_list|,
name|wlist
argument_list|,
name|ignoreCase
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|words
return|;
block|}
comment|/** same as {@link #getWordSet(ResourceLoader, String, boolean)},    * except the input is in snowball format. */
DECL|method|getSnowballWordSet
specifier|protected
name|CharArraySet
name|getSnowballWordSet
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|wordFiles
parameter_list|,
name|boolean
name|ignoreCase
parameter_list|)
throws|throws
name|IOException
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
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
name|wordFiles
argument_list|)
decl_stmt|;
name|CharArraySet
name|words
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// default stopwords list has 35 or so words, but maybe don't make it that
comment|// big to start
name|words
operator|=
operator|new
name|CharArraySet
argument_list|(
name|luceneMatchVersion
argument_list|,
name|files
operator|.
name|size
argument_list|()
operator|*
literal|10
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
name|Reader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|file
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|CharsetDecoder
name|decoder
init|=
name|IOUtils
operator|.
name|CHARSET_UTF_8
operator|.
name|newDecoder
argument_list|()
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
decl_stmt|;
name|reader
operator|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|decoder
argument_list|)
expr_stmt|;
name|WordlistLoader
operator|.
name|getSnowballWordSet
argument_list|(
name|reader
argument_list|,
name|words
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|reader
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|words
return|;
block|}
block|}
end_class
end_unit
