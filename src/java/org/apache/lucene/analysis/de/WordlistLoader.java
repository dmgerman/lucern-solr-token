begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
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
name|FileReader
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
name|LineNumberReader
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
begin_comment
comment|/**  * Loads a text file and adds every line as an entry to a Hashtable. Every line  * should contain only one word. If the file is not found or on any error, an  * empty table is returned.  *  * @author    Gerhard Schwarz  * @version   $Id$  */
end_comment
begin_class
DECL|class|WordlistLoader
specifier|public
class|class
name|WordlistLoader
block|{
comment|/**    * @param path      Path to the wordlist    * @param wordfile  Name of the wordlist    */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
name|getWordtable
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|wordfile
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
operator|||
name|wordfile
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Hashtable
argument_list|()
return|;
block|}
return|return
name|getWordtable
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|,
name|wordfile
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @param wordfile  Complete path to the wordlist    */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
name|getWordtable
parameter_list|(
name|String
name|wordfile
parameter_list|)
block|{
if|if
condition|(
name|wordfile
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Hashtable
argument_list|()
return|;
block|}
return|return
name|getWordtable
argument_list|(
operator|new
name|File
argument_list|(
name|wordfile
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @param wordfile  File containing the wordlist    */
DECL|method|getWordtable
specifier|public
specifier|static
name|Hashtable
name|getWordtable
parameter_list|(
name|File
name|wordfile
parameter_list|)
block|{
if|if
condition|(
name|wordfile
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Hashtable
argument_list|()
return|;
block|}
name|Hashtable
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LineNumberReader
name|lnr
init|=
operator|new
name|LineNumberReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|wordfile
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|word
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|stopwords
init|=
operator|new
name|String
index|[
literal|100
index|]
decl_stmt|;
name|int
name|wordcount
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|word
operator|=
name|lnr
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|wordcount
operator|++
expr_stmt|;
if|if
condition|(
name|wordcount
operator|==
name|stopwords
operator|.
name|length
condition|)
block|{
name|String
index|[]
name|tmp
init|=
operator|new
name|String
index|[
name|stopwords
operator|.
name|length
operator|+
literal|50
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|stopwords
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|wordcount
argument_list|)
expr_stmt|;
name|stopwords
operator|=
name|tmp
expr_stmt|;
block|}
name|stopwords
index|[
name|wordcount
operator|-
literal|1
index|]
operator|=
name|word
expr_stmt|;
block|}
name|result
operator|=
name|makeWordTable
argument_list|(
name|stopwords
argument_list|,
name|wordcount
argument_list|)
expr_stmt|;
block|}
comment|// On error, use an empty table
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|result
operator|=
operator|new
name|Hashtable
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Builds the wordlist table.    *    * @param words   Word that where read    * @param length  Amount of words that where read into<tt>words</tt>    */
DECL|method|makeWordTable
specifier|private
specifier|static
name|Hashtable
name|makeWordTable
parameter_list|(
name|String
index|[]
name|words
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|Hashtable
name|table
init|=
operator|new
name|Hashtable
argument_list|(
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|table
operator|.
name|put
argument_list|(
name|words
index|[
name|i
index|]
argument_list|,
name|words
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|table
return|;
block|}
block|}
end_class
end_unit
