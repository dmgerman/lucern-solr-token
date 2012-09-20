begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|icu
operator|.
name|ICUNormalizer2Filter
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
name|AbstractAnalysisFactory
import|;
end_import
begin_comment
comment|// javadocs
end_comment
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
name|MultiTermAwareComponent
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
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|FilteredNormalizer2
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Normalizer2
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|UnicodeSet
import|;
end_import
begin_comment
comment|/**  * Factory for {@link ICUNormalizer2Filter}  *<p>  * Supports the following attributes:  *<ul>  *<li>name: A<a href="http://unicode.org/reports/tr15/">Unicode Normalization Form</a>,   *       one of 'nfc','nfkc', 'nfkc_cf'. Default is nfkc_cf.  *<li>mode: Either 'compose' or 'decompose'. Default is compose. Use "decompose" with nfc  *       or nfkc, to get nfd or nfkd, respectively.  *<li>filter: A {@link UnicodeSet} pattern. Codepoints outside the set are  *       always left unchanged. Default is [] (the null set, no filtering).  *</ul>  * @see ICUNormalizer2Filter  * @see Normalizer2  * @see FilteredNormalizer2  */
end_comment
begin_class
DECL|class|ICUNormalizer2FilterFactory
specifier|public
class|class
name|ICUNormalizer2FilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|MultiTermAwareComponent
block|{
DECL|field|normalizer
specifier|private
name|Normalizer2
name|normalizer
decl_stmt|;
comment|/** Sole constructor. See {@link AbstractAnalysisFactory} for initialization lifecycle. */
DECL|method|ICUNormalizer2FilterFactory
specifier|public
name|ICUNormalizer2FilterFactory
parameter_list|()
block|{}
comment|// TODO: support custom normalization
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
name|String
name|name
init|=
name|args
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
name|name
operator|=
literal|"nfkc_cf"
expr_stmt|;
name|String
name|mode
init|=
name|args
operator|.
name|get
argument_list|(
literal|"mode"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mode
operator|==
literal|null
condition|)
name|mode
operator|=
literal|"compose"
expr_stmt|;
if|if
condition|(
name|mode
operator|.
name|equals
argument_list|(
literal|"compose"
argument_list|)
condition|)
name|normalizer
operator|=
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
name|name
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|mode
operator|.
name|equals
argument_list|(
literal|"decompose"
argument_list|)
condition|)
name|normalizer
operator|=
name|Normalizer2
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
name|name
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|DECOMPOSE
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid mode: "
operator|+
name|mode
argument_list|)
throw|;
name|String
name|filter
init|=
name|args
operator|.
name|get
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|UnicodeSet
name|set
init|=
operator|new
name|UnicodeSet
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|set
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|set
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|normalizer
operator|=
operator|new
name|FilteredNormalizer2
argument_list|(
name|normalizer
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
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
name|ICUNormalizer2Filter
argument_list|(
name|input
argument_list|,
name|normalizer
argument_list|)
return|;
block|}
DECL|method|getMultiTermComponent
specifier|public
name|AbstractAnalysisFactory
name|getMultiTermComponent
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class
end_unit
