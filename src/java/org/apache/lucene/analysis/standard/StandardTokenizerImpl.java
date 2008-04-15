begin_unit
begin_comment
comment|/* The following code was generated by JFlex 1.4.1 on 4/15/08 4:31 AM */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Token
import|;
end_import
begin_comment
comment|/**  * This class is a scanner generated by   *<a href="http://www.jflex.de/">JFlex</a> 1.4.1  * on 4/15/08 4:31 AM from the specification file  *<tt>/mnt2/mike/src/lucene.clean/src/java/org/apache/lucene/analysis/standard/StandardTokenizerImpl.jflex</tt>  */
end_comment
begin_class
DECL|class|StandardTokenizerImpl
class|class
name|StandardTokenizerImpl
block|{
comment|/** This character denotes the end of file */
DECL|field|YYEOF
specifier|public
specifier|static
specifier|final
name|int
name|YYEOF
init|=
operator|-
literal|1
decl_stmt|;
comment|/** initial size of the lookahead buffer */
DECL|field|ZZ_BUFFERSIZE
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_BUFFERSIZE
init|=
literal|16384
decl_stmt|;
comment|/** lexical states */
DECL|field|YYINITIAL
specifier|public
specifier|static
specifier|final
name|int
name|YYINITIAL
init|=
literal|0
decl_stmt|;
comment|/**     * Translates characters to character classes    */
DECL|field|ZZ_CMAP_PACKED
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_CMAP_PACKED
init|=
literal|"\11\0\1\0\1\16\1\0\1\0\1\15\22\0\1\0\5\0\1\3"
operator|+
literal|"\1\1\4\0\1\7\1\5\1\2\1\7\12\11\6\0\1\4\32\10"
operator|+
literal|"\4\0\1\6\1\0\32\10\105\0\27\10\1\0\37\10\1\0\u0568\10"
operator|+
literal|"\12\12\206\10\12\12\u026c\10\12\12\166\10\12\12\166\10\12\12\166\10"
operator|+
literal|"\12\12\166\10\12\12\167\10\11\12\166\10\12\12\166\10\12\12\166\10"
operator|+
literal|"\12\12\340\10\12\12\166\10\12\12\u0166\10\12\12\266\10\u0100\10\u0e00\10"
operator|+
literal|"\u1040\0\u0150\14\140\0\20\14\u0100\0\200\14\200\0\u19c0\14\100\0\u5200\14"
operator|+
literal|"\u0c00\0\u2bb0\13\u2150\0\u0200\14\u0465\0\73\14\75\10\43\0"
decl_stmt|;
comment|/**     * Translates characters to character classes    */
DECL|field|ZZ_CMAP
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|ZZ_CMAP
init|=
name|zzUnpackCMap
argument_list|(
name|ZZ_CMAP_PACKED
argument_list|)
decl_stmt|;
comment|/**     * Translates DFA states to action switch labels.    */
DECL|field|ZZ_ACTION
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ZZ_ACTION
init|=
name|zzUnpackAction
argument_list|()
decl_stmt|;
DECL|field|ZZ_ACTION_PACKED_0
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_ACTION_PACKED_0
init|=
literal|"\1\0\1\1\4\2\1\3\1\1\6\0\2\2\6\0"
operator|+
literal|"\1\4\4\5\2\6\2\0\1\7\1\0\1\7\3\5"
operator|+
literal|"\6\7\3\5\1\10\1\0\1\11\2\0\1\10\1\11"
operator|+
literal|"\1\0\2\11\2\10\2\5\1\12"
decl_stmt|;
DECL|method|zzUnpackAction
specifier|private
specifier|static
name|int
index|[]
name|zzUnpackAction
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|61
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|zzUnpackAction
argument_list|(
name|ZZ_ACTION_PACKED_0
argument_list|,
name|offset
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|zzUnpackAction
specifier|private
specifier|static
name|int
name|zzUnpackAction
parameter_list|(
name|String
name|packed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|result
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
name|offset
decl_stmt|;
comment|/* index in unpacked array */
name|int
name|l
init|=
name|packed
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|l
condition|)
block|{
name|int
name|count
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|int
name|value
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
do|do
name|result
index|[
name|j
operator|++
index|]
operator|=
name|value
expr_stmt|;
do|while
condition|(
operator|--
name|count
operator|>
literal|0
condition|)
do|;
block|}
return|return
name|j
return|;
block|}
comment|/**     * Translates a state to a row index in the transition table    */
DECL|field|ZZ_ROWMAP
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ZZ_ROWMAP
init|=
name|zzUnpackRowMap
argument_list|()
decl_stmt|;
DECL|field|ZZ_ROWMAP_PACKED_0
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_ROWMAP_PACKED_0
init|=
literal|"\0\0\0\17\0\36\0\55\0\74\0\113\0\17\0\132"
operator|+
literal|"\0\151\0\170\0\207\0\226\0\245\0\264\0\303\0\322"
operator|+
literal|"\0\341\0\360\0\377\0\u010e\0\u011d\0\u012c\0\u013b\0\u014a"
operator|+
literal|"\0\u0159\0\u0168\0\u0177\0\207\0\u0186\0\u0195\0\u01a4\0\u01b3"
operator|+
literal|"\0\u01c2\0\u01d1\0\u01e0\0\u01ef\0\u01fe\0\u020d\0\u021c\0\u022b"
operator|+
literal|"\0\u023a\0\u0249\0\u0258\0\u0267\0\u0276\0\u0285\0\u0294\0\u02a3"
operator|+
literal|"\0\u02b2\0\u02c1\0\u02d0\0\u02df\0\u02ee\0\u02fd\0\u012c\0\341"
operator|+
literal|"\0\170\0\u011d\0\u030c\0\u031b\0\u032a"
decl_stmt|;
DECL|method|zzUnpackRowMap
specifier|private
specifier|static
name|int
index|[]
name|zzUnpackRowMap
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|61
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|zzUnpackRowMap
argument_list|(
name|ZZ_ROWMAP_PACKED_0
argument_list|,
name|offset
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|zzUnpackRowMap
specifier|private
specifier|static
name|int
name|zzUnpackRowMap
parameter_list|(
name|String
name|packed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|result
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
name|offset
decl_stmt|;
comment|/* index in unpacked array */
name|int
name|l
init|=
name|packed
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|l
condition|)
block|{
name|int
name|high
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
operator|<<
literal|16
decl_stmt|;
name|result
index|[
name|j
operator|++
index|]
operator|=
name|high
operator||
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
expr_stmt|;
block|}
return|return
name|j
return|;
block|}
comment|/**     * The transition table of the DFA    */
DECL|field|ZZ_TRANS
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ZZ_TRANS
init|=
name|zzUnpackTrans
argument_list|()
decl_stmt|;
DECL|field|ZZ_TRANS_PACKED_0
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_TRANS_PACKED_0
init|=
literal|"\10\2\1\3\1\4\1\5\1\6\1\7\1\10\1\2"
operator|+
literal|"\20\0\1\11\1\12\1\13\1\14\2\15\1\16\1\17"
operator|+
literal|"\1\4\1\20\1\6\5\0\1\21\1\0\1\22\2\23"
operator|+
literal|"\1\24\3\4\1\6\4\0\1\11\1\25\1\13\1\14"
operator|+
literal|"\2\23\1\24\1\20\1\4\1\20\1\6\5\0\1\26"
operator|+
literal|"\1\0\1\22\2\15\1\16\4\6\21\0\1\2\10\0"
operator|+
literal|"\1\27\1\0\1\27\14\0\1\30\1\31\1\32\1\33"
operator|+
literal|"\13\0\1\34\1\0\1\34\14\0\1\35\1\36\1\35"
operator|+
literal|"\1\36\13\0\1\37\2\40\1\41\13\0\1\16\2\42"
operator|+
literal|"\5\0\1\11\1\26\1\13\1\14\2\15\1\16\1\17"
operator|+
literal|"\1\4\1\20\1\6\4\0\1\11\1\21\1\13\1\14"
operator|+
literal|"\2\23\1\24\1\20\1\4\1\20\1\6\13\0\1\43"
operator|+
literal|"\2\44\1\45\13\0\4\36\13\0\1\46\2\47\1\50"
operator|+
literal|"\13\0\1\51\2\52\1\53\13\0\1\54\1\44\1\55"
operator|+
literal|"\1\45\13\0\1\56\2\31\1\33\4\0\1\11\6\0"
operator|+
literal|"\1\27\1\0\1\27\6\0\1\57\1\0\1\22\2\60"
operator|+
literal|"\1\0\1\56\2\31\1\33\5\0\1\61\1\0\1\22"
operator|+
literal|"\2\62\1\63\3\31\1\33\5\0\1\64\1\0\1\22"
operator|+
literal|"\2\62\1\63\3\31\1\33\5\0\1\65\1\0\1\22"
operator|+
literal|"\2\60\1\0\4\33\5\0\1\66\2\0\1\66\2\0"
operator|+
literal|"\1\35\1\36\1\35\1\36\5\0\1\66\2\0\1\66"
operator|+
literal|"\2\0\4\36\5\0\1\60\1\0\1\22\2\60\1\0"
operator|+
literal|"\1\37\2\40\1\41\5\0\1\62\1\0\1\22\2\62"
operator|+
literal|"\1\63\3\40\1\41\5\0\1\60\1\0\1\22\2\60"
operator|+
literal|"\1\0\4\41\5\0\1\63\2\0\3\63\3\42\6\0"
operator|+
literal|"\1\67\1\0\1\22\2\15\1\16\1\43\2\44\1\45"
operator|+
literal|"\5\0\1\70\1\0\1\22\2\23\1\24\3\44\1\45"
operator|+
literal|"\5\0\1\67\1\0\1\22\2\15\1\16\4\45\5\0"
operator|+
literal|"\1\15\1\0\1\22\2\15\1\16\1\46\2\47\1\50"
operator|+
literal|"\5\0\1\23\1\0\1\22\2\23\1\24\3\47\1\50"
operator|+
literal|"\5\0\1\15\1\0\1\22\2\15\1\16\4\50\5\0"
operator|+
literal|"\1\16\2\0\3\16\1\51\2\52\1\53\5\0\1\24"
operator|+
literal|"\2\0\3\24\3\52\1\53\5\0\1\16\2\0\3\16"
operator|+
literal|"\4\53\5\0\1\71\1\0\1\22\2\15\1\16\1\43"
operator|+
literal|"\2\44\1\45\5\0\1\72\1\0\1\22\2\23\1\24"
operator|+
literal|"\3\44\1\45\5\0\1\65\1\0\1\22\2\60\1\0"
operator|+
literal|"\1\56\2\31\1\33\13\0\1\73\1\33\1\73\1\33"
operator|+
literal|"\13\0\4\41\13\0\4\45\13\0\4\50\13\0\4\53"
operator|+
literal|"\13\0\1\74\1\45\1\74\1\45\13\0\4\33\13\0"
operator|+
literal|"\4\75\5\0\1\57\1\0\1\22\2\60\1\0\4\33"
operator|+
literal|"\5\0\1\71\1\0\1\22\2\15\1\16\4\45\5\0"
operator|+
literal|"\1\66\2\0\1\66\2\0\4\75\3\0"
decl_stmt|;
DECL|method|zzUnpackTrans
specifier|private
specifier|static
name|int
index|[]
name|zzUnpackTrans
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|825
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|zzUnpackTrans
argument_list|(
name|ZZ_TRANS_PACKED_0
argument_list|,
name|offset
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|zzUnpackTrans
specifier|private
specifier|static
name|int
name|zzUnpackTrans
parameter_list|(
name|String
name|packed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|result
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
name|offset
decl_stmt|;
comment|/* index in unpacked array */
name|int
name|l
init|=
name|packed
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|l
condition|)
block|{
name|int
name|count
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|int
name|value
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|value
operator|--
expr_stmt|;
do|do
name|result
index|[
name|j
operator|++
index|]
operator|=
name|value
expr_stmt|;
do|while
condition|(
operator|--
name|count
operator|>
literal|0
condition|)
do|;
block|}
return|return
name|j
return|;
block|}
comment|/* error codes */
DECL|field|ZZ_UNKNOWN_ERROR
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_UNKNOWN_ERROR
init|=
literal|0
decl_stmt|;
DECL|field|ZZ_NO_MATCH
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_NO_MATCH
init|=
literal|1
decl_stmt|;
DECL|field|ZZ_PUSHBACK_2BIG
specifier|private
specifier|static
specifier|final
name|int
name|ZZ_PUSHBACK_2BIG
init|=
literal|2
decl_stmt|;
comment|/* error messages for the codes above */
DECL|field|ZZ_ERROR_MSG
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_ERROR_MSG
index|[]
init|=
block|{
literal|"Unkown internal scanner error"
block|,
literal|"Error: could not match input"
block|,
literal|"Error: pushback value was too large"
block|}
decl_stmt|;
comment|/**    * ZZ_ATTRIBUTE[aState] contains the attributes of state<code>aState</code>    */
DECL|field|ZZ_ATTRIBUTE
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|ZZ_ATTRIBUTE
init|=
name|zzUnpackAttribute
argument_list|()
decl_stmt|;
DECL|field|ZZ_ATTRIBUTE_PACKED_0
specifier|private
specifier|static
specifier|final
name|String
name|ZZ_ATTRIBUTE_PACKED_0
init|=
literal|"\1\0\1\11\4\1\1\11\1\1\6\0\2\1\6\0"
operator|+
literal|"\7\1\2\0\1\1\1\0\16\1\1\0\1\1\2\0"
operator|+
literal|"\2\1\1\0\7\1"
decl_stmt|;
DECL|method|zzUnpackAttribute
specifier|private
specifier|static
name|int
index|[]
name|zzUnpackAttribute
parameter_list|()
block|{
name|int
index|[]
name|result
init|=
operator|new
name|int
index|[
literal|61
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|offset
operator|=
name|zzUnpackAttribute
argument_list|(
name|ZZ_ATTRIBUTE_PACKED_0
argument_list|,
name|offset
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|zzUnpackAttribute
specifier|private
specifier|static
name|int
name|zzUnpackAttribute
parameter_list|(
name|String
name|packed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|result
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
name|offset
decl_stmt|;
comment|/* index in unpacked array */
name|int
name|l
init|=
name|packed
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|l
condition|)
block|{
name|int
name|count
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|int
name|value
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
do|do
name|result
index|[
name|j
operator|++
index|]
operator|=
name|value
expr_stmt|;
do|while
condition|(
operator|--
name|count
operator|>
literal|0
condition|)
do|;
block|}
return|return
name|j
return|;
block|}
comment|/** the input device */
DECL|field|zzReader
specifier|private
name|java
operator|.
name|io
operator|.
name|Reader
name|zzReader
decl_stmt|;
comment|/** the current state of the DFA */
DECL|field|zzState
specifier|private
name|int
name|zzState
decl_stmt|;
comment|/** the current lexical state */
DECL|field|zzLexicalState
specifier|private
name|int
name|zzLexicalState
init|=
name|YYINITIAL
decl_stmt|;
comment|/** this buffer contains the current text to be matched and is       the source of the yytext() string */
DECL|field|zzBuffer
specifier|private
name|char
name|zzBuffer
index|[]
init|=
operator|new
name|char
index|[
name|ZZ_BUFFERSIZE
index|]
decl_stmt|;
comment|/** the textposition at the last accepting state */
DECL|field|zzMarkedPos
specifier|private
name|int
name|zzMarkedPos
decl_stmt|;
comment|/** the textposition at the last state to be included in yytext */
DECL|field|zzPushbackPos
specifier|private
name|int
name|zzPushbackPos
decl_stmt|;
comment|/** the current text position in the buffer */
DECL|field|zzCurrentPos
specifier|private
name|int
name|zzCurrentPos
decl_stmt|;
comment|/** startRead marks the beginning of the yytext() string in the buffer */
DECL|field|zzStartRead
specifier|private
name|int
name|zzStartRead
decl_stmt|;
comment|/** endRead marks the last character in the buffer, that has been read       from input */
DECL|field|zzEndRead
specifier|private
name|int
name|zzEndRead
decl_stmt|;
comment|/** number of newlines encountered up to the start of the matched text */
DECL|field|yyline
specifier|private
name|int
name|yyline
decl_stmt|;
comment|/** the number of characters up to the start of the matched text */
DECL|field|yychar
specifier|private
name|int
name|yychar
decl_stmt|;
comment|/**    * the number of characters from the last newline up to the start of the     * matched text    */
DECL|field|yycolumn
specifier|private
name|int
name|yycolumn
decl_stmt|;
comment|/**     * zzAtBOL == true<=> the scanner is currently at the beginning of a line    */
DECL|field|zzAtBOL
specifier|private
name|boolean
name|zzAtBOL
init|=
literal|true
decl_stmt|;
comment|/** zzAtEOF == true<=> the scanner is at the EOF */
DECL|field|zzAtEOF
specifier|private
name|boolean
name|zzAtEOF
decl_stmt|;
comment|/* user code: */
DECL|field|ALPHANUM
specifier|public
specifier|static
specifier|final
name|int
name|ALPHANUM
init|=
name|StandardTokenizer
operator|.
name|ALPHANUM
decl_stmt|;
DECL|field|APOSTROPHE
specifier|public
specifier|static
specifier|final
name|int
name|APOSTROPHE
init|=
name|StandardTokenizer
operator|.
name|APOSTROPHE
decl_stmt|;
DECL|field|ACRONYM
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM
init|=
name|StandardTokenizer
operator|.
name|ACRONYM
decl_stmt|;
DECL|field|COMPANY
specifier|public
specifier|static
specifier|final
name|int
name|COMPANY
init|=
name|StandardTokenizer
operator|.
name|COMPANY
decl_stmt|;
DECL|field|EMAIL
specifier|public
specifier|static
specifier|final
name|int
name|EMAIL
init|=
name|StandardTokenizer
operator|.
name|EMAIL
decl_stmt|;
DECL|field|HOST
specifier|public
specifier|static
specifier|final
name|int
name|HOST
init|=
name|StandardTokenizer
operator|.
name|HOST
decl_stmt|;
DECL|field|NUM
specifier|public
specifier|static
specifier|final
name|int
name|NUM
init|=
name|StandardTokenizer
operator|.
name|NUM
decl_stmt|;
DECL|field|CJ
specifier|public
specifier|static
specifier|final
name|int
name|CJ
init|=
name|StandardTokenizer
operator|.
name|CJ
decl_stmt|;
comment|/**  * @deprecated this solves a bug where HOSTs that end with '.' are identified  *             as ACRONYMs. It is deprecated and will be removed in the next  *             release.  */
DECL|field|ACRONYM_DEP
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM_DEP
init|=
name|StandardTokenizer
operator|.
name|ACRONYM_DEP
decl_stmt|;
DECL|field|TOKEN_TYPES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TOKEN_TYPES
init|=
name|StandardTokenizer
operator|.
name|TOKEN_TYPES
decl_stmt|;
DECL|method|yychar
specifier|public
specifier|final
name|int
name|yychar
parameter_list|()
block|{
return|return
name|yychar
return|;
block|}
comment|/**  * Fills Lucene token with the current token text.  */
DECL|method|getText
specifier|final
name|void
name|getText
parameter_list|(
name|Token
name|t
parameter_list|)
block|{
name|t
operator|.
name|setTermBuffer
argument_list|(
name|zzBuffer
argument_list|,
name|zzStartRead
argument_list|,
name|zzMarkedPos
operator|-
name|zzStartRead
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new scanner    * There is also a java.io.InputStream version of this constructor.    *    * @param   in  the java.io.Reader to read input from.    */
DECL|method|StandardTokenizerImpl
name|StandardTokenizerImpl
parameter_list|(
name|java
operator|.
name|io
operator|.
name|Reader
name|in
parameter_list|)
block|{
name|this
operator|.
name|zzReader
operator|=
name|in
expr_stmt|;
block|}
comment|/**    * Creates a new scanner.    * There is also java.io.Reader version of this constructor.    *    * @param   in  the java.io.Inputstream to read input from.    */
DECL|method|StandardTokenizerImpl
name|StandardTokenizerImpl
parameter_list|(
name|java
operator|.
name|io
operator|.
name|InputStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Unpacks the compressed character translation table.    *    * @param packed   the packed character translation table    * @return         the unpacked character translation table    */
DECL|method|zzUnpackCMap
specifier|private
specifier|static
name|char
index|[]
name|zzUnpackCMap
parameter_list|(
name|String
name|packed
parameter_list|)
block|{
name|char
index|[]
name|map
init|=
operator|new
name|char
index|[
literal|0x10000
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|/* index in packed string  */
name|int
name|j
init|=
literal|0
decl_stmt|;
comment|/* index in unpacked array */
while|while
condition|(
name|i
operator|<
literal|156
condition|)
block|{
name|int
name|count
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
name|char
name|value
init|=
name|packed
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
do|do
name|map
index|[
name|j
operator|++
index|]
operator|=
name|value
expr_stmt|;
do|while
condition|(
operator|--
name|count
operator|>
literal|0
condition|)
do|;
block|}
return|return
name|map
return|;
block|}
comment|/**    * Refills the input buffer.    *    * @return<code>false</code>, iff there was new input.    *     * @exception   java.io.IOException  if any I/O-Error occurs    */
DECL|method|zzRefill
specifier|private
name|boolean
name|zzRefill
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
comment|/* first: make room (if you can) */
if|if
condition|(
name|zzStartRead
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|zzBuffer
argument_list|,
name|zzStartRead
argument_list|,
name|zzBuffer
argument_list|,
literal|0
argument_list|,
name|zzEndRead
operator|-
name|zzStartRead
argument_list|)
expr_stmt|;
comment|/* translate stored positions */
name|zzEndRead
operator|-=
name|zzStartRead
expr_stmt|;
name|zzCurrentPos
operator|-=
name|zzStartRead
expr_stmt|;
name|zzMarkedPos
operator|-=
name|zzStartRead
expr_stmt|;
name|zzPushbackPos
operator|-=
name|zzStartRead
expr_stmt|;
name|zzStartRead
operator|=
literal|0
expr_stmt|;
block|}
comment|/* is the buffer big enough? */
if|if
condition|(
name|zzCurrentPos
operator|>=
name|zzBuffer
operator|.
name|length
condition|)
block|{
comment|/* if not: blow it up */
name|char
name|newBuffer
index|[]
init|=
operator|new
name|char
index|[
name|zzCurrentPos
operator|*
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|zzBuffer
argument_list|,
literal|0
argument_list|,
name|newBuffer
argument_list|,
literal|0
argument_list|,
name|zzBuffer
operator|.
name|length
argument_list|)
expr_stmt|;
name|zzBuffer
operator|=
name|newBuffer
expr_stmt|;
block|}
comment|/* finally: fill the buffer with new input */
name|int
name|numRead
init|=
name|zzReader
operator|.
name|read
argument_list|(
name|zzBuffer
argument_list|,
name|zzEndRead
argument_list|,
name|zzBuffer
operator|.
name|length
operator|-
name|zzEndRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|numRead
operator|<
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
name|zzEndRead
operator|+=
name|numRead
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Closes the input stream.    */
DECL|method|yyclose
specifier|public
specifier|final
name|void
name|yyclose
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|zzAtEOF
operator|=
literal|true
expr_stmt|;
comment|/* indicate end of file */
name|zzEndRead
operator|=
name|zzStartRead
expr_stmt|;
comment|/* invalidate buffer    */
if|if
condition|(
name|zzReader
operator|!=
literal|null
condition|)
name|zzReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Resets the scanner to read from a new input stream.    * Does not close the old reader.    *    * All internal variables are reset, the old input stream     *<b>cannot</b> be reused (internal buffer is discarded and lost).    * Lexical state is set to<tt>ZZ_INITIAL</tt>.    *    * @param reader   the new input stream     */
DECL|method|yyreset
specifier|public
specifier|final
name|void
name|yyreset
parameter_list|(
name|java
operator|.
name|io
operator|.
name|Reader
name|reader
parameter_list|)
block|{
name|zzReader
operator|=
name|reader
expr_stmt|;
name|zzAtBOL
operator|=
literal|true
expr_stmt|;
name|zzAtEOF
operator|=
literal|false
expr_stmt|;
name|zzEndRead
operator|=
name|zzStartRead
operator|=
literal|0
expr_stmt|;
name|zzCurrentPos
operator|=
name|zzMarkedPos
operator|=
name|zzPushbackPos
operator|=
literal|0
expr_stmt|;
name|yyline
operator|=
name|yychar
operator|=
name|yycolumn
operator|=
literal|0
expr_stmt|;
name|zzLexicalState
operator|=
name|YYINITIAL
expr_stmt|;
block|}
comment|/**    * Returns the current lexical state.    */
DECL|method|yystate
specifier|public
specifier|final
name|int
name|yystate
parameter_list|()
block|{
return|return
name|zzLexicalState
return|;
block|}
comment|/**    * Enters a new lexical state    *    * @param newState the new lexical state    */
DECL|method|yybegin
specifier|public
specifier|final
name|void
name|yybegin
parameter_list|(
name|int
name|newState
parameter_list|)
block|{
name|zzLexicalState
operator|=
name|newState
expr_stmt|;
block|}
comment|/**    * Returns the text matched by the current regular expression.    */
DECL|method|yytext
specifier|public
specifier|final
name|String
name|yytext
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|zzBuffer
argument_list|,
name|zzStartRead
argument_list|,
name|zzMarkedPos
operator|-
name|zzStartRead
argument_list|)
return|;
block|}
comment|/**    * Returns the character at position<tt>pos</tt> from the     * matched text.     *     * It is equivalent to yytext().charAt(pos), but faster    *    * @param pos the position of the character to fetch.     *            A value from 0 to yylength()-1.    *    * @return the character at position pos    */
DECL|method|yycharat
specifier|public
specifier|final
name|char
name|yycharat
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|zzBuffer
index|[
name|zzStartRead
operator|+
name|pos
index|]
return|;
block|}
comment|/**    * Returns the length of the matched text region.    */
DECL|method|yylength
specifier|public
specifier|final
name|int
name|yylength
parameter_list|()
block|{
return|return
name|zzMarkedPos
operator|-
name|zzStartRead
return|;
block|}
comment|/**    * Reports an error that occured while scanning.    *    * In a wellformed scanner (no or only correct usage of     * yypushback(int) and a match-all fallback rule) this method     * will only be called with things that "Can't Possibly Happen".    * If this method is called, something is seriously wrong    * (e.g. a JFlex bug producing a faulty scanner etc.).    *    * Usual syntax/scanner level error handling should be done    * in error fallback rules.    *    * @param   errorCode  the code of the errormessage to display    */
DECL|method|zzScanError
specifier|private
name|void
name|zzScanError
parameter_list|(
name|int
name|errorCode
parameter_list|)
block|{
name|String
name|message
decl_stmt|;
try|try
block|{
name|message
operator|=
name|ZZ_ERROR_MSG
index|[
name|errorCode
index|]
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
name|message
operator|=
name|ZZ_ERROR_MSG
index|[
name|ZZ_UNKNOWN_ERROR
index|]
expr_stmt|;
block|}
throw|throw
operator|new
name|Error
argument_list|(
name|message
argument_list|)
throw|;
block|}
comment|/**    * Pushes the specified amount of characters back into the input stream.    *    * They will be read again by then next call of the scanning method    *    * @param number  the number of characters to be read again.    *                This number must not be greater than yylength()!    */
DECL|method|yypushback
specifier|public
name|void
name|yypushback
parameter_list|(
name|int
name|number
parameter_list|)
block|{
if|if
condition|(
name|number
operator|>
name|yylength
argument_list|()
condition|)
name|zzScanError
argument_list|(
name|ZZ_PUSHBACK_2BIG
argument_list|)
expr_stmt|;
name|zzMarkedPos
operator|-=
name|number
expr_stmt|;
block|}
comment|/**    * Resumes scanning until the next regular expression is matched,    * the end of input is encountered or an I/O-Error occurs.    *    * @return      the next token    * @exception   java.io.IOException  if any I/O-Error occurs    */
DECL|method|getNextToken
specifier|public
name|int
name|getNextToken
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|int
name|zzInput
decl_stmt|;
name|int
name|zzAction
decl_stmt|;
comment|// cached fields:
name|int
name|zzCurrentPosL
decl_stmt|;
name|int
name|zzMarkedPosL
decl_stmt|;
name|int
name|zzEndReadL
init|=
name|zzEndRead
decl_stmt|;
name|char
index|[]
name|zzBufferL
init|=
name|zzBuffer
decl_stmt|;
name|char
index|[]
name|zzCMapL
init|=
name|ZZ_CMAP
decl_stmt|;
name|int
index|[]
name|zzTransL
init|=
name|ZZ_TRANS
decl_stmt|;
name|int
index|[]
name|zzRowMapL
init|=
name|ZZ_ROWMAP
decl_stmt|;
name|int
index|[]
name|zzAttrL
init|=
name|ZZ_ATTRIBUTE
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|zzMarkedPosL
operator|=
name|zzMarkedPos
expr_stmt|;
name|yychar
operator|+=
name|zzMarkedPosL
operator|-
name|zzStartRead
expr_stmt|;
name|zzAction
operator|=
operator|-
literal|1
expr_stmt|;
name|zzCurrentPosL
operator|=
name|zzCurrentPos
operator|=
name|zzStartRead
operator|=
name|zzMarkedPosL
expr_stmt|;
name|zzState
operator|=
name|zzLexicalState
expr_stmt|;
name|zzForAction
label|:
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|zzCurrentPosL
operator|<
name|zzEndReadL
condition|)
name|zzInput
operator|=
name|zzBufferL
index|[
name|zzCurrentPosL
operator|++
index|]
expr_stmt|;
elseif|else
if|if
condition|(
name|zzAtEOF
condition|)
block|{
name|zzInput
operator|=
name|YYEOF
expr_stmt|;
break|break
name|zzForAction
break|;
block|}
else|else
block|{
comment|// store back cached positions
name|zzCurrentPos
operator|=
name|zzCurrentPosL
expr_stmt|;
name|zzMarkedPos
operator|=
name|zzMarkedPosL
expr_stmt|;
name|boolean
name|eof
init|=
name|zzRefill
argument_list|()
decl_stmt|;
comment|// get translated positions and possibly new buffer
name|zzCurrentPosL
operator|=
name|zzCurrentPos
expr_stmt|;
name|zzMarkedPosL
operator|=
name|zzMarkedPos
expr_stmt|;
name|zzBufferL
operator|=
name|zzBuffer
expr_stmt|;
name|zzEndReadL
operator|=
name|zzEndRead
expr_stmt|;
if|if
condition|(
name|eof
condition|)
block|{
name|zzInput
operator|=
name|YYEOF
expr_stmt|;
break|break
name|zzForAction
break|;
block|}
else|else
block|{
name|zzInput
operator|=
name|zzBufferL
index|[
name|zzCurrentPosL
operator|++
index|]
expr_stmt|;
block|}
block|}
name|int
name|zzNext
init|=
name|zzTransL
index|[
name|zzRowMapL
index|[
name|zzState
index|]
operator|+
name|zzCMapL
index|[
name|zzInput
index|]
index|]
decl_stmt|;
if|if
condition|(
name|zzNext
operator|==
operator|-
literal|1
condition|)
break|break
name|zzForAction
break|;
name|zzState
operator|=
name|zzNext
expr_stmt|;
name|int
name|zzAttributes
init|=
name|zzAttrL
index|[
name|zzState
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|zzAttributes
operator|&
literal|1
operator|)
operator|==
literal|1
condition|)
block|{
name|zzAction
operator|=
name|zzState
expr_stmt|;
name|zzMarkedPosL
operator|=
name|zzCurrentPosL
expr_stmt|;
if|if
condition|(
operator|(
name|zzAttributes
operator|&
literal|8
operator|)
operator|==
literal|8
condition|)
break|break
name|zzForAction
break|;
block|}
block|}
block|}
comment|// store back cached position
name|zzMarkedPos
operator|=
name|zzMarkedPosL
expr_stmt|;
switch|switch
condition|(
name|zzAction
operator|<
literal|0
condition|?
name|zzAction
else|:
name|ZZ_ACTION
index|[
name|zzAction
index|]
condition|)
block|{
case|case
literal|5
case|:
block|{
return|return
name|HOST
return|;
block|}
case|case
literal|11
case|:
break|break;
case|case
literal|9
case|:
block|{
return|return
name|ACRONYM_DEP
return|;
block|}
case|case
literal|12
case|:
break|break;
case|case
literal|8
case|:
block|{
return|return
name|ACRONYM
return|;
block|}
case|case
literal|13
case|:
break|break;
case|case
literal|1
case|:
block|{
comment|/* ignore */
block|}
case|case
literal|14
case|:
break|break;
case|case
literal|7
case|:
block|{
return|return
name|NUM
return|;
block|}
case|case
literal|15
case|:
break|break;
case|case
literal|3
case|:
block|{
return|return
name|CJ
return|;
block|}
case|case
literal|16
case|:
break|break;
case|case
literal|2
case|:
block|{
return|return
name|ALPHANUM
return|;
block|}
case|case
literal|17
case|:
break|break;
case|case
literal|6
case|:
block|{
return|return
name|COMPANY
return|;
block|}
case|case
literal|18
case|:
break|break;
case|case
literal|4
case|:
block|{
return|return
name|APOSTROPHE
return|;
block|}
case|case
literal|19
case|:
break|break;
case|case
literal|10
case|:
block|{
return|return
name|EMAIL
return|;
block|}
case|case
literal|20
case|:
break|break;
default|default:
if|if
condition|(
name|zzInput
operator|==
name|YYEOF
operator|&&
name|zzStartRead
operator|==
name|zzCurrentPos
condition|)
block|{
name|zzAtEOF
operator|=
literal|true
expr_stmt|;
return|return
name|YYEOF
return|;
block|}
else|else
block|{
name|zzScanError
argument_list|(
name|ZZ_NO_MATCH
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
end_unit
