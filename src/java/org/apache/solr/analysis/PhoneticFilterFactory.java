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
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_comment
comment|/**  * Create tokens based on phonetic encoders  *   * http://jakarta.apache.org/commons/codec/api-release/org/apache/commons/codec/language/package-summary.html  *   * This takes two arguments:  *  "encoder" required, one of "DoubleMetaphone", "Metaphone", "Soundex", "RefinedSoundex"  *   * "inject" (default=true) add tokens to the stream with the offset=0  *   * @version $Id$  * @see PhoneticFilter  */
end_comment
begin_class
DECL|class|PhoneticFilterFactory
specifier|public
class|class
name|PhoneticFilterFactory
extends|extends
name|BaseTokenFilterFactory
block|{
DECL|field|ENCODER
specifier|public
specifier|static
specifier|final
name|String
name|ENCODER
init|=
literal|"encoder"
decl_stmt|;
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
decl_stmt|;
static|static
block|{
name|registry
operator|=
operator|new
name|HashMap
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
argument_list|()
expr_stmt|;
name|registry
operator|.
name|put
argument_list|(
literal|"DoubleMetaphone"
operator|.
name|toUpperCase
argument_list|()
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
argument_list|()
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
argument_list|()
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
argument_list|()
argument_list|,
name|RefinedSoundex
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|inject
specifier|protected
name|boolean
name|inject
init|=
literal|true
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
init|=
literal|null
decl_stmt|;
DECL|field|encoder
specifier|protected
name|Encoder
name|encoder
init|=
literal|null
decl_stmt|;
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
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|get
argument_list|(
literal|"inject"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|inject
operator|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|INJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|args
operator|.
name|get
argument_list|(
name|ENCODER
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
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
literal|"Missing required parameter: "
operator|+
name|ENCODER
operator|+
literal|" ["
operator|+
name|registry
operator|.
name|keySet
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|Encoder
argument_list|>
name|clazz
init|=
name|registry
operator|.
name|get
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
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
literal|"Unknown encoder: "
operator|+
name|name
operator|+
literal|" ["
operator|+
name|registry
operator|.
name|keySet
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
try|try
block|{
name|encoder
operator|=
name|clazz
operator|.
name|newInstance
argument_list|()
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error initializing: "
operator|+
name|name
operator|+
literal|"/"
operator|+
name|clazz
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|create
specifier|public
name|TokenStream
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
name|encoder
argument_list|,
name|name
argument_list|,
name|inject
argument_list|)
return|;
block|}
block|}
end_class
end_unit
