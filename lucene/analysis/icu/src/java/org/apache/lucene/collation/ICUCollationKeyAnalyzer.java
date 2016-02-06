begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.collation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
package|;
end_package
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
name|Collator
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
name|Analyzer
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
name|KeywordTokenizer
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
name|collation
operator|.
name|CollationKeyAnalyzer
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
name|util
operator|.
name|Version
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
begin_comment
comment|/**  *<p>  *   Configures {@link KeywordTokenizer} with {@link ICUCollationAttributeFactory}.  *<p>  *   Converts the token into its {@link com.ibm.icu.text.CollationKey}, and  *   then encodes the CollationKey directly to allow it to  *   be stored as an index term.  *</p>  *<p>  *<strong>WARNING:</strong> Make sure you use exactly the same Collator at  *   index and query time -- CollationKeys are only comparable when produced by  *   the same Collator.  {@link com.ibm.icu.text.RuleBasedCollator}s are   *   independently versioned, so it is safe to search against stored  *   CollationKeys if the following are exactly the same (best practice is  *   to store this information with the index and check that they remain the  *   same at query time):  *</p>  *<ol>  *<li>  *     Collator version - see {@link Collator#getVersion()}  *</li>  *<li>  *     The collation strength used - see {@link Collator#setStrength(int)}  *</li>  *</ol>   *<p>  *   CollationKeys generated by ICU Collators are not compatible with those  *   generated by java.text.Collators.  Specifically, if you use   *   ICUCollationKeyAnalyzer to generate index terms, do not use   *   {@link CollationKeyAnalyzer} on the query side, or vice versa.  *</p>  *<p>  *   ICUCollationKeyAnalyzer is significantly faster and generates significantly  *   shorter keys than CollationKeyAnalyzer.  See  *<a href="http://site.icu-project.org/charts/collation-icu4j-sun"  *>http://site.icu-project.org/charts/collation-icu4j-sun</a> for key  *   generation timing and key length comparisons between ICU4J and  *   java.text.Collator over several languages.  *</p>  */
end_comment
begin_class
DECL|class|ICUCollationKeyAnalyzer
specifier|public
specifier|final
class|class
name|ICUCollationKeyAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|factory
specifier|private
specifier|final
name|ICUCollationAttributeFactory
name|factory
decl_stmt|;
comment|/**    * Create a new ICUCollationKeyAnalyzer, using the specified collator.    *    * @param collator CollationKey generator    */
DECL|method|ICUCollationKeyAnalyzer
specifier|public
name|ICUCollationKeyAnalyzer
parameter_list|(
name|Collator
name|collator
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
operator|new
name|ICUCollationAttributeFactory
argument_list|(
name|collator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|KeywordTokenizer
name|tokenizer
init|=
operator|new
name|KeywordTokenizer
argument_list|(
name|factory
argument_list|,
name|KeywordTokenizer
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
block|}
end_class
end_unit
