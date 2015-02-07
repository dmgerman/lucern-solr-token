begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|Encoder
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
name|codec
operator|.
name|language
operator|.
name|Caverphone2
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
name|codec
operator|.
name|language
operator|.
name|ColognePhonetic
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
name|codec
operator|.
name|language
operator|.
name|DoubleMetaphone
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
name|codec
operator|.
name|language
operator|.
name|Metaphone
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
name|codec
operator|.
name|language
operator|.
name|Nysiis
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
name|codec
operator|.
name|language
operator|.
name|RefinedSoundex
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
name|codec
operator|.
name|language
operator|.
name|Soundex
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
name|TokenStream
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
name|ResourceLoader
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
name|ResourceLoaderAware
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
name|TokenFilterFactory
import|;
end_import
begin_comment
comment|/**  * Factory for {@link PhoneticFilter}.  *   * Create tokens based on phonetic encoders from   *<a href="http://commons.apache.org/codec/api-release/org/apache/commons/codec/language/package-summary.html">Apache Commons Codec</a>.  *<p>  * This takes one required argument, "encoder", and the rest are optional:  *<dl>  *<dt>encoder</dt><dd> required, one of "DoubleMetaphone", "Metaphone", "Soundex", "RefinedSoundex", "Caverphone" (v2.0),  *  "ColognePhonetic" or "Nysiis" (case insensitive). If encoder isn't one of these, it'll be resolved as a class name  *  either by itself if it already contains a '.' or otherwise as in the same package as these others.</dd>  *<dt>inject</dt><dd> (default=true) add tokens to the stream with the offset=0</dd>  *<dt>maxCodeLength</dt><dd>The maximum length of the phonetic codes, as defined by the encoder. If an encoder doesn't  *  support this then specifying this is an error.</dd>  *</dl>  *  *<pre class="prettyprint">  *&lt;fieldType name="text_phonetic" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.PhoneticFilterFactory" encoder="DoubleMetaphone" inject="true"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *   * @see PhoneticFilter  */
end_comment
begin_class
DECL|class|PhoneticFilterFactory
specifier|public
class|class
name|PhoneticFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
comment|/** parameter name: either a short name or a full class name */
DECL|field|ENCODER
specifier|public
specifier|static
specifier|final
name|String
name|ENCODER
init|=
literal|"encoder"
decl_stmt|;
comment|/** parameter name: true if encoded tokens should be added as synonyms */
DECL|field|INJECT
specifier|public
specifier|static
specifier|final
name|String
name|INJECT
init|=
literal|"inject"
decl_stmt|;
comment|// boolean
comment|/** parameter name: restricts the length of the phonetic code */
DECL|field|MAX_CODE_LENGTH
specifier|public
specifier|static
specifier|final
name|String
name|MAX_CODE_LENGTH
init|=
literal|"maxCodeLength"
decl_stmt|;
DECL|field|PACKAGE_CONTAINING_ENCODERS
specifier|private
specifier|static
specifier|final
name|String
name|PACKAGE_CONTAINING_ENCODERS
init|=
literal|"org.apache.commons.codec.language."
decl_stmt|;
comment|//Effectively constants; uppercase keys
DECL|field|registry
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
argument_list|>
name|registry
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|6
argument_list|)
decl_stmt|;
static|static
block|{
name|registry
operator|.
name|put
argument_list|(
literal|"DoubleMetaphone"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|DoubleMetaphone
operator|.
name|class
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"Metaphone"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|Metaphone
operator|.
name|class
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"Soundex"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|Soundex
operator|.
name|class
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"RefinedSoundex"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|RefinedSoundex
operator|.
name|class
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"Caverphone"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|Caverphone2
operator|.
name|class
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"ColognePhonetic"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|ColognePhonetic
operator|.
name|class
argument_list|)
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"Nysiis"
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|Nysiis
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|inject
specifier|final
name|boolean
name|inject
decl_stmt|;
comment|//accessed by the test
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|maxCodeLength
specifier|private
specifier|final
name|Integer
name|maxCodeLength
decl_stmt|;
DECL|field|clazz
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
name|clazz
init|=
literal|null
decl_stmt|;
DECL|field|setMaxCodeLenMethod
specifier|private
name|Method
name|setMaxCodeLenMethod
init|=
literal|null
decl_stmt|;
comment|/** Creates a new PhoneticFilterFactory */
DECL|method|PhoneticFilterFactory
specifier|public
name|PhoneticFilterFactory
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
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inject
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
name|INJECT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|name
operator|=
name|require
argument_list|(
name|args
argument_list|,
name|ENCODER
argument_list|)
expr_stmt|;
name|String
name|v
init|=
name|get
argument_list|(
name|args
argument_list|,
name|MAX_CODE_LENGTH
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|maxCodeLength
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|maxCodeLength
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
name|clazz
operator|=
name|registry
operator|.
name|get
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
name|clazz
operator|=
name|resolveEncoder
argument_list|(
name|name
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxCodeLength
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|setMaxCodeLenMethod
operator|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"setMaxCodeLen"
argument_list|,
name|int
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Encoder "
operator|+
name|name
operator|+
literal|" / "
operator|+
name|clazz
operator|+
literal|" does not support "
operator|+
name|MAX_CODE_LENGTH
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|getEncoder
argument_list|()
expr_stmt|;
comment|//trigger initialization for potential problems to be thrown now
block|}
DECL|method|resolveEncoder
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
name|resolveEncoder
parameter_list|(
name|String
name|name
parameter_list|,
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|String
name|lookupName
init|=
name|name
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|lookupName
operator|=
name|PACKAGE_CONTAINING_ENCODERS
operator|+
name|name
expr_stmt|;
block|}
try|try
block|{
return|return
name|loader
operator|.
name|newInstance
argument_list|(
name|lookupName
argument_list|,
name|Encoder
operator|.
name|class
argument_list|)
operator|.
name|getClass
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error loading encoder '"
operator|+
name|name
operator|+
literal|"': must be full class name or one of "
operator|+
name|registry
operator|.
name|keySet
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Must be thread-safe. */
DECL|method|getEncoder
specifier|protected
name|Encoder
name|getEncoder
parameter_list|()
block|{
comment|// Unfortunately, Commons-Codec doesn't offer any thread-safe guarantees so we must play it safe and instantiate
comment|// every time.  A simple benchmark showed this as negligible.
try|try
block|{
name|Encoder
name|encoder
init|=
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|// Try to set the maxCodeLength
if|if
condition|(
name|maxCodeLength
operator|!=
literal|null
operator|&&
name|setMaxCodeLenMethod
operator|!=
literal|null
condition|)
block|{
name|setMaxCodeLenMethod
operator|.
name|invoke
argument_list|(
name|encoder
argument_list|,
name|maxCodeLength
argument_list|)
expr_stmt|;
block|}
return|return
name|encoder
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|Throwable
name|t
init|=
operator|(
name|e
operator|instanceof
name|InvocationTargetException
operator|)
condition|?
name|e
operator|.
name|getCause
argument_list|()
else|:
name|e
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Error initializing encoder: "
operator|+
name|name
operator|+
literal|" / "
operator|+
name|clazz
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|PhoneticFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|PhoneticFilter
argument_list|(
name|input
argument_list|,
name|getEncoder
argument_list|()
argument_list|,
name|inject
argument_list|)
return|;
block|}
block|}
end_class
end_unit
