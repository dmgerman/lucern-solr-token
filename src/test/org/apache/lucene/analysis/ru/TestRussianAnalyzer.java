begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
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
name|io
operator|.
name|BufferedReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|UnsupportedEncodingException
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
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import
begin_comment
comment|/**  * Test case for RussianAnalyzer.  *  * @author    Boris Okner  * @version   $Id$  */
end_comment
begin_class
DECL|class|TestRussianAnalyzer
specifier|public
class|class
name|TestRussianAnalyzer
extends|extends
name|TestCase
block|{
DECL|field|inWords
specifier|private
name|InputStreamReader
name|inWords
decl_stmt|;
DECL|field|sampleUnicode
specifier|private
name|InputStreamReader
name|sampleUnicode
decl_stmt|;
DECL|field|inWordsKOI8
specifier|private
name|FileReader
name|inWordsKOI8
decl_stmt|;
DECL|field|sampleKOI8
specifier|private
name|FileReader
name|sampleKOI8
decl_stmt|;
DECL|field|inWords1251
specifier|private
name|FileReader
name|inWords1251
decl_stmt|;
DECL|field|sample1251
specifier|private
name|FileReader
name|sample1251
decl_stmt|;
DECL|method|TestRussianAnalyzer
specifier|public
name|TestRussianAnalyzer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see TestCase#setUp()      */
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see TestCase#tearDown()      */
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testUnicode
specifier|public
name|void
name|testUnicode
parameter_list|()
throws|throws
name|IOException
block|{
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|RussianCharsets
operator|.
name|UnicodeRussian
argument_list|)
decl_stmt|;
name|inWords
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
literal|"src/test/org/apache/lucene/analysis/ru/testUnicode.txt"
argument_list|)
argument_list|,
literal|"Unicode"
argument_list|)
expr_stmt|;
name|sampleUnicode
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
literal|"src/test/org/apache/lucene/analysis/ru/resUnicode.htm"
argument_list|)
argument_list|,
literal|"Unicode"
argument_list|)
expr_stmt|;
name|TokenStream
name|in
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|"all"
argument_list|,
name|inWords
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|sample
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|sampleUnicode
argument_list|,
name|RussianCharsets
operator|.
name|UnicodeRussian
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Token
name|token
init|=
name|in
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|Token
name|sampleToken
init|=
name|sample
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unicode"
argument_list|,
name|token
operator|.
name|termText
argument_list|()
argument_list|,
name|sampleToken
operator|==
literal|null
condition|?
literal|null
else|:
name|sampleToken
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inWords
operator|.
name|close
argument_list|()
expr_stmt|;
name|sampleUnicode
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testKOI8
specifier|public
name|void
name|testKOI8
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println(new java.util.Date());
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|RussianCharsets
operator|.
name|KOI8
argument_list|)
decl_stmt|;
comment|// KOI8
name|inWordsKOI8
operator|=
operator|new
name|FileReader
argument_list|(
literal|"src/test/org/apache/lucene/analysis/ru/testKOI8.txt"
argument_list|)
expr_stmt|;
name|sampleKOI8
operator|=
operator|new
name|FileReader
argument_list|(
literal|"src/test/org/apache/lucene/analysis/ru/resKOI8.htm"
argument_list|)
expr_stmt|;
name|TokenStream
name|in
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|"all"
argument_list|,
name|inWordsKOI8
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|sample
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|sampleKOI8
argument_list|,
name|RussianCharsets
operator|.
name|KOI8
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Token
name|token
init|=
name|in
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|Token
name|sampleToken
init|=
name|sample
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"KOI8"
argument_list|,
name|token
operator|.
name|termText
argument_list|()
argument_list|,
name|sampleToken
operator|==
literal|null
condition|?
literal|null
else|:
name|sampleToken
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inWordsKOI8
operator|.
name|close
argument_list|()
expr_stmt|;
name|sampleKOI8
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|test1251
specifier|public
name|void
name|test1251
parameter_list|()
throws|throws
name|IOException
block|{
comment|// 1251
name|inWords1251
operator|=
operator|new
name|FileReader
argument_list|(
literal|"src/test/org/apache/lucene/analysis/ru/test1251.txt"
argument_list|)
expr_stmt|;
name|sample1251
operator|=
operator|new
name|FileReader
argument_list|(
literal|"src/test/org/apache/lucene/analysis/ru/res1251.htm"
argument_list|)
expr_stmt|;
name|RussianAnalyzer
name|ra
init|=
operator|new
name|RussianAnalyzer
argument_list|(
name|RussianCharsets
operator|.
name|CP1251
argument_list|)
decl_stmt|;
name|TokenStream
name|in
init|=
name|ra
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
name|inWords1251
argument_list|)
decl_stmt|;
name|RussianLetterTokenizer
name|sample
init|=
operator|new
name|RussianLetterTokenizer
argument_list|(
name|sample1251
argument_list|,
name|RussianCharsets
operator|.
name|CP1251
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Token
name|token
init|=
name|in
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|Token
name|sampleToken
init|=
name|sample
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1251"
argument_list|,
name|token
operator|.
name|termText
argument_list|()
argument_list|,
name|sampleToken
operator|==
literal|null
condition|?
literal|null
else|:
name|sampleToken
operator|.
name|termText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inWords1251
operator|.
name|close
argument_list|()
expr_stmt|;
name|sample1251
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
