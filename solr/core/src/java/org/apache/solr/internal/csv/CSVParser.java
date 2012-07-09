begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
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
name|InputStream
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
begin_comment
comment|/**  * Parses CSV files according to the specified configuration.  *  * Because CSV appears in many different dialects, the parser supports many  * configuration settings by allowing the specification of a {@link CSVStrategy}.  *   *<p>Parsing of a csv-string having tabs as separators,  * '"' as an optional value encapsulator, and comments starting with '#':</p>  *<pre>  *  String[][] data =   *   (new CSVParser(new StringReader("a\tb\nc\td"), new CSVStrategy('\t','"','#'))).getAllValues();  *</pre>  *   *<p>Parsing of a csv-string in Excel CSV format</p>  *<pre>  *  String[][] data =  *   (new CSVParser(new StringReader("a;b\nc;d"), CSVStrategy.EXCEL_STRATEGY)).getAllValues();  *</pre>  *   *<p>  * Internal parser state is completely covered by the strategy  * and the reader-state.</p>  *   *<p>see<a href="package-summary.html">package documentation</a>   * for more details</p>  */
end_comment
begin_class
DECL|class|CSVParser
specifier|public
class|class
name|CSVParser
block|{
comment|/** length of the initial token (content-)buffer */
DECL|field|INITIAL_TOKEN_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_TOKEN_LENGTH
init|=
literal|50
decl_stmt|;
comment|// the token types
comment|/** Token has no valid content, i.e. is in its initilized state. */
DECL|field|TT_INVALID
specifier|protected
specifier|static
specifier|final
name|int
name|TT_INVALID
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Token with content, at beginning or in the middle of a line. */
DECL|field|TT_TOKEN
specifier|protected
specifier|static
specifier|final
name|int
name|TT_TOKEN
init|=
literal|0
decl_stmt|;
comment|/** Token (which can have content) when end of file is reached. */
DECL|field|TT_EOF
specifier|protected
specifier|static
specifier|final
name|int
name|TT_EOF
init|=
literal|1
decl_stmt|;
comment|/** Token with content when end of a line is reached. */
DECL|field|TT_EORECORD
specifier|protected
specifier|static
specifier|final
name|int
name|TT_EORECORD
init|=
literal|2
decl_stmt|;
comment|/** Immutable empty String array. */
DECL|field|EMPTY_STRING_ARRAY
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|EMPTY_STRING_ARRAY
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
comment|// the input stream
DECL|field|in
specifier|private
specifier|final
name|ExtendedBufferedReader
name|in
decl_stmt|;
DECL|field|strategy
specifier|private
specifier|final
name|CSVStrategy
name|strategy
decl_stmt|;
comment|// the following objects are shared to reduce garbage
comment|/** A record buffer for getLine(). Grows as necessary and is reused. */
DECL|field|record
specifier|private
specifier|final
name|ArrayList
name|record
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|reusableToken
specifier|private
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
DECL|field|wsBuf
specifier|private
specifier|final
name|CharBuffer
name|wsBuf
init|=
operator|new
name|CharBuffer
argument_list|()
decl_stmt|;
DECL|field|code
specifier|private
specifier|final
name|CharBuffer
name|code
init|=
operator|new
name|CharBuffer
argument_list|(
literal|4
argument_list|)
decl_stmt|;
comment|/**    * Token is an internal token representation.    *     * It is used as contract between the lexer and the parser.     */
DECL|class|Token
specifier|static
class|class
name|Token
block|{
comment|/** Token type, see TT_xxx constants. */
DECL|field|type
name|int
name|type
init|=
name|TT_INVALID
decl_stmt|;
comment|/** The content buffer. */
DECL|field|content
name|CharBuffer
name|content
init|=
operator|new
name|CharBuffer
argument_list|(
name|INITIAL_TOKEN_LENGTH
argument_list|)
decl_stmt|;
comment|/** Token ready flag: indicates a valid token with content (ready for the parser). */
DECL|field|isReady
name|boolean
name|isReady
decl_stmt|;
DECL|method|reset
name|Token
name|reset
parameter_list|()
block|{
name|content
operator|.
name|clear
argument_list|()
expr_stmt|;
name|type
operator|=
name|TT_INVALID
expr_stmt|;
name|isReady
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
comment|// ======================================================
comment|//  the constructor
comment|// ======================================================
comment|/**    * CSV parser using the default {@link CSVStrategy}.    *     * @param input a Reader containing "csv-formatted" input    */
DECL|method|CSVParser
specifier|public
name|CSVParser
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
comment|// note: must match default-CSV-strategy !!
name|this
argument_list|(
name|input
argument_list|,
literal|','
argument_list|)
expr_stmt|;
block|}
comment|/**    * Customized value delimiter parser.    *     * The parser follows the default {@link CSVStrategy}    * except for the delimiter setting.    *     * @param input a Reader based on "csv-formatted" input    * @param delimiter a Char used for value separation    * @deprecated use {@link #CSVParser(Reader,CSVStrategy)}.    */
DECL|method|CSVParser
specifier|public
name|CSVParser
parameter_list|(
name|Reader
name|input
parameter_list|,
name|char
name|delimiter
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|delimiter
argument_list|,
literal|'"'
argument_list|,
name|CSVStrategy
operator|.
name|COMMENTS_DISABLED
argument_list|)
expr_stmt|;
block|}
comment|/**    * Customized csv parser.    *     * The parser parses according to the given CSV dialect settings.    * Leading whitespaces are truncated, unicode escapes are    * not interpreted and empty lines are ignored.    *     * @param input a Reader based on "csv-formatted" input    * @param delimiter a Char used for value separation    * @param encapsulator a Char used as value encapsulation marker    * @param commentStart a Char used for comment identification    * @deprecated use {@link #CSVParser(Reader,CSVStrategy)}.    */
DECL|method|CSVParser
specifier|public
name|CSVParser
parameter_list|(
name|Reader
name|input
parameter_list|,
name|char
name|delimiter
parameter_list|,
name|char
name|encapsulator
parameter_list|,
name|char
name|commentStart
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
operator|new
name|CSVStrategy
argument_list|(
name|delimiter
argument_list|,
name|encapsulator
argument_list|,
name|commentStart
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Customized CSV parser using the given {@link CSVStrategy}    *    * @param input a Reader containing "csv-formatted" input    * @param strategy the CSVStrategy used for CSV parsing    */
DECL|method|CSVParser
specifier|public
name|CSVParser
parameter_list|(
name|Reader
name|input
parameter_list|,
name|CSVStrategy
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
operator|new
name|ExtendedBufferedReader
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
block|}
comment|// ======================================================
comment|//  the parser
comment|// ======================================================
comment|/**    * Parses the CSV according to the given strategy    * and returns the content as an array of records    * (whereas records are arrays of single values).    *<p>    * The returned content starts at the current parse-position in    * the stream.    *     * @return matrix of records x values ('null' when end of file)    * @throws IOException on parse error or input read-failure    */
DECL|method|getAllValues
specifier|public
name|String
index|[]
index|[]
name|getAllValues
parameter_list|()
throws|throws
name|IOException
block|{
name|ArrayList
name|records
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|String
index|[]
name|values
decl_stmt|;
name|String
index|[]
index|[]
name|ret
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|values
operator|=
name|getLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|records
operator|.
name|add
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|records
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ret
operator|=
operator|new
name|String
index|[
name|records
operator|.
name|size
argument_list|()
index|]
index|[]
expr_stmt|;
name|records
operator|.
name|toArray
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Parses the CSV according to the given strategy    * and returns the next csv-value as string.    *     * @return next value in the input stream ('null' when end of file)    * @throws IOException on parse error or input read-failure    */
DECL|method|nextValue
specifier|public
name|String
name|nextValue
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|tkn
init|=
name|nextToken
argument_list|()
decl_stmt|;
name|String
name|ret
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|tkn
operator|.
name|type
condition|)
block|{
case|case
name|TT_TOKEN
case|:
case|case
name|TT_EORECORD
case|:
name|ret
operator|=
name|tkn
operator|.
name|content
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
case|case
name|TT_EOF
case|:
name|ret
operator|=
literal|null
expr_stmt|;
break|break;
case|case
name|TT_INVALID
case|:
default|default:
comment|// error no token available (or error)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"(line "
operator|+
name|getLineNumber
argument_list|()
operator|+
literal|") invalid parse sequence"
argument_list|)
throw|;
comment|// unreachable: break;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Parses from the current point in the stream til    * the end of the current line.    *     * @return array of values til end of line     *        ('null' when end of file has been reached)    * @throws IOException on parse error or input read-failure    */
DECL|method|getLine
specifier|public
name|String
index|[]
name|getLine
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|ret
init|=
name|EMPTY_STRING_ARRAY
decl_stmt|;
name|record
operator|.
name|clear
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|reusableToken
operator|.
name|reset
argument_list|()
expr_stmt|;
name|nextToken
argument_list|(
name|reusableToken
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|reusableToken
operator|.
name|type
condition|)
block|{
case|case
name|TT_TOKEN
case|:
name|record
operator|.
name|add
argument_list|(
name|reusableToken
operator|.
name|content
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|TT_EORECORD
case|:
name|record
operator|.
name|add
argument_list|(
name|reusableToken
operator|.
name|content
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|TT_EOF
case|:
if|if
condition|(
name|reusableToken
operator|.
name|isReady
condition|)
block|{
name|record
operator|.
name|add
argument_list|(
name|reusableToken
operator|.
name|content
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
literal|null
expr_stmt|;
block|}
break|break;
case|case
name|TT_INVALID
case|:
default|default:
comment|// error: throw IOException
throw|throw
operator|new
name|IOException
argument_list|(
literal|"(line "
operator|+
name|getLineNumber
argument_list|()
operator|+
literal|") invalid parse sequence"
argument_list|)
throw|;
comment|// unreachable: break;
block|}
if|if
condition|(
name|reusableToken
operator|.
name|type
operator|!=
name|TT_TOKEN
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|record
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ret
operator|=
operator|(
name|String
index|[]
operator|)
name|record
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|record
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Returns the current line number in the input stream.    *     * ATTENTION: in case your csv has multiline-values the returned    *            number does not correspond to the record-number    *     * @return  current line number    */
DECL|method|getLineNumber
specifier|public
name|int
name|getLineNumber
parameter_list|()
block|{
return|return
name|in
operator|.
name|getLineNumber
argument_list|()
return|;
block|}
comment|// ======================================================
comment|//  the lexer(s)
comment|// ======================================================
comment|/**    * Convenience method for<code>nextToken(null)</code>.    */
DECL|method|nextToken
specifier|protected
name|Token
name|nextToken
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|nextToken
argument_list|(
operator|new
name|Token
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the next token.    *     * A token corresponds to a term, a record change or an    * end-of-file indicator.    *     * @param tkn an existing Token object to reuse. The caller is responsible to initialize the    * Token.    * @return the next token found    * @throws IOException on stream access error    */
DECL|method|nextToken
specifier|protected
name|Token
name|nextToken
parameter_list|(
name|Token
name|tkn
parameter_list|)
throws|throws
name|IOException
block|{
name|wsBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// resuse
comment|// get the last read char (required for empty line detection)
name|int
name|lastChar
init|=
name|in
operator|.
name|readAgain
argument_list|()
decl_stmt|;
comment|//  read the next char and set eol
comment|/* note: unfourtunately isEndOfLine may consumes a character silently.      *       this has no effect outside of the method. so a simple workaround      *       is to call 'readAgain' on the stream...      *       uh: might using objects instead of base-types (jdk1.5 autoboxing!)      */
name|int
name|c
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
name|boolean
name|eol
init|=
name|isEndOfLine
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|c
operator|=
name|in
operator|.
name|readAgain
argument_list|()
expr_stmt|;
comment|//  empty line detection: eol AND (last char was EOL or beginning)
while|while
condition|(
name|strategy
operator|.
name|getIgnoreEmptyLines
argument_list|()
operator|&&
name|eol
operator|&&
operator|(
name|lastChar
operator|==
literal|'\n'
operator|||
name|lastChar
operator|==
name|ExtendedBufferedReader
operator|.
name|UNDEFINED
operator|)
operator|&&
operator|!
name|isEndOfFile
argument_list|(
name|lastChar
argument_list|)
condition|)
block|{
comment|// go on char ahead ...
name|lastChar
operator|=
name|c
expr_stmt|;
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|eol
operator|=
name|isEndOfLine
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
name|in
operator|.
name|readAgain
argument_list|()
expr_stmt|;
comment|// reached end of file without any content (empty line at the end)
if|if
condition|(
name|isEndOfFile
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|tkn
operator|.
name|type
operator|=
name|TT_EOF
expr_stmt|;
return|return
name|tkn
return|;
block|}
block|}
comment|// did we reached eof during the last iteration already ? TT_EOF
if|if
condition|(
name|isEndOfFile
argument_list|(
name|lastChar
argument_list|)
operator|||
operator|(
name|lastChar
operator|!=
name|strategy
operator|.
name|getDelimiter
argument_list|()
operator|&&
name|isEndOfFile
argument_list|(
name|c
argument_list|)
operator|)
condition|)
block|{
name|tkn
operator|.
name|type
operator|=
name|TT_EOF
expr_stmt|;
return|return
name|tkn
return|;
block|}
comment|//  important: make sure a new char gets consumed in each iteration
while|while
condition|(
operator|!
name|tkn
operator|.
name|isReady
operator|&&
name|tkn
operator|.
name|type
operator|!=
name|TT_EOF
condition|)
block|{
comment|// ignore whitespaces at beginning of a token
while|while
condition|(
name|strategy
operator|.
name|getIgnoreLeadingWhitespaces
argument_list|()
operator|&&
name|isWhitespace
argument_list|(
name|c
argument_list|)
operator|&&
operator|!
name|eol
condition|)
block|{
name|wsBuf
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|eol
operator|=
name|isEndOfLine
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
comment|// ok, start of token reached: comment, encapsulated, or token
if|if
condition|(
name|c
operator|==
name|strategy
operator|.
name|getCommentStart
argument_list|()
condition|)
block|{
comment|// ignore everything till end of line and continue (incr linecount)
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|tkn
operator|=
name|nextToken
argument_list|(
name|tkn
operator|.
name|reset
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
name|strategy
operator|.
name|getDelimiter
argument_list|()
condition|)
block|{
comment|// empty token return TT_TOKEN("")
name|tkn
operator|.
name|type
operator|=
name|TT_TOKEN
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|eol
condition|)
block|{
comment|// empty token return TT_EORECORD("")
comment|//noop: tkn.content.append("");
name|tkn
operator|.
name|type
operator|=
name|TT_EORECORD
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
name|strategy
operator|.
name|getEncapsulator
argument_list|()
condition|)
block|{
comment|// consume encapsulated token
name|encapsulatedTokenLexer
argument_list|(
name|tkn
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isEndOfFile
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// end of file return TT_EOF()
comment|//noop: tkn.content.append("");
name|tkn
operator|.
name|type
operator|=
name|TT_EOF
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// next token must be a simple token
comment|// add removed blanks when not ignoring whitespace chars...
if|if
condition|(
operator|!
name|strategy
operator|.
name|getIgnoreLeadingWhitespaces
argument_list|()
condition|)
block|{
name|tkn
operator|.
name|content
operator|.
name|append
argument_list|(
name|wsBuf
argument_list|)
expr_stmt|;
block|}
name|simpleTokenLexer
argument_list|(
name|tkn
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tkn
return|;
block|}
comment|/**    * A simple token lexer    *     * Simple token are tokens which are not surrounded by encapsulators.    * A simple token might contain escaped delimiters (as \, or \;). The    * token is finished when one of the following conditions become true:    *<ul>    *<li>end of line has been reached (TT_EORECORD)</li>    *<li>end of stream has been reached (TT_EOF)</li>    *<li>an unescaped delimiter has been reached (TT_TOKEN)</li>    *</ul>    *      * @param tkn  the current token    * @param c    the current character    * @return the filled token    *     * @throws IOException on stream access error    */
DECL|method|simpleTokenLexer
specifier|private
name|Token
name|simpleTokenLexer
parameter_list|(
name|Token
name|tkn
parameter_list|,
name|int
name|c
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|isEndOfLine
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// end of record
name|tkn
operator|.
name|type
operator|=
name|TT_EORECORD
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|isEndOfFile
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// end of file
name|tkn
operator|.
name|type
operator|=
name|TT_EOF
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
name|strategy
operator|.
name|getDelimiter
argument_list|()
condition|)
block|{
comment|// end of token
name|tkn
operator|.
name|type
operator|=
name|TT_TOKEN
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'\\'
operator|&&
name|strategy
operator|.
name|getUnicodeEscapeInterpretation
argument_list|()
operator|&&
name|in
operator|.
name|lookAhead
argument_list|()
operator|==
literal|'u'
condition|)
block|{
comment|// interpret unicode escaped chars (like \u0070 -> p)
name|tkn
operator|.
name|content
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|unicodeEscapeLexer
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
name|strategy
operator|.
name|getEscape
argument_list|()
condition|)
block|{
name|tkn
operator|.
name|content
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|readEscape
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tkn
operator|.
name|content
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|strategy
operator|.
name|getIgnoreTrailingWhitespaces
argument_list|()
condition|)
block|{
name|tkn
operator|.
name|content
operator|.
name|trimTrailingWhitespace
argument_list|()
expr_stmt|;
block|}
return|return
name|tkn
return|;
block|}
comment|/**    * An encapsulated token lexer    *     * Encapsulated tokens are surrounded by the given encapsulating-string.    * The encapsulator itself might be included in the token using a    * doubling syntax (as "", '') or using escaping (as in \", \').    * Whitespaces before and after an encapsulated token are ignored.    *     * @param tkn    the current token    * @param c      the current character    * @return a valid token object    * @throws IOException on invalid state    */
DECL|method|encapsulatedTokenLexer
specifier|private
name|Token
name|encapsulatedTokenLexer
parameter_list|(
name|Token
name|tkn
parameter_list|,
name|int
name|c
parameter_list|)
throws|throws
name|IOException
block|{
comment|// save current line
name|int
name|startLineNumber
init|=
name|getLineNumber
argument_list|()
decl_stmt|;
comment|// ignore the given delimiter
comment|// assert c == delimiter;
for|for
control|(
init|;
condition|;
control|)
block|{
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
operator|&&
name|strategy
operator|.
name|getUnicodeEscapeInterpretation
argument_list|()
operator|&&
name|in
operator|.
name|lookAhead
argument_list|()
operator|==
literal|'u'
condition|)
block|{
name|tkn
operator|.
name|content
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|unicodeEscapeLexer
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
name|strategy
operator|.
name|getEscape
argument_list|()
condition|)
block|{
name|tkn
operator|.
name|content
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|readEscape
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
name|strategy
operator|.
name|getEncapsulator
argument_list|()
condition|)
block|{
if|if
condition|(
name|in
operator|.
name|lookAhead
argument_list|()
operator|==
name|strategy
operator|.
name|getEncapsulator
argument_list|()
condition|)
block|{
comment|// double or escaped encapsulator -> add single encapsulator to token
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|tkn
operator|.
name|content
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// token finish mark (encapsulator) reached: ignore whitespace till delimiter
for|for
control|(
init|;
condition|;
control|)
block|{
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|c
operator|==
name|strategy
operator|.
name|getDelimiter
argument_list|()
condition|)
block|{
name|tkn
operator|.
name|type
operator|=
name|TT_TOKEN
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
return|return
name|tkn
return|;
block|}
elseif|else
if|if
condition|(
name|isEndOfFile
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|tkn
operator|.
name|type
operator|=
name|TT_EOF
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
return|return
name|tkn
return|;
block|}
elseif|else
if|if
condition|(
name|isEndOfLine
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// ok eo token reached
name|tkn
operator|.
name|type
operator|=
name|TT_EORECORD
expr_stmt|;
name|tkn
operator|.
name|isReady
operator|=
literal|true
expr_stmt|;
return|return
name|tkn
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|isWhitespace
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// error invalid char between token and next delimiter
throw|throw
operator|new
name|IOException
argument_list|(
literal|"(line "
operator|+
name|getLineNumber
argument_list|()
operator|+
literal|") invalid char between encapsulated token end delimiter"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|isEndOfFile
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// error condition (end of file before end of token)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"(startline "
operator|+
name|startLineNumber
operator|+
literal|")"
operator|+
literal|"eof reached before encapsulated token finished"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// consume character
name|tkn
operator|.
name|content
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Decodes Unicode escapes.    *     * Interpretation of "\\uXXXX" escape sequences    * where XXXX is a hex-number.    * @param c current char which is discarded because it's the "\\" of "\\uXXXX"    * @return the decoded character    * @throws IOException on wrong unicode escape sequence or read error    */
DECL|method|unicodeEscapeLexer
specifier|protected
name|int
name|unicodeEscapeLexer
parameter_list|(
name|int
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
comment|// ignore 'u' (assume c==\ now) and read 4 hex digits
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|code
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|isEndOfFile
argument_list|(
name|c
argument_list|)
operator|||
name|isEndOfLine
argument_list|(
name|c
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"number too short"
argument_list|)
throw|;
block|}
name|code
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
name|ret
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|code
operator|.
name|toString
argument_list|()
argument_list|,
literal|16
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"(line "
operator|+
name|getLineNumber
argument_list|()
operator|+
literal|") Wrong unicode escape sequence found '"
operator|+
name|code
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|readEscape
specifier|private
name|int
name|readEscape
parameter_list|(
name|int
name|c
parameter_list|)
throws|throws
name|IOException
block|{
comment|// assume c is the escape char (normally a backslash)
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|int
name|out
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'r'
case|:
name|out
operator|=
literal|'\r'
expr_stmt|;
break|break;
case|case
literal|'n'
case|:
name|out
operator|=
literal|'\n'
expr_stmt|;
break|break;
case|case
literal|'t'
case|:
name|out
operator|=
literal|'\t'
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|out
operator|=
literal|'\b'
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|out
operator|=
literal|'\f'
expr_stmt|;
break|break;
default|default :
name|out
operator|=
name|c
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
comment|// ======================================================
comment|//  strategies
comment|// ======================================================
comment|/**    * Obtain the specified CSV Strategy.  This should not be modified.    *     * @return strategy currently being used    */
DECL|method|getStrategy
specifier|public
name|CSVStrategy
name|getStrategy
parameter_list|()
block|{
return|return
name|this
operator|.
name|strategy
return|;
block|}
comment|// ======================================================
comment|//  Character class checker
comment|// ======================================================
comment|/**    * @return true if the given char is a whitespace character    */
DECL|method|isWhitespace
specifier|private
name|boolean
name|isWhitespace
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|isWhitespace
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
operator|&&
operator|(
name|c
operator|!=
name|strategy
operator|.
name|getDelimiter
argument_list|()
operator|)
return|;
block|}
comment|/**    * Greedy - accepts \n and \r\n     * This checker consumes silently the second control-character...    *     * @return true if the given character is a line-terminator    */
DECL|method|isEndOfLine
specifier|private
name|boolean
name|isEndOfLine
parameter_list|(
name|int
name|c
parameter_list|)
throws|throws
name|IOException
block|{
comment|// check if we have \r\n...
if|if
condition|(
name|c
operator|==
literal|'\r'
condition|)
block|{
if|if
condition|(
name|in
operator|.
name|lookAhead
argument_list|()
operator|==
literal|'\n'
condition|)
block|{
comment|// note: does not change c outside of this method !!
name|c
operator|=
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
block|}
return|return
operator|(
name|c
operator|==
literal|'\n'
operator|)
return|;
block|}
comment|/**    * @return true if the given character indicates end of file    */
DECL|method|isEndOfFile
specifier|private
name|boolean
name|isEndOfFile
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|c
operator|==
name|ExtendedBufferedReader
operator|.
name|END_OF_STREAM
return|;
block|}
block|}
end_class
end_unit
