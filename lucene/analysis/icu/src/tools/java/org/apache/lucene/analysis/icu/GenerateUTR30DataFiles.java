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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|lang
operator|.
name|UCharacter
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
name|lang
operator|.
name|UProperty
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
name|UnicodeSetIterator
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
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileFilter
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
name|OutputStream
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
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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
name|Locale
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
begin_comment
comment|/**  * Downloads/generates lucene/analysis/icu/src/data/utr30/*.txt  *  * ASSUMPTION: This class will be run with current directory set to  * lucene/analysis/icu/src/data/utr30/  *  *<ol>  *<li>  *     Downloads nfc.txt, nfkc.txt and nfkc_cf.txt from icu-project.org,  *     overwriting the versions in lucene/analysis/icu/src/data/utr30/.  *</li>  *<li>  *     Converts round-trip mappings in nfc.txt (containing '=')  *     that map to at least one [:Diacritic:] character  *     into one-way mappings ('>' instead of '=').  *</li>  *</ol>  */
end_comment
begin_class
DECL|class|GenerateUTR30DataFiles
specifier|public
class|class
name|GenerateUTR30DataFiles
block|{
DECL|field|ICU_SVN_TAG_URL
specifier|private
specifier|static
specifier|final
name|String
name|ICU_SVN_TAG_URL
init|=
literal|"http://source.icu-project.org/repos/icu/icu/tags"
decl_stmt|;
DECL|field|ICU_RELEASE_TAG
specifier|private
specifier|static
specifier|final
name|String
name|ICU_RELEASE_TAG
init|=
literal|"release-52-1"
decl_stmt|;
DECL|field|ICU_DATA_NORM2_PATH
specifier|private
specifier|static
specifier|final
name|String
name|ICU_DATA_NORM2_PATH
init|=
literal|"source/data/unidata/norm2"
decl_stmt|;
DECL|field|NFC_TXT
specifier|private
specifier|static
specifier|final
name|String
name|NFC_TXT
init|=
literal|"nfc.txt"
decl_stmt|;
DECL|field|NFKC_TXT
specifier|private
specifier|static
specifier|final
name|String
name|NFKC_TXT
init|=
literal|"nfkc.txt"
decl_stmt|;
DECL|field|NFKC_CF_TXT
specifier|private
specifier|static
specifier|final
name|String
name|NFKC_CF_TXT
init|=
literal|"nfkc_cf.txt"
decl_stmt|;
DECL|field|DOWNLOAD_BUFFER
specifier|private
specifier|static
name|byte
index|[]
name|DOWNLOAD_BUFFER
init|=
operator|new
name|byte
index|[
literal|8192
index|]
decl_stmt|;
DECL|field|ROUND_TRIP_MAPPING_LINE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|ROUND_TRIP_MAPPING_LINE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*([^=]+?)\\s*=\\s*(.*)$"
argument_list|)
decl_stmt|;
DECL|field|VERBATIM_RULE_LINE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|VERBATIM_RULE_LINE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^#\\s*Rule:\\s*verbatim\\s*$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|RULE_LINE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|RULE_LINE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^#\\s*Rule:\\s*(.*)>(.*)"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|BLANK_OR_COMMENT_LINE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|BLANK_OR_COMMENT_LINE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^\\s*(?:#.*)?$"
argument_list|)
decl_stmt|;
DECL|field|NUMERIC_VALUE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|NUMERIC_VALUE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"Numeric[-\\s_]*Value"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
try|try
block|{
name|getNFKCDataFilesFromIcuProject
argument_list|()
expr_stmt|;
name|expandRulesInUTR30DataFiles
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|expandRulesInUTR30DataFiles
specifier|private
specifier|static
name|void
name|expandRulesInUTR30DataFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|FileFilter
name|filter
init|=
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
name|String
name|name
init|=
name|pathname
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|pathname
operator|.
name|isFile
argument_list|()
operator|&&
name|name
operator|.
name|matches
argument_list|(
literal|".*\\.(?s:txt)"
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|NFC_TXT
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|NFKC_TXT
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|NFKC_CF_TXT
argument_list|)
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|listFiles
argument_list|(
name|filter
argument_list|)
control|)
block|{
name|expandDataFileRules
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|expandDataFileRules
specifier|private
specifier|static
name|void
name|expandDataFileRules
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileInputStream
name|stream
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
specifier|final
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|bufferedReader
init|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
name|boolean
name|verbatim
init|=
literal|false
decl_stmt|;
name|boolean
name|modified
init|=
literal|false
decl_stmt|;
name|int
name|lineNum
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|null
operator|!=
operator|(
name|line
operator|=
name|bufferedReader
operator|.
name|readLine
argument_list|()
operator|)
condition|)
block|{
operator|++
name|lineNum
expr_stmt|;
if|if
condition|(
name|VERBATIM_RULE_LINE_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|verbatim
operator|=
literal|true
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Matcher
name|ruleMatcher
init|=
name|RULE_LINE_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|ruleMatcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|verbatim
operator|=
literal|false
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|leftHandSide
init|=
name|ruleMatcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|rightHandSide
init|=
name|ruleMatcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|expandSingleRule
argument_list|(
name|builder
argument_list|,
name|leftHandSide
argument_list|,
name|rightHandSide
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR in "
operator|+
name|file
operator|.
name|getName
argument_list|()
operator|+
literal|" line #"
operator|+
name|lineNum
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|modified
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|BLANK_OR_COMMENT_LINE_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|verbatim
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|line
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|modified
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
name|bufferedReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|modified
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Expanding rules in and overwriting "
operator|+
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|write
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|getNFKCDataFilesFromIcuProject
specifier|private
specifier|static
name|void
name|getNFKCDataFilesFromIcuProject
parameter_list|()
throws|throws
name|IOException
block|{
name|URL
name|icuTagsURL
init|=
operator|new
name|URL
argument_list|(
name|ICU_SVN_TAG_URL
operator|+
literal|"/"
argument_list|)
decl_stmt|;
name|URL
name|icuReleaseTagURL
init|=
operator|new
name|URL
argument_list|(
name|icuTagsURL
argument_list|,
name|ICU_RELEASE_TAG
operator|+
literal|"/"
argument_list|)
decl_stmt|;
name|URL
name|norm2url
init|=
operator|new
name|URL
argument_list|(
name|icuReleaseTagURL
argument_list|,
name|ICU_DATA_NORM2_PATH
operator|+
literal|"/"
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"Downloading "
operator|+
name|NFKC_TXT
operator|+
literal|" ... "
argument_list|)
expr_stmt|;
name|download
argument_list|(
operator|new
name|URL
argument_list|(
name|norm2url
argument_list|,
name|NFKC_TXT
argument_list|)
argument_list|,
name|NFKC_TXT
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"done."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"Downloading "
operator|+
name|NFKC_CF_TXT
operator|+
literal|" ... "
argument_list|)
expr_stmt|;
name|download
argument_list|(
operator|new
name|URL
argument_list|(
name|norm2url
argument_list|,
name|NFKC_CF_TXT
argument_list|)
argument_list|,
name|NFKC_CF_TXT
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"done."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"Downloading "
operator|+
name|NFKC_CF_TXT
operator|+
literal|" and making diacritic rules one-way ... "
argument_list|)
expr_stmt|;
name|URLConnection
name|connection
init|=
name|openConnection
argument_list|(
operator|new
name|URL
argument_list|(
name|norm2url
argument_list|,
name|NFC_TXT
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|NFC_TXT
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|line
decl_stmt|;
while|while
condition|(
literal|null
operator|!=
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
condition|)
block|{
name|Matcher
name|matcher
init|=
name|ROUND_TRIP_MAPPING_LINE_PATTERN
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
specifier|final
name|String
name|leftHandSide
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|rightHandSide
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|diacritics
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|outputCodePoint
range|:
name|rightHandSide
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
control|)
block|{
name|int
name|ch
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|outputCodePoint
argument_list|,
literal|16
argument_list|)
decl_stmt|;
if|if
condition|(
name|UCharacter
operator|.
name|hasBinaryProperty
argument_list|(
name|ch
argument_list|,
name|UProperty
operator|.
name|DIACRITIC
argument_list|)
comment|// gennorm2 fails if U+0653-U+0656 are included in round-trip mappings
operator|||
operator|(
name|ch
operator|>=
literal|0x653
operator|&&
name|ch
operator|<=
literal|0x656
operator|)
condition|)
block|{
name|diacritics
operator|.
name|add
argument_list|(
name|outputCodePoint
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|diacritics
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuilder
name|replacementLine
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|replacementLine
operator|.
name|append
argument_list|(
name|leftHandSide
argument_list|)
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
operator|.
name|append
argument_list|(
name|rightHandSide
argument_list|)
expr_stmt|;
name|replacementLine
operator|.
name|append
argument_list|(
literal|"  # one-way: diacritic"
argument_list|)
expr_stmt|;
if|if
condition|(
name|diacritics
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|replacementLine
operator|.
name|append
argument_list|(
literal|"s"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|diacritic
range|:
name|diacritics
control|)
block|{
name|replacementLine
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|diacritic
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|replacementLine
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|write
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"done."
argument_list|)
expr_stmt|;
block|}
DECL|method|download
specifier|private
specifier|static
name|void
name|download
parameter_list|(
name|URL
name|url
parameter_list|,
name|String
name|outputFile
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|URLConnection
name|connection
init|=
name|openConnection
argument_list|(
name|url
argument_list|)
decl_stmt|;
specifier|final
name|InputStream
name|inputStream
init|=
name|connection
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
specifier|final
name|OutputStream
name|outputStream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|outputFile
argument_list|)
decl_stmt|;
name|int
name|numBytes
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|-
literal|1
operator|!=
operator|(
name|numBytes
operator|=
name|inputStream
operator|.
name|read
argument_list|(
name|DOWNLOAD_BUFFER
argument_list|)
operator|)
condition|)
block|{
name|outputStream
operator|.
name|write
argument_list|(
name|DOWNLOAD_BUFFER
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|inputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|openConnection
specifier|private
specifier|static
name|URLConnection
name|openConnection
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setUseCaches
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|connection
operator|.
name|addRequestProperty
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
DECL|method|expandSingleRule
specifier|private
specifier|static
name|void
name|expandSingleRule
parameter_list|(
name|StringBuilder
name|builder
parameter_list|,
name|String
name|leftHandSide
parameter_list|,
name|String
name|rightHandSide
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|UnicodeSet
name|set
init|=
operator|new
name|UnicodeSet
argument_list|(
name|leftHandSide
argument_list|,
name|UnicodeSet
operator|.
name|IGNORE_SPACE
argument_list|)
decl_stmt|;
name|boolean
name|numericValue
init|=
name|NUMERIC_VALUE_PATTERN
operator|.
name|matcher
argument_list|(
name|rightHandSide
argument_list|)
operator|.
name|matches
argument_list|()
decl_stmt|;
for|for
control|(
name|UnicodeSetIterator
name|it
init|=
operator|new
name|UnicodeSetIterator
argument_list|(
name|set
argument_list|)
init|;
name|it
operator|.
name|nextRange
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|it
operator|.
name|codepoint
operator|!=
name|UnicodeSetIterator
operator|.
name|IS_STRING
condition|)
block|{
if|if
condition|(
name|numericValue
condition|)
block|{
for|for
control|(
name|int
name|cp
init|=
name|it
operator|.
name|codepoint
init|;
name|cp
operator|<=
name|it
operator|.
name|codepointEnd
condition|;
operator|++
name|cp
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%04X"
argument_list|,
name|cp
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%04X"
argument_list|,
literal|0x30
operator|+
name|UCharacter
operator|.
name|getNumericValue
argument_list|(
name|cp
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"   # "
argument_list|)
operator|.
name|append
argument_list|(
name|UCharacter
operator|.
name|getName
argument_list|(
name|cp
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%04X"
argument_list|,
name|it
operator|.
name|codepoint
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|it
operator|.
name|codepointEnd
operator|>
name|it
operator|.
name|codepoint
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|".."
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%04X"
argument_list|,
name|it
operator|.
name|codepointEnd
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
operator|.
name|append
argument_list|(
name|rightHandSide
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: String '"
operator|+
name|it
operator|.
name|getString
argument_list|()
operator|+
literal|"' found in UnicodeSet"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
