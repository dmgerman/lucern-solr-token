begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.br
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|br
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2004 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|LowerCaseFilter
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
name|de
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
name|analysis
operator|.
name|standard
operator|.
name|StandardFilter
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
name|standard
operator|.
name|StandardTokenizer
import|;
end_import
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
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import
begin_comment
comment|/**  * Analyzer for brazilian language. Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (word that will  * not be stemmed, but indexed).  *  * @author    João Kramer  */
end_comment
begin_class
DECL|class|BrazilianAnalyzer
specifier|public
specifier|final
class|class
name|BrazilianAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** 	 * List of typical german stopwords. 	 */
DECL|field|BRAZILIAN_STOP_WORDS
specifier|private
name|String
index|[]
name|BRAZILIAN_STOP_WORDS
init|=
block|{
literal|"a"
block|,
literal|"ainda"
block|,
literal|"alem"
block|,
literal|"ambas"
block|,
literal|"ambos"
block|,
literal|"antes"
block|,
literal|"ao"
block|,
literal|"aonde"
block|,
literal|"aos"
block|,
literal|"apos"
block|,
literal|"aquele"
block|,
literal|"aqueles"
block|,
literal|"as"
block|,
literal|"assim"
block|,
literal|"com"
block|,
literal|"como"
block|,
literal|"contra"
block|,
literal|"contudo"
block|,
literal|"cuja"
block|,
literal|"cujas"
block|,
literal|"cujo"
block|,
literal|"cujos"
block|,
literal|"da"
block|,
literal|"das"
block|,
literal|"de"
block|,
literal|"dela"
block|,
literal|"dele"
block|,
literal|"deles"
block|,
literal|"demais"
block|,
literal|"depois"
block|,
literal|"desde"
block|,
literal|"desta"
block|,
literal|"deste"
block|,
literal|"dispoe"
block|,
literal|"dispoem"
block|,
literal|"diversa"
block|,
literal|"diversas"
block|,
literal|"diversos"
block|,
literal|"do"
block|,
literal|"dos"
block|,
literal|"durante"
block|,
literal|"e"
block|,
literal|"ela"
block|,
literal|"elas"
block|,
literal|"ele"
block|,
literal|"eles"
block|,
literal|"em"
block|,
literal|"entao"
block|,
literal|"entre"
block|,
literal|"essa"
block|,
literal|"essas"
block|,
literal|"esse"
block|,
literal|"esses"
block|,
literal|"esta"
block|,
literal|"estas"
block|,
literal|"este"
block|,
literal|"estes"
block|,
literal|"ha"
block|,
literal|"isso"
block|,
literal|"isto"
block|,
literal|"logo"
block|,
literal|"mais"
block|,
literal|"mas"
block|,
literal|"mediante"
block|,
literal|"menos"
block|,
literal|"mesma"
block|,
literal|"mesmas"
block|,
literal|"mesmo"
block|,
literal|"mesmos"
block|,
literal|"na"
block|,
literal|"nas"
block|,
literal|"nao"
block|,
literal|"nas"
block|,
literal|"nem"
block|,
literal|"nesse"
block|,
literal|"neste"
block|,
literal|"nos"
block|,
literal|"o"
block|,
literal|"os"
block|,
literal|"ou"
block|,
literal|"outra"
block|,
literal|"outras"
block|,
literal|"outro"
block|,
literal|"outros"
block|,
literal|"pelas"
block|,
literal|"pelas"
block|,
literal|"pelo"
block|,
literal|"pelos"
block|,
literal|"perante"
block|,
literal|"pois"
block|,
literal|"por"
block|,
literal|"porque"
block|,
literal|"portanto"
block|,
literal|"proprio"
block|,
literal|"propios"
block|,
literal|"quais"
block|,
literal|"qual"
block|,
literal|"qualquer"
block|,
literal|"quando"
block|,
literal|"quanto"
block|,
literal|"que"
block|,
literal|"quem"
block|,
literal|"quer"
block|,
literal|"se"
block|,
literal|"seja"
block|,
literal|"sem"
block|,
literal|"sendo"
block|,
literal|"seu"
block|,
literal|"seus"
block|,
literal|"sob"
block|,
literal|"sobre"
block|,
literal|"sua"
block|,
literal|"suas"
block|,
literal|"tal"
block|,
literal|"tambem"
block|,
literal|"teu"
block|,
literal|"teus"
block|,
literal|"toda"
block|,
literal|"todas"
block|,
literal|"todo"
block|,
literal|"todos"
block|,
literal|"tua"
block|,
literal|"tuas"
block|,
literal|"tudo"
block|,
literal|"um"
block|,
literal|"uma"
block|,
literal|"umas"
block|,
literal|"uns"
block|}
decl_stmt|;
comment|/** 	 * Contains the stopwords used with the StopFilter. 	 */
DECL|field|stoptable
specifier|private
name|HashSet
name|stoptable
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/** 	 * Contains words that should be indexed but not stemmed. 	 */
DECL|field|excltable
specifier|private
name|HashSet
name|excltable
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
comment|/** 	 * Builds an analyzer. 	 */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
parameter_list|()
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|BRAZILIAN_STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
parameter_list|(
name|String
index|[]
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
parameter_list|(
name|Hashtable
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|(
name|stopwords
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|BrazilianAnalyzer
specifier|public
name|BrazilianAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
operator|new
name|HashSet
argument_list|(
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|stopwords
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from an array of Strings. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
index|[]
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|StopFilter
operator|.
name|makeStopSet
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from a Hashtable. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|Hashtable
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
operator|new
name|HashSet
argument_list|(
name|exclusionlist
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from the words contained in the given file. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
operator|new
name|HashSet
argument_list|(
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|exclusionlist
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates a TokenStream which tokenizes all the text in the provided Reader. 	 * 	 * @return  A TokenStream build from a StandardTokenizer filtered with 	 * 			StandardFilter, StopFilter, GermanStemFilter and LowerCaseFilter. 	 */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|BrazilianStemFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
comment|// Convert to lowercase after stemming!
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
